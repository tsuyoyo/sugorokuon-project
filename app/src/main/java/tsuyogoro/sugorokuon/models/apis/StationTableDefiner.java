/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.models.apis;

import android.database.Cursor;

import tsuyogoro.sugorokuon.models.entities.Station;

class StationTableDefiner extends BaseTableDefiner {

    public static final String TABLE_NAME = "stationtable";

    public enum StationTableColumn implements ColumnEnumBase {

        // Station ID
        ID("id", "TEXT PRIMARY KEY"),

        // Station Type (e.g. "radiko", "NHK"...etc)
        TYPE("type", "TEXT"),

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
        LOGO_CACHE("logo_cache", "TEXT"),

        // FeedがonAirされた曲情報を提供しているかどうか (1:true, 0:false)
        ON_AIR_SONGS_AVALIABLE("on_air_songs", "INTEGER"),
        ;

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
        return StationTableColumn.values();
    }

    /**
     * CursorからStationデータインスタンスを作る
     *
     * @param c
     * @return
     */
    public static Station createData(Cursor c) {
        Station.Builder builder = new Station.Builder();

        builder.id = c.getString(c.getColumnIndex(StationTableColumn.ID.name));
        builder.type = c.getString(c.getColumnIndex(StationTableColumn.TYPE.name));
        builder.name = c.getString(c.getColumnIndex(StationTableColumn.NAME.name));
        builder.ascii_name = c.getString(c.getColumnIndex(StationTableColumn.ASCII_NAME.name));
        builder.siteUrl = c.getString(c.getColumnIndex(StationTableColumn.SITE_URL.name));
        builder.logoUrl = c.getString(c.getColumnIndex(StationTableColumn.LOGO_URL.name));
        builder.bannerUrl = c.getString(c.getColumnIndex(StationTableColumn.BANNER_URL.name));
        builder.logoCachePath = c.getString(c.getColumnIndex(StationTableColumn.LOGO_CACHE.name));

        return builder.create();
    }

    @Override
    protected String uniqueSql() {
        // 同じIDの局エントリーは許可しない
        return "UNIQUE (" + StationTableColumn.ID.name +  ")";
    }

}
