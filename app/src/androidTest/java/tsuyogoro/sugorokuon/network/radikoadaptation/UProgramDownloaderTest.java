/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network.radikoadaptation;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import junit.framework.Assert;

import java.util.List;

import tsuyogoro.sugorokuon.models.entities.OnedayTimetable;
import tsuyogoro.sugorokuon.models.entities.Station;

public class UProgramDownloaderTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @MediumTest
    public void testDownloadWeeklyTimeTable() throws Exception {

        // Download for specific station
        List<OnedayTimetable> timetables =
                TimeTableFetcher.fetchWeeklyTable("FMT");
        Assert.assertEquals(7, timetables.size());

        for (OnedayTimetable timeTable : timetables) {
            Assert.assertTrue(0 < timeTable.programs.size());
        }

    }

    @MediumTest
    public void testDownloadTodayTimeTable() throws Exception {
        Station.Builder builder = new Station.Builder();
        builder.id = "FMT";

        OnedayTimetable timeTable = TimeTableFetcher.fetchTodaysTable(builder.create());

        Assert.assertNotNull(timeTable);
        Assert.assertTrue(0 < timeTable.programs.size());
    }

    @MediumTest
    public void testDownloadTomorrowTimeTable() throws Exception {
        // Memo : 必要なら考えるけど、station IDを指定して翌日の番組表は取れない
        Assert.assertTrue(false);
    }

}
