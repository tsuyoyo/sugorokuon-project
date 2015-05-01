/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.models.apis;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

class ProgramDbOpenHelper extends BaseDbOpenHelper {

    private static final String DB_NAME = "programinfo";

    // Memo (2005/2/15)
    // ProgramのStart, EndをUnix時間 (秒、INTEGER) でDBに持たせるようにするので
    // バージョンを上げる
    private static final int DB_VERSION = 2;

    private List<BaseTableDefiner> mTableDefiners = new ArrayList<BaseTableDefiner>();

    public ProgramDbOpenHelper(Context context) {
        super(DB_NAME, DB_VERSION, context);

        mTableDefiners.add(new ProgramTableDefiner());
        mTableDefiners.add(new ProgramIconCacheTableDefiner());
    }

    @Override
    List<BaseTableDefiner> getTableDefiners() {
        return mTableDefiners;
    }
}
