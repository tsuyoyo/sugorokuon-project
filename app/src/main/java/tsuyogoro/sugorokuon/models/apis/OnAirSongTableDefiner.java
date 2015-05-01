/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.models.apis;

import android.database.Cursor;

import java.util.Calendar;
import java.util.Date;

import tsuyogoro.sugorokuon.models.entities.OnAirSong;

class OnAirSongTableDefiner extends BaseTableDefiner {

    public static final String TABLE_NAME = "onairsongtable";

    public enum OnAirSongsTableColumn implements ColumnEnumBase {

        // onAirSongを取り出す例
        // http://radiko.jp/v2/station/feed_PC/FMT.xml

        // あと、全角アルファベットを半角に直す
        // http://www7a.biglobe.ne.jp/~java-master/samples/string/ZenkakuAlphabetToHankakuAlphabet.html

        // OnAirされた局
        STATION_ID("stationid", "TEXT"),

        // OnAirされた時刻 (UnixTimeミリ秒)
        DATE("date", "INTEGER"),

        TITLE("title", "TEXT"),

        ARTIST("artist", "TEXT"),

        // feedの中の "itemid" attribute。用途がよくわからないけど一応。
        ITEMID("itemid", "TEXT"),

        ;

        OnAirSongsTableColumn(String name, String type) {
            this.name = name;
            this.type = type;
        }

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
    }

    @Override
    protected ColumnEnumBase[] getFields() {
        return OnAirSongsTableColumn.values();
    }

    /**
     * CursorからOnAirSongインスタンスを復元
     *
     * @param
     * @return
     */
    public static OnAirSong createData(Cursor c) {

        String stationId = c.getString(c.getColumnIndex(OnAirSongsTableColumn.STATION_ID.name));
        String artist = c.getString(c.getColumnIndex(OnAirSongsTableColumn.ARTIST.name));
        String title = c.getString(c.getColumnIndex(OnAirSongsTableColumn.TITLE.name));
        String itemId = c.getString(c.getColumnIndex(OnAirSongsTableColumn.ITEMID.name));

        // UnixTimeのOnAir日時をCalendarクラスへ
        long dateMilliSec = c.getLong(c.getColumnIndex(OnAirSongsTableColumn.DATE.name));
        Calendar date = Calendar.getInstance();
        date.setTime(new Date(dateMilliSec));

        return new OnAirSong(stationId, artist, title, date, itemId);
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected String uniqueSql() {
        // これらが同じ値のエントリーは許可しない (同じ曲の二重登録)
        return "UNIQUE (" + OnAirSongsTableColumn.STATION_ID.name + "," +
                OnAirSongsTableColumn.DATE.name + ")";
    }

}
