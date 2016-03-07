/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.models.apis;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import tsuyogoro.sugorokuon.models.entities.OnAirSong;

public class OnAirSongsApi {

    private OnAirSongDbOpenHelper mOpenHelper;

    public OnAirSongsApi(Context context) {
        mOpenHelper = new OnAirSongDbOpenHelper(context);
    }

    /**
     * 曲情報を１つ追加。
     *
     * @param song 追加したい曲情報
     * @return 追加されたRowのID。失敗した場合は-1を返す。
     */
    public long insert(OnAirSong song) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long id = doInsert(song, db);

        db.close();
        return id;
    }

    /**
     * 複数の曲情報を追加。
     * 被った場合はDBへの登録は弾かれる (Unique) ので、気にせず渡してよし。
     *
     * @param songs 追加したい曲情報のリスト
     * @return 追加された (追加するのが成功した) RowのIDの配列。
     */
    public List<Long> insert(List<OnAirSong> songs) {
        List<Long> ids = new ArrayList<Long>();
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        for (OnAirSong song : songs) {
            long id = doInsert(song, db);
            if (0 < id) {
                ids.add(id);
            }
        }

        db.close();
        return ids;
    }

    /**
     * 一日分、指定の局のonAir曲を検索
     *
     * @param year
     * @param month     2月なら2 (コードで扱う際の-1は不要)
     * @param date
     * @param stationId
     * @return 見つかった曲情報のリスト。onAir順にソートされている。nullは返さない。
     */
    public List<OnAirSong> search(int year, int month, int date, String stationId) {
        Calendar from = beginOfDay(year, month, date);
        Calendar to = endOfDay(year, month, date);

        String selection = OnAirSongTableDefiner.OnAirSongsTableColumn.STATION_ID.name + "=? AND " +
                "(" + OnAirSongTableDefiner.OnAirSongsTableColumn.DATE.name + " BETWEEN ? AND ? )";
        String[] selectionArgs = new String[]{
                stationId, Long.toString(from.getTimeInMillis()), Long.toString(to.getTimeInMillis())
        };

        return doSearch(selection, selectionArgs);
    }

    /**
     * 一日分、局関係なくonAir曲を検索
     *
     * @param year
     * @param month 2月なら2 (コードで扱う際の-1は不要)
     * @param date
     * @return 見つかった曲情報のリスト。onAir順にソートされている。nullは返さない。
     */
    public List<OnAirSong> search(int year, int month, int date) {
        Calendar from = beginOfDay(year, month, date);
        Calendar to = endOfDay(year, month, date);

        String selection = OnAirSongTableDefiner.OnAirSongsTableColumn.DATE.name + " BETWEEN ? AND ? ";

        String[] selectionArgs = new String[]{
                Long.toString(from.getTimeInMillis()), Long.toString(to.getTimeInMillis())
        };

        return doSearch(selection, selectionArgs);
    }

    /**
     * Artist名で検索
     *
     * @param artist
     * @return 見つかった曲情報のリスト。onAir順にソートされている。nullは返さない。
     */
    public List<OnAirSong> search(String artist) {
        String selection = OnAirSongTableDefiner.OnAirSongsTableColumn.ARTIST.name + "=?";
        String[] selectionArgs = new String[]{artist};

        return doSearch(selection, selectionArgs);
    }

    /**
     * Artist名でfromからtoの間にかかった曲を検索
     *
     * @param artist
     * @return 見つかった曲情報のリスト。onAir順にソートされている。nullは返さない。
     */
    public List<OnAirSong> search(String artist, Calendar from, Calendar to) {
        // Fail safe
        from.set(Calendar.MILLISECOND, 0);
        to.set(Calendar.MILLISECOND, 0);

        String selection = OnAirSongTableDefiner.OnAirSongsTableColumn.ARTIST.name + "=? AND (" +
                OnAirSongTableDefiner.OnAirSongsTableColumn.DATE.name + " BETWEEN ? AND ?)";

        String[] selectionArgs = new String[]{
                artist, Long.toString(from.getTimeInMillis()), Long.toString(to.getTimeInMillis())
        };

        return doSearch(selection, selectionArgs);
    }

    /**
     * from から to の間にかかった曲を検索する
     *
     * @param from
     * @param to
     * @return 見つかった曲情報のリスト。onAir順にソートされている。nullは返さない。
     */
    public List<OnAirSong> search(Calendar from, Calendar to) {
        // Fail safe
        from.set(Calendar.MILLISECOND, 0);
        to.set(Calendar.MILLISECOND, 0);

        String selection = OnAirSongTableDefiner.OnAirSongsTableColumn.DATE.name + " BETWEEN ? AND ? ";

        String[] selectionArgs = new String[]{
                Long.toString(from.getTimeInMillis()), Long.toString(to.getTimeInMillis())
        };

        return doSearch(selection, selectionArgs);
    }

    /**
     * 一定の期間内の、指定した局でonAirされた曲を返す
     *
     * @param from
     * @param to
     * @param stationId
     * @return 見つかった曲情報のリスト。onAir順にソートされている。nullは返さない。
     */
    public List<OnAirSong> search(Calendar from, Calendar to, String stationId) {
        // Fail safe
        from.set(Calendar.MILLISECOND, 0);
        to.set(Calendar.MILLISECOND, 0);

        String selection = OnAirSongTableDefiner.OnAirSongsTableColumn.STATION_ID.name + "=? AND " +
                OnAirSongTableDefiner.OnAirSongsTableColumn.DATE.name + " BETWEEN ? AND ? ";

        String[] selectionArgs = new String[]{
                stationId, Long.toString(from.getTimeInMillis()), Long.toString(to.getTimeInMillis())
        };

        return doSearch(selection, selectionArgs);
    }

    /**
     * 全ての曲を検索
     *
     * @return
     */
    public List<OnAirSong> searchAll() {
        return doSearch(null, null);
    }

    /**
     * 一日分、指定の局のonAir曲を削除。
     *
     * @param year
     * @param month
     * @param date
     * @param stationId
     * @return 削除したRowの数
     */
    public int delete(int year, int month, int date, String stationId) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        Calendar from = beginOfDay(year, month, date);
        Calendar to = endOfDay(year, month, date);

        String whereClause = OnAirSongTableDefiner.OnAirSongsTableColumn.STATION_ID.name + "=? AND " +
                "(" + OnAirSongTableDefiner.OnAirSongsTableColumn.DATE.name + " BETWEEN ? AND ? )";
        String[] whereClauseArgs = new String[]{
                stationId, Long.toString(from.getTimeInMillis()), Long.toString(to.getTimeInMillis())
        };

        return db.delete(OnAirSongTableDefiner.TABLE_NAME, whereClause, whereClauseArgs);
    }

    /**
     * 一日分、局関係なくonAir曲を削除。
     *
     * @param year
     * @param month
     * @param date
     * @return 問題なければtrue
     */
    public int delete(int year, int month, int date) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        Calendar from = beginOfDay(year, month, date);
        Calendar to = endOfDay(year, month, date);

        String whereClause = OnAirSongTableDefiner.OnAirSongsTableColumn.DATE.name + " BETWEEN ? AND ?";
        String[] whereClauseArgs = new String[]{
                Long.toString(from.getTimeInMillis()), Long.toString(to.getTimeInMillis())
        };

        return db.delete(OnAirSongTableDefiner.TABLE_NAME, whereClause, whereClauseArgs);
    }

    /**
     * Databaseをclear
     *
     * @return
     */
    public int clear() {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        return db.delete(OnAirSongTableDefiner.TABLE_NAME, null, null);
    }

    /**
     * songの内容に沿ってupdateをかける
     *
     * @param song
     * @return UpdateされたRowの数
     */
    public int update(OnAirSong song) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        ContentValues cv = createContentValues(song);
        String whereClause = OnAirSongTableDefiner.OnAirSongsTableColumn.STATION_ID.name + "=? AND "
                + OnAirSongTableDefiner.OnAirSongsTableColumn.DATE.name + "=?";
        String[] whereClauseArgs = new String[]{
                song.stationId, Long.toString(song.date.getTimeInMillis())
        };

        return db.update(OnAirSongTableDefiner.TABLE_NAME, cv, whereClause, whereClauseArgs);
    }

    int deleteAll() {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        return db.delete(OnAirSongTableDefiner.TABLE_NAME, null, null);
    }

    private Calendar beginOfDay(int year, int month, int date) {
        Calendar from = Calendar.getInstance();
        from.set(year, month, date, 0, 0, 0);
        return from;
    }

    private Calendar endOfDay(int year, int month, int date) {
        Calendar to = Calendar.getInstance();
        to.set(year, month, date, 23, 59, 59);
        return to;
    }

    private long doInsert(OnAirSong song, SQLiteDatabase writableDb) {
        ContentValues cv = createContentValues(song);
        // insertは失敗すると-1を返す
        return writableDb.insert(OnAirSongTableDefiner.TABLE_NAME, null, cv);
    }

    private ContentValues createContentValues(OnAirSong song) {
        ContentValues cv = new ContentValues();
        cv.put(OnAirSongTableDefiner.OnAirSongsTableColumn.STATION_ID.name, song.stationId);
        cv.put(OnAirSongTableDefiner.OnAirSongsTableColumn.DATE.name, song.date.getTimeInMillis());
        cv.put(OnAirSongTableDefiner.OnAirSongsTableColumn.ARTIST.name, song.artist);
        cv.put(OnAirSongTableDefiner.OnAirSongsTableColumn.TITLE.name, song.title);
        cv.put(OnAirSongTableDefiner.OnAirSongsTableColumn.IMAGE.name, song.imageUrl);
        return cv;
    }

    private List<OnAirSong> doSearch(String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        String[] requestColumns = new String[]{
                OnAirSongTableDefiner.OnAirSongsTableColumn.STATION_ID.name,
                OnAirSongTableDefiner.OnAirSongsTableColumn.DATE.name,
                OnAirSongTableDefiner.OnAirSongsTableColumn.TITLE.name,
                OnAirSongTableDefiner.OnAirSongsTableColumn.ARTIST.name,
                OnAirSongTableDefiner.OnAirSongsTableColumn.IMAGE.name
        };
        Cursor c = db.query(OnAirSongTableDefiner.TABLE_NAME,
                requestColumns, selection, selectionArgs, null, null,
                OnAirSongTableDefiner.OnAirSongsTableColumn.DATE.name);

        List<OnAirSong> songs = new ArrayList<OnAirSong>();

        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            songs.add(OnAirSongTableDefiner.createData(c));
            c.moveToNext();
        }
        c.close();
        db.close();

        return songs;
    }

}
