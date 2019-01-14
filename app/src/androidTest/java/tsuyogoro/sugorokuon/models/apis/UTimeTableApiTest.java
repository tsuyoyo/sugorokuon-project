/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.models.apis;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import tsuyogoro.sugorokuon.models.entities.OnedayTimetable;
import tsuyogoro.sugorokuon.models.entities.Program;

public class UTimeTableApiTest extends AndroidTestCase {

    private TimeTableApi mTarget;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mTarget = new TimeTableApi(getContext());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mTarget.clear();
        mTarget = null;
    }

    private final String[] DUMMY_STATION_IDS = new String[]{
            "dummyStation01", "dummyStation02", "dummyStation03"
    };

    private long insertDummyProgram(int year, int month, int date, int hour, int minute,
                                    int lengthMin, String stationId, boolean isRecommend) {

        Program p = createProgram(year, month, date, hour, minute, lengthMin, stationId);

        p.setRecommend(isRecommend);

        return mTarget.insert(p);
    }

    private Program createProgram(int year, int month, int date, int hour, int minute,
                                  int lengthMin, String stationId) {
        Program.Builder builder = new Program.Builder();

        Calendar from = Calendar.getInstance();
        from.set(year, month - 1, date, hour, minute, 0);
        from.set(Calendar.MILLISECOND, 0);

        Calendar to = (Calendar) from.clone();
        to.add(Calendar.MINUTE, lengthMin);

        builder.startTime = from;
        builder.endTime = to;

        builder.description = "dummy description";
        builder.info = "dummy info";
        builder.personalities = "personality-1, personality-2";
        builder.stationId = stationId;
        builder.title = "dummy title";
        builder.subtitle = "dummy subtitle";
        builder.url = "http://www.google.com";

        return builder.create();
    }

    // Station、[0]:3つ、[1]:4つ、[2]:5つ
    // 2015/5/29の番組 : 9つ、2015/5/30の番組 : 3つ
    // オススメ番組 : 5つ
    private List<Long> insertTestData() {
        List<Long> ids = new ArrayList<Long>();

        ids.add(insertDummyProgram(2015, 5, 29, 5, 0, 90, DUMMY_STATION_IDS[0], true));
        ids.add(insertDummyProgram(2015, 5, 29, 7, 30, 120, DUMMY_STATION_IDS[0], false));
        ids.add(insertDummyProgram(2015, 5, 29, 9, 30, 180, DUMMY_STATION_IDS[0], false));
        ids.add(insertDummyProgram(2015, 5, 29, 15, 0, 90, DUMMY_STATION_IDS[1], true));
        ids.add(insertDummyProgram(2015, 5, 29, 17, 30, 120, DUMMY_STATION_IDS[1], false));
        ids.add(insertDummyProgram(2015, 5, 29, 19, 30, 180, DUMMY_STATION_IDS[1], false));
        ids.add(insertDummyProgram(2015, 5, 29, 23, 0, 90, DUMMY_STATION_IDS[1], true));
        ids.add(insertDummyProgram(2015, 5, 30, 0, 0, 120, DUMMY_STATION_IDS[2], false));
        ids.add(insertDummyProgram(2015, 5, 30, 4, 59, 180, DUMMY_STATION_IDS[2], true));
        ids.add(insertDummyProgram(2015, 5, 30, 5, 0, 180, DUMMY_STATION_IDS[2], true));
        ids.add(insertDummyProgram(2015, 5, 30, 8, 0, 180, DUMMY_STATION_IDS[2], false));
        ids.add(insertDummyProgram(2015, 5, 30, 11, 0, 180, DUMMY_STATION_IDS[2], false));

        return ids;
    }

    @SmallTest
    public void testInsert() {
        List<Long> ids = insertTestData();

        Assert.assertTrue(0 < ids.size());

        for (Long id : ids) {
            Assert.assertTrue(-1 < id);
        }
    }

    @SmallTest
    public void testInsertConflictData() {
        Assert.assertTrue(0 < insertDummyProgram(2015, 5, 10, 9, 0, 60, DUMMY_STATION_IDS[0], true));

        // 失敗して-1が返ってくるはず
        Assert.assertEquals(-1, insertDummyProgram(2015, 5, 10, 9, 0, 60, DUMMY_STATION_IDS[0], true));

        // 時間が同じでもStationIDが同じだけなら成功するはず
        Assert.assertTrue(0 < insertDummyProgram(2015, 5, 10, 9, 0, 60, DUMMY_STATION_IDS[1], true));
    }

    @SmallTest
    public void testGetTimeTable() {
        insertTestData();

        OnedayTimetable timeTable;

        // 2015/5/29のDUMMY_STATION_IDS[0]の番組は3つ
        timeTable = mTarget.fetchTimetable(2015, 5, 29, DUMMY_STATION_IDS[0]);
        Assert.assertEquals(3, timeTable.programs.size());

        // 2015/5/29のDUMMY_STATION_IDS[1]の番組は4つ
        timeTable = mTarget.fetchTimetable(2015, 5, 29, DUMMY_STATION_IDS[1]);
        Assert.assertEquals(4, timeTable.programs.size());

        // 2015/5/29のDUMMY_STATION_IDS[2]の番組は2つ
        timeTable = mTarget.fetchTimetable(2015, 5, 29, DUMMY_STATION_IDS[2]);
        Assert.assertEquals(2, timeTable.programs.size());

        // 2015/5/30のDUMMY_STATION_IDS[2]の番組は3つ
        timeTable = mTarget.fetchTimetable(2015, 5, 30, DUMMY_STATION_IDS[2]);
        Assert.assertEquals(3, timeTable.programs.size());
    }

    @SmallTest
    public void testGetRecommendPrograms() {
        insertTestData();
        List<Program> programs = mTarget.fetchRecommends();
        Assert.assertEquals(5, programs.size());
    }

    @SmallTest
    public void testUpdateRecommendPrograms() {
        insertTestData();

        String[] dummyKeywords = new String[] { "dummy" };
        mTarget.updateRecommends(new ProgramSearchKeywordFilter(dummyKeywords));

        List<Program> programs = mTarget.fetchRecommends();
        Assert.assertTrue(0 < programs.size());
    }

    @SmallTest
    public void testUpdate() {
        insertTestData();

        Program.Builder builder = new Program.Builder();

        Calendar from = Calendar.getInstance();
        from.set(2015, 5 - 1, 29, 5, 0, 0);
        from.set(Calendar.MILLISECOND, 0);

        Calendar to = (Calendar) from.clone();
        to.add(Calendar.MINUTE, 90);

        builder.startTime = from;
        builder.endTime = to;
        builder.description = "update description";
        builder.info = "update info";
        builder.stationId = DUMMY_STATION_IDS[0];
        builder.url = "http://www.yahoo.com";

        int rows = mTarget.update(builder.create());

        Assert.assertEquals(1, rows);
    }

    @SmallTest
    public void testSearch() {
        insertTestData();

        List<Program> p = mTarget.search(
                new ProgramSearchKeywordFilter(new String[]{ "dummy" }));

        Assert.assertTrue(0 < p.size());
    }

}
