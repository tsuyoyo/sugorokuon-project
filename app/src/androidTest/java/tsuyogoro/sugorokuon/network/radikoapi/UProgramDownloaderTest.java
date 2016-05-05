/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network.radikoapi;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import junit.framework.Assert;

import java.util.List;

import okhttp3.OkHttpClient;
import tsuyogoro.sugorokuon.models.entities.OnedayTimetable;
import tsuyogoro.sugorokuon.models.entities.Station;
import tsuyogoro.sugorokuon.network.IRadikoTimeTableFetcher;

public class UProgramDownloaderTest extends AndroidTestCase {

    private IRadikoTimeTableFetcher target;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        target = new RadikoTimeTableFetcher();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @MediumTest
    public void testDownloadWeeklyTimeTable() throws Exception {

        // Download for specific station
        List<OnedayTimetable> timetables = target.fetchWeeklyTable("FMT");
        Assert.assertEquals(7, timetables.size());

        for (OnedayTimetable timeTable : timetables) {
            Assert.assertTrue(0 < timeTable.programs.size());
        }

    }

    @MediumTest
    public void testDownloadTodayTimeTable() throws Exception {
        Station.Builder builder = new Station.Builder();
        builder.id = "FMT";

        OnedayTimetable timeTable = target.fetchTodaysTable(builder.create());

        Assert.assertNotNull(timeTable);
        Assert.assertTrue(0 < timeTable.programs.size());
    }

    @MediumTest
    public void testDownloadTomorrowTimeTable() throws Exception {
        // Memo : 必要なら考えるけど、station IDを指定して翌日の番組表は取れない
        Assert.assertTrue(false);
    }

    @MediumTest
    public void testTimeTableApiClientWeekly() throws Exception {
        TimeTableApiClient apiClient = new TimeTableApiClient(new OkHttpClient());

        TimeTableApiClient.TimeTableRoot res = apiClient.fetchWeeklyTimeTable("QRR");
        Assert.assertNotNull(res);
        Assert.assertFalse(res.stations.isEmpty());
        Assert.assertTrue(1 == res.stations.size());
        Assert.assertTrue(res.stations.get(0).timetables.size() > 0);

        for (TimeTableApiClient.TimeTableRoot.Station.TimeTable t : res.stations.get(0).timetables) {
            Assert.assertTrue(t.programs.size() > 0);
        }
    }

    @MediumTest
    public void testTimeTableApiClientToday() throws Exception {
        TimeTableApiClient apiClient = new TimeTableApiClient(new OkHttpClient());

        TimeTableApiClient.TimeTableRoot res = apiClient.fetchTodaysTimeTable("QRR");
        Assert.assertNotNull(res);
        Assert.assertFalse(res.stations.isEmpty());
        Assert.assertEquals(1, res.stations.get(0).timetables.size());
    }

}
