/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import tsuyogoro.sugorokuon.datatype.Station;

public class StationDbReader {

    /**
     * 全Stationのデータを読み出す。
     *
     * @return Stationデータが無かったら空のlistが返る。
     */
    public List<Station> getStationData(SQLiteDatabase readableDb) {
        List<Station> stations = new ArrayList<Station>();

        Cursor c = readableDb.query(StationTableDefiner.TABLE_NAME,
                StationTableDefiner.allColumnNames(),
                null, null, null, null, null);

        c.moveToFirst();

        for (int i = 0; i < c.getCount(); i++) {
            Station station = StationTableDefiner.createData(c);
            stations.add(station);
            c.moveToNext();
        }

        c.close();

        return stations;
    }

    /**
     * 指定したIDのStationのデータを取得。
     *
     * @param stationId データが欲しいstationのID。
     * @return Stationデータが無かったらnullが返る。
     */
    public Station getStationData(String stationId, SQLiteDatabase readableDb) {

        Station data = null;

        Cursor c = readableDb.query(StationTableDefiner.TABLE_NAME,
                StationTableDefiner.allColumnNames(),
                StationTableDefiner.StationTableColumn.ID.name + "= ? ",
                new String[]{stationId}, null, null, null);

        if (1 == c.getCount()) {
            c.moveToNext();
            data = StationTableDefiner.createData(c);
        }
        c.close();

        return data;
    }

}
