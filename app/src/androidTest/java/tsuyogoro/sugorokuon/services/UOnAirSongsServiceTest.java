/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import junit.framework.Assert;

import org.apache.http.impl.client.DefaultHttpClient;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import tsuyogoro.sugorokuon.constants.Area;
import tsuyogoro.sugorokuon.constants.StationLogoSize;
import tsuyogoro.sugorokuon.models.apis.OnAirSongsApi;
import tsuyogoro.sugorokuon.models.apis.StationApi;
import tsuyogoro.sugorokuon.models.entities.Station;
import tsuyogoro.sugorokuon.network.radikoadaptation.StationsFetcher;

public class UOnAirSongsServiceTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        List<Station> stations = StationsFetcher.fetch(
                Area.CHIBA.id, StationLogoSize.SMALL, new DefaultHttpClient());

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
