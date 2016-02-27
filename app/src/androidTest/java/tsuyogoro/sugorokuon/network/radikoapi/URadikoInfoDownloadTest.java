/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network.radikoapi;

import android.content.pm.PackageManager;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

import junit.framework.Assert;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import tsuyogoro.sugorokuon.constants.Area;
import tsuyogoro.sugorokuon.constants.StationLogoSize;
import tsuyogoro.sugorokuon.models.apis.ProgramSearchKeywordFilter;
import tsuyogoro.sugorokuon.models.apis.StationApi;
import tsuyogoro.sugorokuon.models.apis.TimeTableApi;
import tsuyogoro.sugorokuon.models.entities.OnedayTimetable;
import tsuyogoro.sugorokuon.models.entities.Station;

// 局情報取得、番組情報取得、データベースへのストア、を全てテスト。
// パフォーマンスも測定。
// 時間がかかるテストは@LargeTestのannotation付き。
public class URadikoInfoDownloadTest extends AndroidTestCase {

    private String getLogoCacheDirName() {
        String logoCachedDir = null;
        try {
            String pkgName = getContext().getPackageName();
            logoCachedDir = getContext().getPackageManager().getPackageInfo(pkgName, 0)
                    .applicationInfo.dataDir + File.separator + "stationlogos";
        } catch (PackageManager.NameNotFoundException e) {
            Assert.assertTrue("Error Package name not found " + e, false);
        }
        return logoCachedDir;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        String logoCacheDir = getLogoCacheDirName();
        File cacheDir = new File(logoCacheDir);
        if (cacheDir.exists()) {
            for (File logo : cacheDir.listFiles()) {
                Assert.assertTrue("Old Logo should success to be deleted : " + logo.getName(),
                        logo.delete());
            }
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        TimeTableApi timeTableApi = new TimeTableApi(getContext());
        timeTableApi.clear();

        StationApi stationApi = new StationApi(getContext());
        stationApi.clear();
    }

    @MediumTest
    public void testDownloadOneArea() {

        long start = Calendar.getInstance().getTimeInMillis();

        // 全リージョンのstation情報を落とす
        List<Station> stations = StationsFetcher.fetch(Area.CHIBA.id, StationLogoSize.LARGE,
                getLogoCacheDirName());

        StationApi stationApi = new StationApi(getContext());
        stationApi.insert(stations);

        // 全番組情報を落とす
        List<OnedayTimetable> timeTable = TimeTableFetcher.fetchWeeklyTable(stations);

        // 全ての局の番組情報がきちんととれているかをチェック
        Assert.assertEquals(stations.size() * 7, timeTable.size());

        // 番組情報をデータベースへ入れる
        TimeTableApi timeTableApi = new TimeTableApi(getContext());
        Assert.assertTrue(0 < timeTableApi.insert(timeTable).length);

        // オススメフラグを立てる
        ProgramSearchKeywordFilter filter = new ProgramSearchKeywordFilter(new String[]{"岡村隆史"});
        int updatedNum = timeTableApi.updateRecommends(filter);
        Log.d("SugorokuonTest", "testDownloadOneArea - " + updatedNum + " programs are recommended");

        long end = Calendar.getInstance().getTimeInMillis();
        Log.d("SugorokuonTest", "testDownloadOneArea - " +
                Long.toString((end - start) / 1000) + " sec");

        // searchのパフォーマンス
        start = Calendar.getInstance().getTimeInMillis();
        int found = timeTableApi.search(filter).size();
        assertTrue(0 < found);
        end = Calendar.getInstance().getTimeInMillis();
        Log.d("SugorokuonTest", "testDownloadOneArea - load (found = " + Integer.toString(found)
                + ") : " + Long.toString((end - start) / 1000) + " sec");

    }

}
