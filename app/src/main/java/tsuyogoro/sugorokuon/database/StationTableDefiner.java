/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.database;

import android.database.Cursor;

import tsuyogoro.sugorokuon.datatype.Station;

class StationTableDefiner extends TableDefinerBase {

    public static final String TABLE_NAME = "stationtable";

    public enum StationTableColumn implements ColumnEnumBase {

        // Station ID
        ID("id", "TEXT PRIMARY KEY"),

        // Station name(例：文化放送)
        NAME("name", "TEXT"),

        // ASCII name (例：BUNKA_HOSO）
        ASCII_NAME("ascii_name", "TEXT"),

        // 局のSite URL
        SITE_URL("site_url", "TEXT"),

        // 局のLogoのURL
        LOGO_URL("logo_url", "TEXT"),

        // 局のBannerのURL
        BANNER_URL("banner_url", "TEXT"),

        // Logoのlocalキャッシュのファイルパス
        LOGO_CACHE("logo_cache", "TEXT");

        public String name;

        public String type;

        @Override
        public String columnName() {
            return name;
        }

        @Override
        public String columnType() {
            return type;
        }

        StationTableColumn(String name, String type) {
            this.name = name;
            this.type = type;
        }

    }

    /**
     * メモ : 全columnを返すメソッドが元々あったのでとりあえず。
     *
     * @return
     */
    public static String[] allColumnNames() {
        String[] names = new String[StationTableColumn.values().length];
        for (int i=0; i<StationTableColumn.values().length; i++) {
            names[i] = StationTableColumn.values()[i].name;
        }
        return names;
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected ColumnEnumBase[] getFields() {

        ColumnEnumBase[] columns = new ColumnEnumBase[StationTableColumn.values().length];

        // createDataの処理と、この並びの順番が依存しているので注意
        columns[0] = StationTableColumn.ID;
        columns[1] = StationTableColumn.NAME;
        columns[2] = StationTableColumn.ASCII_NAME;
        columns[3] = StationTableColumn.SITE_URL;
        columns[4] = StationTableColumn.LOGO_URL;
        columns[5] = StationTableColumn.BANNER_URL;
        columns[6] = StationTableColumn.LOGO_CACHE;

        return columns;
    }

    /**
     * CursorからStationデータインスタンスを作る
     *
     * @param c
     * @return
     */
    public static Station createData(Cursor c) {
        Station.Builder builder = new Station.Builder();

        builder.id = c.getString(0);
        builder.name = c.getString(1);
        builder.ascii_name = c.getString(2);
        builder.siteUrl = c.getString(3);
        builder.logoUrl = c.getString(4);
        builder.bannerUrl = c.getString(5);
        builder.logoCachePath = c.getString(6);

        return builder.create();
    }

}
