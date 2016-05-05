package tsuyogoro.sugorokuon.network.nhk;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import junit.framework.Assert;

import java.util.List;

import tsuyogoro.sugorokuon.models.entities.Station;

public class UNhkStationsFetcherTest extends AndroidTestCase {

    private NhkStationsFetcher target;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        target = new NhkStationsFetcher();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @MediumTest
    public void testDownloadStationList() throws Exception {

        List<Station> stations = target.fetch();

        Assert.assertTrue(stations != null);
        Assert.assertTrue(stations.size() > 0);
    }
}
