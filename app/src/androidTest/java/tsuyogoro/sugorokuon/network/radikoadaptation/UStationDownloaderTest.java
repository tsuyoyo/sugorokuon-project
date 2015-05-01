/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network.radikoadaptation;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import junit.framework.Assert;

import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import tsuyogoro.sugorokuon.constants.Area;
import tsuyogoro.sugorokuon.constants.StationLogoSize;
import tsuyogoro.sugorokuon.models.entities.Station;

public class UStationDownloaderTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @SmallTest
    public void testDownloadStations() throws Exception {

        AbstractHttpClient httpClient = new DefaultHttpClient();
        List<Station> stations = StationsFetcher.fetch(
                Area.CHIBA.id, StationLogoSize.LARGE, httpClient);

        Assert.assertTrue(0 < stations.size());
    }

    @MediumTest
    public void testDownloadAllStations() throws Exception {

        long start = Calendar.getInstance().getTimeInMillis();

        AbstractHttpClient httpClient = new DefaultHttpClient();
        List<Station> stations = StationsFetcher.fetch(Area.values(), StationLogoSize.LARGE, httpClient);

        long end = Calendar.getInstance().getTimeInMillis();
        Log.d("SugorokuonTest", "testDownloadAllStations - "
                + Long.toString((end - start) / 1000) + " sec");

        Assert.assertTrue(0 < stations.size());
    }

    @SmallTest
    public void testDownloadStationsAsync() throws Exception {

        final CountDownLatch latch = new CountDownLatch(1);

        RequestQueue queue = Volley.newRequestQueue(getContext());

        StationsFetcher.fetchAsync(Area.CHIBA.id, StationLogoSize.LARGE,
                new StationsFetcher.IOnGetStationListener() {
                    @Override
                    public void onGet(List<Station> stations) {
                        Assert.assertTrue(0 < stations.size());
                        latch.countDown();
                    }
                }, queue);

        latch.await();
    }

}
