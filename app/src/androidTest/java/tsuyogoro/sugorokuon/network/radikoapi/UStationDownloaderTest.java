/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network.radikoapi;

import android.content.pm.PackageManager;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import junit.framework.Assert;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import tsuyogoro.sugorokuon.constants.Area;
import tsuyogoro.sugorokuon.constants.StationLogoSize;
import tsuyogoro.sugorokuon.models.entities.Station;
import tsuyogoro.sugorokuon.utils.SugorokuonLog;

public class UStationDownloaderTest extends AndroidTestCase {

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
    }

    @SmallTest
    public void testDownloadStations() throws Exception {

//        File testDir = new File(getLogoCacheDirName());
//        if (!testDir.mkdir()) {
//            String dir = getLogoCacheDirName();
//            SugorokuonLog.e("Failed to create : " + getLogoCacheDirName());
//            Assert.assertTrue(false);
//        }

        List<Station> stations = StationsFetcher.fetch(Area.CHIBA.id, StationLogoSize.LARGE,
                getLogoCacheDirName());

        Assert.assertTrue(stations.size() > 0);

        File cachedDir = new File(getLogoCacheDirName());
        assertTrue("Cached directory was not created", cachedDir.exists());
        assertTrue("No file is stored in cached dir", cachedDir.listFiles().length > 0);
    }

    @MediumTest
    public void testDownloadAllStations() throws Exception {

        long start = Calendar.getInstance().getTimeInMillis();

        List<Station> stations = StationsFetcher.fetch(Area.values(), StationLogoSize.LARGE,
                getLogoCacheDirName());

        long end = Calendar.getInstance().getTimeInMillis();
        Log.d("SugorokuonTest", "testDownloadAllStations - "
                + Long.toString((end - start) / 1000) + " sec");

        Assert.assertTrue(0 < stations.size());
    }

//    @SmallTest
//    public void testDownloadStationsAsync() throws Exception {
//
//        final CountDownLatch latch = new CountDownLatch(1);
//
//        RequestQueue queue = Volley.newRequestQueue(getContext());
//
//        StationsFetcher.fetchAsync(Area.CHIBA.id, StationLogoSize.LARGE,
//                new StationsFetcher.IOnGetStationListener() {
//                    @Override
//                    public void onGet(List<Station> stations) {
//                        Assert.assertTrue(0 < stations.size());
//                        latch.countDown();
//                    }
//                }, queue);
//
//        latch.await();
//    }

}
