/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network.radikoadaptation;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import junit.framework.Assert;

import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import tsuyogoro.sugorokuon.models.entities.Feed;

public class UFeedFetcherTest extends AndroidTestCase {

    @MediumTest
    public void testDownloadFeed() throws Exception {
        AbstractHttpClient client = new DefaultHttpClient();
        Feed feed = FeedFetcher.fetch("FMT", client);
        Assert.assertTrue(0 < feed.onAirSongs.size());
    }
}
