/**
 * Copyright (c) 
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.models.apis;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

class StationDbOpenHelper extends BaseDbOpenHelper {

	private static final String DB_NAME = "stationinfo";

	private static final int DB_VERSION = 3;

    private List<BaseTableDefiner> mTableDefiners = new ArrayList<BaseTableDefiner>();

    /**
     * コンストラクタ
     *
     * @param context
     */
	public StationDbOpenHelper(Context context) {
		super(DB_NAME, DB_VERSION, context);

        mTableDefiners.add(new StationTableDefiner());
	}

    @Override
    List<BaseTableDefiner> getTableDefiners() {
        return mTableDefiners;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }
}
