/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.models.apis;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import tsuyogoro.sugorokuon.models.entities.OnedayTimetable;
import tsuyogoro.sugorokuon.models.entities.Program;
import tsuyogoro.sugorokuon.utils.SugorokuonLog;

/**
 * 番組情報を格納したDBへのAccessor。
 * <p/>
 * (メモ)
 * http://d.hatena.ne.jp/ukiki999/20100524/p1 によると、
 * DBのcloseはCursolに対してだけ呼ぶのが好ましいらしい。
 *
 * @author Tsuyoyo
 */
public class TimeTableApi {

    private ProgramDbOpenHelper mOpenHelper;

    public TimeTableApi(Context context) {
        mOpenHelper = new ProgramDbOpenHelper(context);
    }

    /**
     * 番組データを一つDBに追加
     *
     * @param program
     * @return 失敗すると-1 (SQLiteDatabase#insertのfailの値)
     */
    public long insert(Program program) {
        List<Program> p = new ArrayList<Program>();
        p.add(program);
        return doInsert(p)[0];
    }

    /**
     * 1日分のProgramデータをDBに格納する。
     *
     * @param timeTable 1日分のタイムテーブル
     * @return insertされたprogramのIDリスト
     */
    public long[] insert(OnedayTimetable timeTable) {
        return doInsert(timeTable.programs);
    }

    /**
     * 複数のtimetableをDBに格納する
     *
     * @param timeTables
     * @return insertされたprogramのIDリスト
     */
    public long[] insert(List<OnedayTimetable> timeTables) {

        List<Program> programs = new ArrayList<Program>();
        for (OnedayTimetable t : timeTables) {
            programs.addAll(t.programs);
        }

        return doInsert(programs);
    }

    private long[] doInsert(List<Program> programs) {
        long[] ids = new long[programs.size()];

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final SQLiteStatement statement =
                    ProgramTableDefiner.compileSQLiteStatementForInsert(db);

            for (int i = 0; i < programs.size(); ++i) {
                try {
                    ProgramTableDefiner.bindSQLiteStatementForInsert(statement, programs.get(i));
                    ids[i] = statement.executeInsert();
                } catch (SQLiteConstraintException e) {
                    // (Memo) API Specと違うんだが、なぜかこのException
                    ids[i] = -1;
                    SugorokuonLog.w("Failed to insert : " + e.getMessage());
                }
            }
            statement.close();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }

        return ids;
    }

    /**
     * 日付とラジオ局指定で、番組表をdeleteする
     *
     * @param year
     * @param month     -1不要 (2月なら2)
     * @param date
     * @param stationId
     * @return
     */
    public int delete(int year, int month, int date, String stationId) {
        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();

        from.set(year, month - 1, date, 0, 0, 0);
        to.set(year, month - 1, date, 0, 0, 0);
        to.add(Calendar.DATE, 1);

        from.set(Calendar.MILLISECOND, 0);
        to.set(Calendar.MILLISECOND, 0);

        String whereClause = "(" + ProgramTableDefiner.ProgramTableColumn.START_TIME.columnName() + " BETWEEN ? AND ?) AND " +
                ProgramTableDefiner.ProgramTableColumn.STATION_ID.columnName() + "=?";

        String[] whereArgs = new String[]{Long.toString(from.getTimeInMillis()),
                Long.toString(to.getTimeInMillis()), stationId};

        return doDelete(whereClause, whereArgs);
    }

    /**
     * Programデータが格納されているDBを削除する（番組情報更新の際に使う）。
     */
    public int clear() {
        return doDelete(null, null);
    }

    /**
     * 番組データを更新する。
     * パラメータのprogramのstartTimeとstationIdを見て、
     * それに該当する番組を更新。
     *
     * @param program
     * @return 更新されたrowの数
     */
    public int update(Program program) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int updatedNum = doUpdate(program, db);
        db.close();
        return updatedNum;
    }

    /**
     * 一日分の番組データを更新
     *
     * @param timeTable
     * @return 更新されたrowの数
     */
    public int update(OnedayTimetable timeTable) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int updatedNum = 0;
        for (Program p : timeTable.programs) {
            updatedNum += doUpdate(p, db);
        }
        db.close();
        return updatedNum;
    }

    /**
     * 一日分の番組データをまとめて更新
     *
     * @param timeTables
     * @return 更新されたrowの数
     */
    public int update(List<OnedayTimetable> timeTables) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int updatedNum = 0;
        for (OnedayTimetable t : timeTables) {
            for (Program p : t.programs) {
                updatedNum += doUpdate(p, db);
            }
        }
        db.close();
        return updatedNum;
    }

    private int doUpdate(Program program, SQLiteDatabase db) {
        ContentValues cv = contentValuesByProgram(program);

        String whereClause = ProgramTableDefiner.ProgramTableColumn.STATION_ID.columnName() + "=?" +
                " AND " +
                ProgramTableDefiner.ProgramTableColumn.START_TIME.columnName() + "=?";

        String[] whereArgs = new String[]{program.stationId,
                Long.toString(program.startTime.getTimeInMillis())};

        int updatedNum = db.update(ProgramTableDefiner.TABLE_NAME, cv, whereClause, whereArgs);

        if (1 != updatedNum) {
            SugorokuonLog.w("Warning at update program : " +
                    program.startTime.toString() + " : " + program.title +
                    " - " + updatedNum + " rows updated");
        }
        return updatedNum;
    }

    /**
     * 1日分の番組データをDBから取る。
     *
     * @param year
     * @param month     -1不要 (2月なら2)
     * @param date
     * @param stationId
     * @return nullは返らない (中身が空っぽということはある)
     */
    public OnedayTimetable fetchTimetable(int year, int month, int date, String stationId) {

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        String selection = ProgramTableDefiner.ProgramTableColumn.STATION_ID.columnName() + "=? AND " +
                "(" + ProgramTableDefiner.ProgramTableColumn.START_TIME.columnName() + " BETWEEN ? AND ? )";

        Calendar from = beginOfDay(year, month, date);
        Calendar to = endOfDay(year, month, date);

        String selectionArgs[] = {stationId,
                Long.toString(from.getTimeInMillis()),
                Long.toString(to.getTimeInMillis())};

        Cursor c = db.query(ProgramTableDefiner.TABLE_NAME,
                ProgramTableDefiner.allColumnNames(), selection,
                selectionArgs, null, null, ProgramTableDefiner.ProgramTableColumn.START_TIME.columnName());

        OnedayTimetable timeTable = new OnedayTimetable(year, month, date, stationId);

        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            timeTable.programs.add(ProgramTableDefiner.createData(c));
            c.moveToNext();
        }

        c.close();
        db.close();

        return timeTable;
    }

    /**
     * 新しいRecommendのfilterを渡して、DBのオススメかどうかのflagを更新していく。
     *
     * @return 新たにrecommendフラグが立ったrowの数
     */
    public int updateRecommends(ProgramSearchFilter filter) {

        // まず、全てのオススメフラグをfalseに倒す
        resetRecommend();

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        // キーワードにひっかかる番組のオススメフラグを立てる
        ContentValues recommendFlagCv = new ContentValues();
        recommendFlagCv.put(ProgramTableDefiner.ProgramTableColumn.ISRECOMMEND.columnName(), true);

        ProgramSearchFilter.Condition condition = filter.getCondition();

        int updatedNum = db.update(ProgramTableDefiner.TABLE_NAME, recommendFlagCv,
                condition.where, condition.whereArgs);

        db.close();

        return updatedNum;
    }

    /**
     * 全ての番組のオススメフラグをリセット
     *
     * @return 更新した番組数
     */
    public int resetRecommend() {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        ContentValues resetRecommendFlagCv = new ContentValues();
        resetRecommendFlagCv.put(ProgramTableDefiner.ProgramTableColumn.ISRECOMMEND.columnName(), false);

        int res = db.update(ProgramTableDefiner.TABLE_NAME, resetRecommendFlagCv, null, null);
        db.close();

        return res;
    }

    /**
     * DBに登録されている、全てのオススメの番組を取得
     * 放送開始時刻順にソートされて返る
     *
     * @return 何もなかった場合も空のlistが返る
     */
    public List<Program> fetchRecommends() {
        String where = ProgramTableDefiner.ProgramTableColumn.ISRECOMMEND.columnName() + "= ?";
        return doQuery(where, new String[]{"1"});
    }

    /**
     * 指定時間以降の、DBに登録されているオススメの番組を取得
     * 放送開始時刻順にソートされて返る
     *
     * @param after
     * @return 何もなかった場合も空のlistが返る
     */
    public List<Program> fetchRecommends(Calendar after) {
        String where = "(" + ProgramTableDefiner.ProgramTableColumn.ISRECOMMEND.columnName() + "= ? AND " +
                ProgramTableDefiner.ProgramTableColumn.START_TIME.columnName() + " > ?)";
        return doQuery(where, new String[]{"1", Long.toString(after.getTimeInMillis())});
    }

    /**
     * filterにしたがって番組をsearchする
     * 放送開始時刻順にソートされて返る
     *
     * @param filter
     * @return searchに引っかかった番組を返す (結果がない場合はsize 0のlist)
     */
    public List<Program> search(ProgramSearchFilter filter) {
        // Memo : もしパフォーマンスで行き詰まったら↓も検討
        // (http://qiita.com/shikato/items/512db7bf051eddb84600)
        ProgramSearchFilter.Condition condition = filter.getCondition();
        return doQuery(condition.where, condition.whereArgs);
    }

    private List<Program> doQuery(String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        List<Program> programs = new ArrayList<Program>();

        Cursor c = db.query(ProgramTableDefiner.TABLE_NAME,
                ProgramTableDefiner.allColumnNames(),
                where, whereArgs, null, null,
                ProgramTableDefiner.ProgramTableColumn.START_TIME.columnName()); // 放送開始順にソート。

        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            programs.add(ProgramTableDefiner.createData(c));
            c.moveToNext();
        }
        c.close();
        db.close();

        return programs;
    }

    private ContentValues contentValuesByProgram(Program program) {
        ContentValues cv = new ContentValues();

        cv.put(ProgramTableDefiner.ProgramTableColumn.STATION_ID.columnName(), program.stationId);
        cv.put(ProgramTableDefiner.ProgramTableColumn.START_TIME.columnName(), program.startTime.getTimeInMillis());
        cv.put(ProgramTableDefiner.ProgramTableColumn.END_TIME.columnName(), program.endTime.getTimeInMillis());
        cv.put(ProgramTableDefiner.ProgramTableColumn.TITLE.columnName(), program.title);
        cv.put(ProgramTableDefiner.ProgramTableColumn.SUBTITLE.columnName(), program.subtitle);
        cv.put(ProgramTableDefiner.ProgramTableColumn.PERSONALITIES.columnName(), program.personalities);
        cv.put(ProgramTableDefiner.ProgramTableColumn.DESCRIPTION.columnName(), program.description);
        cv.put(ProgramTableDefiner.ProgramTableColumn.INFO.columnName(), program.info); //mInfoCacheMgr.createInfoCache(p));
        cv.put(ProgramTableDefiner.ProgramTableColumn.URL.columnName(), program.url);

        cv.put(ProgramTableDefiner.ProgramTableColumn.ISRECOMMEND.columnName(), program.recommend());

        return cv;
    }

    private Calendar beginOfDay(int year, int month, int date) {
        Calendar from = Calendar.getInstance();
        from.set(year, month - 1, date, 5, 0, 0);
        from.set(Calendar.MILLISECOND, 0);
        return from;
    }

    private Calendar endOfDay(int year, int month, int date) {
        Calendar to = Calendar.getInstance();
        to.set(year, month - 1, date, 4, 59, 59);
        to.add(Calendar.DATE, 1);
        to.set(Calendar.MILLISECOND, 0);
        return to;
    }

    private int doDelete(String whereClause, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rows = db.delete(ProgramTableDefiner.TABLE_NAME, whereClause, whereArgs);

        db.close();
        return rows;
    }

}
