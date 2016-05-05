/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import junit.framework.Assert;

import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import tsuyogoro.sugorokuon.constants.Area;
import tsuyogoro.sugorokuon.constants.StationLogoSize;
import tsuyogoro.sugorokuon.models.apis.OnAirSongsApi;
import tsuyogoro.sugorokuon.models.apis.StationApi;
import tsuyogoro.sugorokuon.models.entities.Station;
import tsuyogoro.sugorokuon.network.IRadikoStationFetcher;
import tsuyogoro.sugorokuon.network.radikoapi.RadikoStationsFetcher;

public class UOnAirSongsServiceTest extends AndroidTestCase {

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

        IRadikoStationFetcher stationFetcher = new RadikoStationsFetcher();
        List<Station> stations = stationFetcher.fetch(Area.CHIBA.id, StationLogoSize.SMALL,
                getLogoCacheDirName());

        StationApi stationDb = new StationApi(getContext());
        stationDb.clear();
        stationDb.insert(stations);

        OnAirSongsApi onAirSongDb = new OnAirSongsApi(getContext());
        onAirSongDb.clear();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        StationApi stationDb = new StationApi(getContext());
        stationDb.clear();
    }

    @MediumTest
    public void testActionFetchOnAirSongs() throws Exception {

        final CountDownLatch latch = new CountDownLatch(1);

        IntentFilter filter = new IntentFilter(OnAirSongsService.NOTIFY_ON_FETCH_LATEST_SETLIST);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                latch.countDown();
            }
        };
        Intent intent = new Intent(OnAirSongsService.ACTION_FETCH_ON_AIR_SONGS);
        getContext().registerReceiver(receiver, filter);

        // Android LのLVL(Licence Verification Library)のバグにつき、package指定が要る
        // http://qiita.com/tmurakam99/items/8eb98c7eb572aa46dd76
        intent.setPackage("tsuyogoro.sugorokuon");
        getContext().startService(intent);

        OnAirSongsApi db = new OnAirSongsApi(getContext());

        // Serviceが走る前は空っぽ
        Assert.assertTrue(db.searchAll().isEmpty());

        latch.await(1, TimeUnit.MINUTES);

        // Serviceが走ったら何か入っているはず
        Assert.assertFalse(db.searchAll().isEmpty());
    }

}
