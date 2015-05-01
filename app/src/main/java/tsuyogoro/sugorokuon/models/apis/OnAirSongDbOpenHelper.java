/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.models.apis;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

class OnAirSongDbOpenHelper extends BaseDbOpenHelper {

    private static final String DB_NAME = "onairsongs";

    private static final int DB_VERSION = 1;

    private List<BaseTableDefiner> mTableDefiners = new ArrayList<BaseTableDefiner>();

    public OnAirSongDbOpenHelper(Context context) {
        super(DB_NAME, DB_VERSION, context);

        mTableDefiners.add(new OnAirSongTableDefiner());
    }

    @Override
    List<BaseTableDefiner> getTableDefiners() {
        return mTableDefiners;
    }
}
