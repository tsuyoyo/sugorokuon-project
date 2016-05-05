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
import java.util.List;

import tsuyogoro.sugorokuon.models.entities.Station;
import tsuyogoro.sugorokuon.utils.SugorokuonLog;

public class StationApi {

    private StationDbOpenHelper mDbOpenHelper;

    /**
     * コンストラクタ
     *
     * @param context
     */
    public StationApi(Context context) {
        if (context == null) {
            SugorokuonLog.w("Context is null in StationApi constructor");
            throw new IllegalArgumentException("Context is null in StationApi constructor");
        }
        mDbOpenHelper = new StationDbOpenHelper(context);
    }

    /**
     * 全Stationのデータを読み出す。
     *
     * @return Stationデータが無かったら空のlistが返る。
     */
    public List<Station> load() {
        return doLoad(null, null);
    }

    /**
     * OnAirSong情報を提供している or していない stationを読み込む
     *
     * @param isOnAirSongAvailable
     * @return 見つからなかった場合は空っぽのリスト
     */
    public List<Station> load(boolean isOnAirSongAvailable) {
        String where = StationTableDefiner.StationTableColumn.ON_AIR_SONGS_AVALIABLE.columnName() + " = ?";
        String[] whereArgs = new String[] { isOnAirSongAvailable ? "1" : "0" };

        return doLoad(where, whereArgs);
    }

    /**
     * 指定したIDのStationのデータを取得。
     *
     * @param stationId データが欲しいstationのID。
     * @return Stationデータが無かったらnullが返る。
     */
    public Station load(String stationId) {
        String where = StationTableDefiner.StationTableColumn.ID.name + "= ? ";
        String[] whereArgs = new String[]{ stationId };

        List<Station> stations = doLoad(where, whereArgs);
        if (0 < stations.size()) {
            return stations.get(0);
        } else {
            return null;
        }
    }

    private List<Station> doLoad(String where, String[] whereArgs) {
        SQLiteDatabase readableDb = mDbOpenHelper.getReadableDatabase();

        List<Station> stations = new ArrayList<Station>();

        Cursor c;

        c = readableDb.query(StationTableDefiner.TABLE_NAME,
                StationTableDefiner.allColumnNames(),
                where, whereArgs, null, null, null);

        c.moveToFirst();

        for (int i = 0; i < c.getCount(); i++) {
            stations.add(StationTableDefiner.createData(c));
            c.moveToNext();
        }

        c.close();
        readableDb.close();

        return stations;
    }

    /**
     * Stationを1件データベースへ追加
     *
     * @param station
     * @return 新しく追加されたrowのID
     */
    public long insert(Station station) {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();

        long rowId = db.insert(StationTableDefiner.TABLE_NAME, "",
                makeContentValues(station));

        db.close();

        return rowId;
    }

    /**
     * StationデータをDBに格納する。
     * データをDownloadした後、このmethodを使って格納していく事。
     *
     * @param stations Downloadしてparseしたstationのデータ
     * @return 新しく追加されたrowのIDリスト
     */
    public long[] insert(List<Station> stations) {

        long[] ids = new long[stations.size()];

        for (int i = 0; i < stations.size(); i++) {
            ids[i] = insert(stations.get(i));
        }

        return ids;
    }

    /**
     * Stationデータが格納されているDB(Table)を削除する
     * 同時に、logoデータのキャッシュも削除される
     */
    public void clear() {

        List<Station> stations = load();
        for (Station s : stations) {
            s.abandonLogoCache();
        }

        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        db.delete(StationTableDefiner.TABLE_NAME, null, null);
        db.close();
    }

    /**
     * stationのidと等しいrowをupdateする
     *
     * @param station
     * @return
     */
    public boolean update(Station station) {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();

        ContentValues cv = makeContentValues(station);

        int rows = db.update(StationTableDefiner.TABLE_NAME, cv,
                StationTableDefiner.StationTableColumn.ID.columnName() + "=?",
                new String[]{ station.id });

        db.close();

        if (1 == rows) {
            return true;
        } else if (1 < rows) {
            SugorokuonLog.w("More than 1 rows were updated for " + station.ascii_name);
            return false;
        } else {
            SugorokuonLog.w("Failed to update station : " + station.ascii_name);
            return false;
        }
    }

    private ContentValues makeContentValues(Station station) {
        ContentValues cv = new ContentValues();

        cv.put(StationTableDefiner.StationTableColumn.ID.columnName(), station.id);
        cv.put(StationTableDefiner.StationTableColumn.TYPE.columnName(), station.type);
        cv.put(StationTableDefiner.StationTableColumn.NAME.columnName(), station.name);
        cv.put(StationTableDefiner.StationTableColumn.ASCII_NAME.columnName(), station.ascii_name);
        cv.put(StationTableDefiner.StationTableColumn.SITE_URL.columnName(), station.siteUrl);
        cv.put(StationTableDefiner.StationTableColumn.LOGO_URL.columnName(), station.logoUrl);
        cv.put(StationTableDefiner.StationTableColumn.BANNER_URL.columnName(), station.bannerUrl);

        if (null == station.getLogoCachePath()) {
            cv.put(StationTableDefiner.StationTableColumn.LOGO_CACHE.columnName(), "");
        } else {
            cv.put(StationTableDefiner.StationTableColumn.LOGO_CACHE.columnName(),
                    station.getLogoCachePath());
        }

        cv.put(StationTableDefiner.StationTableColumn.ON_AIR_SONGS_AVALIABLE.columnName(),
                (station.isOnAirSongsAvailable()) ? 1 : 0);

        return cv;
    }

}
