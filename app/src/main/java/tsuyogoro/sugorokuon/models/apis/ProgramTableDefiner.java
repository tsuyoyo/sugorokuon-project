/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.models.apis;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.Calendar;
import java.util.Date;

import tsuyogoro.sugorokuon.models.entities.Program;

class ProgramTableDefiner extends BaseTableDefiner {

    public static final String TABLE_NAME = "programtable";

    public enum ProgramTableColumn implements ColumnEnumBase {

        // Station ID
        STATION_ID("stationid", "TEXT"),

        // 番組の開始時刻。UnixTimeミリ秒
        // NOTE (2015/2/15) :
        // 1.x系はyyyymmddhhmmssのフォーマットだったが、2.x系からUnix時刻で管理するようにする。
        // (TEXT -> INTEGERへ変更)
        START_TIME("starttime", "INTEGER"),

        // 番組の終了時刻。（同上）
        END_TIME("endtime", "INTEGER"),

        // 番組のタイトル。
        TITLE("title", "TEXT"),

        // 番組のサブタイトル。
        SUBTITLE("subtitle", "TEXT"),

        // パーソナリティ。
        PERSONALITIES("personalities", "TEXT"),

        // description（空っぽのことが多い）。
        DESCRIPTION("description", "TEXT"),

        // html形式の番組info。
        INFO("info", "TEXT"),

        // 番組サイトのURL
        URL("url", "TEXT"),

        // Recommendかどうか (0:false, 1:true)
        ISRECOMMEND("isrecommend", "INTEGER");

        private String name;

        private String type;

        @Override
        public String columnName() {
            return name;
        }

        @Override
        public String columnType() {
            return type;
        }

        ProgramTableColumn(String name, String type) {
            this.name = name;
            this.type = type;
        }

    }

    @Override
    protected ColumnEnumBase[] getFields() {
        return ProgramTableColumn.values();
    }

    /**
     * CursorからProgramインスタンスを復元
     *
     * @param c
     * @return
     */
    public static Program createData(Cursor c) {
        Program.Builder builder = new Program.Builder();

        builder.stationId = c.getString(c.getColumnIndex(ProgramTableColumn.STATION_ID.name));
        builder.title = c.getString(c.getColumnIndex(ProgramTableColumn.TITLE.name));
        builder.subtitle = c.getString(c.getColumnIndex(ProgramTableColumn.SUBTITLE.name));
        builder.personalities = c.getString(c.getColumnIndex(ProgramTableColumn.PERSONALITIES.name));
        builder.description = c.getString(c.getColumnIndex(ProgramTableColumn.DESCRIPTION.name));
        builder.info = c.getString(c.getColumnIndex(ProgramTableColumn.INFO.name)); //mInfoCacheMgr.readInfoCache(c.getString(7));
        builder.url = c.getString(c.getColumnIndex(ProgramTableColumn.URL.name));

        long startTimeMilli = c.getLong(c.getColumnIndex(ProgramTableColumn.START_TIME.name));
        long endTimeMilli = c.getLong(c.getColumnIndex(ProgramTableColumn.END_TIME.name));
        builder.startTime = createCalendar(startTimeMilli);
        builder.endTime = createCalendar(endTimeMilli);

        return builder.create();
    }

    static SQLiteStatement compileSQLiteStatementForInsert(SQLiteDatabase db) {
        String value = "(";
        for (int i=0; i<ProgramTableColumn.values().length; i++) {
            value += "?";
            if (i < ProgramTableColumn.values().length - 1) {
                value += ",";
            }
        }
        value += ")";

        return db.compileStatement("INSERT INTO "
                + ProgramTableDefiner.TABLE_NAME + " VALUES " + value);
    }

    static void bindSQLiteStatementForInsert(SQLiteStatement statement, Program p) {
        statement.bindString(
                ProgramTableColumn.STATION_ID.ordinal() + 1, p.stationId);
        statement.bindLong(
                ProgramTableColumn.START_TIME.ordinal() + 1, p.startTime.getTimeInMillis());
        statement.bindLong(
                ProgramTableColumn.END_TIME.ordinal() + 1, p.endTime.getTimeInMillis());
        statement.bindString(
                ProgramTableColumn.TITLE.ordinal() + 1, (p.title != null) ? p.title : "");
        statement.bindString(
                ProgramTableColumn.SUBTITLE.ordinal() + 1, (p.subtitle != null) ? p.subtitle : "");
        statement.bindString(
                ProgramTableColumn.PERSONALITIES.ordinal() + 1, (p.personalities != null) ? p.personalities : "");
        statement.bindString(
                ProgramTableColumn.DESCRIPTION.ordinal() + 1, (p.description != null) ? p.description : "");
        statement.bindString(
                ProgramTableColumn.INFO.ordinal() + 1, (p.info != null) ? p.info : "");
        statement.bindString(
                ProgramTableColumn.URL.ordinal() + 1, (p.url != null) ? p.url : "");
        statement.bindLong(
                ProgramTableColumn.ISRECOMMEND.ordinal() + 1, (p.recommend()) ? 1 : 0);
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected String primaryKeySql() {
        // StationIdとstart timeがあれば、番組を一意に特定できるのでprimary key。
        return "PRIMARY KEY (" + ProgramTableColumn.STATION_ID.name
                + "," + ProgramTableColumn.START_TIME.name + ")";
    }

    @Override
    protected String uniqueSql() {
        // これらが同じ値のエントリーは許可しない (同じ番組の二重登録)
        return "UNIQUE (" + ProgramTableColumn.STATION_ID.name + "," +
                ProgramTableColumn.START_TIME.name + ")";
    }

    /**
     * メモ : 全columnを返すメソッドが元々あったのでとりあえず。
     *
     * @return
     */
    public static String[] allColumnNames() {
        String[] names = new String[ProgramTableColumn.values().length];
        for (int i = 0; i < ProgramTableColumn.values().length; i++) {
            names[i] = ProgramTableColumn.values()[i].name;
        }
        return names;
    }

    private static Calendar createCalendar(long milliSeconds) {
        Date d = new Date(milliSeconds);
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return c;
    }

}
