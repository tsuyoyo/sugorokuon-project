/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network.radikoapi;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import junit.framework.Assert;

import okhttp3.OkHttpClient;
import tsuyogoro.sugorokuon.models.entities.Feed;

public class UFeedFetcherTest extends AndroidTestCase {

    @MediumTest
    public void testDownloadFeed() throws Exception {
        Feed feed = FeedFetcher.fetch("FMT");
        Assert.assertTrue(0 < feed.onAirSongs.size());
    }

    @MediumTest
    public void testOnAirSongsApi() throws Exception {
        FeedApiClient api = new FeedApiClient(new OkHttpClient());

        FeedApiClient.NowOnAir res = api.fetchNowOnAirSongs("INT");
        Assert.assertTrue(0 < res.onAirSongs.size());
    }

    @MediumTest
    public void testCmApi() throws Exception {
        FeedApiClient api = new FeedApiClient(new OkHttpClient());

        FeedApiClient.Cm res = api.fetchCm("INT");
        Assert.assertTrue(0 < res.items.size());
    }
}
