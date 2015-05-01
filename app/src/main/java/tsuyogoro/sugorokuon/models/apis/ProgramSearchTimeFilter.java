/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.models.apis;

import java.util.Calendar;

public class ProgramSearchTimeFilter extends ProgramSearchFilter {

    private final Calendar mFrom;

    private final Calendar mTo;

    private final boolean mRecommendOnly;

    public ProgramSearchTimeFilter(Calendar from, Calendar to) {
        mFrom = from;
        mTo = to;
        mRecommendOnly = false;
    }

    public ProgramSearchTimeFilter(Calendar from, Calendar to, boolean recommendOnly) {
        mFrom = from;
        mTo = to;
        mRecommendOnly = recommendOnly;
    }

    @Override
    Condition getCondition() {

        String from = ProgramTableDefiner.ProgramTableColumn.START_TIME + " >= ?";
        String to = ProgramTableDefiner.ProgramTableColumn.START_TIME + " <= ?";

        String where = from + " " + to;

        if (mRecommendOnly) {
            where += " " + ProgramTableDefiner.ProgramTableColumn.ISRECOMMEND + " = 1";
        }

        String[] whereArgs = new String[] {
                Long.toString(mFrom.getTimeInMillis()),
                Long.toString(mTo.getTimeInMillis())
        };

        return new Condition(where, whereArgs);
    }
}
