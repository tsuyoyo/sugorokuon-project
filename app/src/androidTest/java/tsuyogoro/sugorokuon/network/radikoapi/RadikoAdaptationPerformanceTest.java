/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network.radikoapi;

import android.content.pm.PackageManager;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

import junit.framework.Assert;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tsuyogoro.sugorokuon.constants.Area;
import tsuyogoro.sugorokuon.constants.StationLogoSize;
import tsuyogoro.sugorokuon.models.apis.ProgramSearchKeywordFilter;
import tsuyogoro.sugorokuon.models.apis.StationApi;
import tsuyogoro.sugorokuon.models.apis.TimeTableApi;
import tsuyogoro.sugorokuon.models.entities.OnedayTimetable;
import tsuyogoro.sugorokuon.models.entities.Station;
import tsuyogoro.sugorokuon.network.IStationFetcher;
import tsuyogoro.sugorokuon.network.ITimeTableFetcher;

public class RadikoAdaptationPerformanceTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        makeLogoCacheDir();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        File cachedDir = new File(getLogoCacheDirName());
        if (cachedDir.exists() && cachedDir.isDirectory()) {
            for (File logo : cachedDir.listFiles()) {
                Assert.assertTrue("Should succeed to delete logo : " + logo.getName(), logo.delete());
            }
        }

        TimeTableApi timeTableApi = new TimeTableApi(getContext());
        timeTableApi.clear();

        StationApi stationApi = new StationApi(getContext());
        stationApi.clear();
    }

    private String getLogoCacheDirName() {
        String logoCachedDir = null;
        try {
            String pkgName = getContext().getPackageName();
            logoCachedDir = getContext().getPackageManager().getPackageInfo(pkgName, 0)
                    .applicationInfo.dataDir + File.pathSeparator + "stationlogos";
        } catch (PackageManager.NameNotFoundException e) {
            Assert.assertTrue("Error Package name not found " + e, false);
        }
        return logoCachedDir;
    }

    private void makeLogoCacheDir() {
        String dirName = getLogoCacheDirName();
        File cacheDir = new File(dirName);
        Assert.assertTrue("Should succeed to create " + dirName, cacheDir.mkdir());
    }

    @LargeTest
    public void testDownloadAllStationInfo() {

        long start = Calendar.getInstance().getTimeInMillis();

        // 全リージョンのstation情報を落とす
        IStationFetcher stationsFetcher = new RadikoStationsFetcher();
        List<Station> stations = stationsFetcher.fetch(Area.values(),
                StationLogoSize.LARGE, getLogoCacheDirName());

        // 全番組情報を落とす
        ITimeTableFetcher timeTableFetcher = new RadikoTimeTableFetcher();
        List<OnedayTimetable> timeTable = timeTableFetcher.fetchWeeklyTable(stations);

        // 全ての局の番組情報がきちんととれているかをチェック
        Assert.assertEquals(stations.size() * 7, timeTable.size());
        for (OnedayTimetable oneday : timeTable) {
            Assert.assertTrue(0 < oneday.programs.size());
        }

        // DBへinsert
        TimeTableApi db = new TimeTableApi(getContext());
        db.insert(timeTable);

        // オススメフラグを立てる
        ProgramSearchKeywordFilter filter = new ProgramSearchKeywordFilter(new String[]{"岡村隆史"});
        int updatedNum = db.updateRecommends(filter);
        Log.d("SugorokuonTest", "testDownloadAllStationInfo - " + updatedNum + " programs are recommended");

        long end = Calendar.getInstance().getTimeInMillis();
        Log.d("SugorokuonTest", "testDownloadAllStationInfo - " +
                Long.toString((end - start) / 1000) + " sec");

    }

    @MediumTest
    public void testDownloadOneAreaPararel() {

        // (メモ)
        // 複数スレッドでダウンロードすれば速いんじゃないかと思ってたけど、
        // こっちの方が時間がかかる... (スレッドを2にすると100秒、4にすると133秒、1だと88秒)
        ExecutorService exec = Executors.newFixedThreadPool(1);
        Log.d("SugorokuonTest",
                "testDownloadOneAreaPararel -- Num of threads = 2");

        long start = Calendar.getInstance().getTimeInMillis();

        // 全リージョンのstation情報を落とす
        IStationFetcher stationFetcher = new RadikoStationsFetcher();
        List<Station> stations = stationFetcher.fetch(Area.CHIBA.id,
                StationLogoSize.LARGE, getLogoCacheDirName());

        // DBへstation情報をstore
        StationApi stationApi = new StationApi(getContext());
        stationApi.insert(stations);

        final CountDownLatch latch = new CountDownLatch(stations.size());

        final List<OnedayTimetable> timeTable = new ArrayList<OnedayTimetable>();

        // 1 station分のTimeTableをdownloadするtask
        class ProgramDownloadTask implements Runnable {

            private final String mStationId;

            ProgramDownloadTask(String stationId) {
                this.mStationId = stationId;
            }

            @Override
            public void run() {

                Log.d("SugorokuonTest",
                        "testDownloadOneAreaPararel -- " + mStationId + " DL-S");

                ITimeTableFetcher timeTableFetcher = new RadikoTimeTableFetcher();
                List<OnedayTimetable> weekTimeTable = timeTableFetcher.fetchWeeklyTable(
                        mStationId);
                if (null != weekTimeTable) {
                    synchronized (timeTable) {
                        timeTable.addAll(weekTimeTable);
                    }
                }

                latch.countDown();
            }
        }

        for (Station station : stations) {
            exec.submit(new ProgramDownloadTask(station.id));
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Assert.assertTrue("Unexpected interruption : " + e.getMessage(), false);
        }

        // DBへの書き込み
        TimeTableApi timeTableApi = new TimeTableApi(getContext());
        timeTableApi.insert(timeTable);

        Log.d("SugorokuonTest", "testDownloadOneAreaPararel (download programs) - " +
                Long.toString((Calendar.getInstance().getTimeInMillis() - start) / 1000) + " sec");

        // オススメフラグ
        int updatedNum = timeTableApi.updateRecommends(
                new ProgramSearchKeywordFilter(new String[]{"岡村隆史"}));
        assertTrue(updatedNum > 0);
        assertTrue(updatedNum < 10);

        long end = Calendar.getInstance().getTimeInMillis();

        Log.d("SugorokuonTest", "testDownloadOneAreaPararel - " +
                Long.toString((end - start) / 1000) + " sec");
    }

    @LargeTest
    public void testDownloadAllStationsEach() throws Exception {

        Area[] allArea = Area.values();

        IStationFetcher stationFetcher = new RadikoStationsFetcher();
        for (Area area : allArea) {
            List<Station> stations = stationFetcher.fetch(
                    area.id, StationLogoSize.LARGE, getLogoCacheDirName());
            Assert.assertTrue("Failed to get stations in " + area.name(), 0 < stations.size());
        }
    }

//    @MediumTest
//    public void testDownloadAllStationsAsync() throws Exception {
//
//        final List<Station> res = new ArrayList<Station>();
//        final CountDownLatch latch = new CountDownLatch(Area.values().length);
//
//        final long start = Calendar.getInstance().getTimeInMillis();
//
//        // 1局分のダウンロードを非同期板で行っていく
//        RequestQueue queue = Volley.newRequestQueue(getContext());
//        for (Area area : Area.values()) {
//            StationsFetcher.fetchAsync(area.id, StationLogoSize.LARGE,
//                    new StationsFetcher.IOnGetStationListener() {
//                        @Override
//                        public void onGet(List<Station> stations) {
//                            Assert.assertTrue(0 < stations.size());
//                            synchronized (res) {
//                                addStationsWithoutDuplicate(res, stations);
//                            }
//                            latch.countDown();
//                        }
//                    }, queue);
//        }
//        latch.await();
//
//        long end = Calendar.getInstance().getTimeInMillis();
//        Log.d("SugorokuonTest", "testDownloadAllStationsAsync - " +
//                Long.toString((end - start) / 1000) + " sec");
//
//        // 一応結果を同期板と比較
//        List<Station> refResult = StationsFetcher.fetch(Area.values(), StationLogoSize.LARGE);
//    }

    private static void addStationsWithoutDuplicate(List<Station> list, List<Station> toAdd) {
        for (Station addCand : toAdd) {
            boolean isNew = true;

            for (Station s : list) {
                if (s.id.equals(addCand.id)) {
                    isNew = false;
                    continue;
                }
            }

            if (isNew) {
                list.add(addCand);
            }
        }
    }
}
