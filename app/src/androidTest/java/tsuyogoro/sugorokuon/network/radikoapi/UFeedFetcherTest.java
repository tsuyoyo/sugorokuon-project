/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network.radikoapi;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import junit.framework.Assert;

import tsuyogoro.sugorokuon.models.entities.Feed;

public class UFeedFetcherTest extends AndroidTestCase {

    @MediumTest
    public void testDownloadFeed() throws Exception {
        Feed feed = FeedFetcher.fetch("FMT");
        Assert.assertTrue(0 < feed.onAirSongs.size());
    }
}
