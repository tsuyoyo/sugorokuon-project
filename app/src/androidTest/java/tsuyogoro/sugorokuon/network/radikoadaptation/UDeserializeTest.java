package tsuyogoro.sugorokuon.network.radikoadaptation;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import junit.framework.Assert;

import tsuyogoro.sugorokuon.models.entities.Feed;

/**
 * Created by tsuyoyo on 16/02/09.
 */
public class UDeserializeTest extends AndroidTestCase {

    @MediumTest
    public void testDownloadFeed() throws Exception {
        Feed feed = FeedFetcher.fetch("FMT");
        Assert.assertTrue(0 < feed.onAirSongs.size());
    }
}
