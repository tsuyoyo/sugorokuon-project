/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.models.apis;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.Assert;

import tsuyogoro.sugorokuon.models.entities.Station;

public class UStationApiTest extends AndroidTestCase {

    private StationApi mTarget;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mTarget = new StationApi(getContext());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mTarget.clear();
        mTarget = null;
    }

    @SmallTest
    public void testInsert() {

        Station.Builder testData01 = new Station.Builder();
        testData01.id = "testId01";
        testData01.ascii_name = "testAscii_01";
        testData01.bannerUrl = "http://testdata01.com";
        testData01.logoCache = "logoCache01";
        testData01.logoCachePath = "logoCachePath01";
        testData01.logoUrl = "http://testlogo01.com";
        testData01.name = "testName01";
        testData01.siteUrl = "testSiteUrl_01";
        mTarget.insert(testData01.create());

        Station s = mTarget.load("testId01");
        Assert.assertNotNull(s);
        Assert.assertEquals("testAscii_01", s.ascii_name);

        Assert.assertEquals(mTarget.load().size(), 1);
    }

    @SmallTest
    public void testInsertSomeData() {
        insertTestData("01");
        insertTestData("02");
        insertTestData("03");

        Assert.assertEquals(mTarget.load().size(), 3);
    }

    @SmallTest
    public void testDeleteAll() {
        insertTestData("01");
        insertTestData("02");
        insertTestData("03");

        Assert.assertEquals(mTarget.load().size(), 3);

        mTarget.clear();;
        Assert.assertEquals(mTarget.load().size(), 0);
    }

    @SmallTest
    public void testUpdate() {
        Station.Builder testData01 = new Station.Builder();
        testData01.id = "testId01";
        testData01.ascii_name = "testAscii_01";
        testData01.bannerUrl = "http://testdata01.com";
        testData01.logoCache = "logoCache01";
        testData01.logoCachePath = "logoCachePath01";
        testData01.logoUrl = "http://testlogo01.com";
        testData01.name = "testName01";
        testData01.siteUrl = "testSiteUrl_01";
        mTarget.insert(testData01.create());

        Station.Builder updateData = new Station.Builder();
        updateData.id = "testId01";
        updateData.ascii_name = "testAscii_02";
        updateData.bannerUrl = "http://testdata02.com";
        updateData.logoCache = "logoCache02";
        updateData.logoCachePath = "logoCachePath02";
        updateData.logoUrl = "http://testlogo02.com";
        updateData.name = "testName02";
        updateData.siteUrl = "testSiteUrl_02";
        mTarget.update(updateData.create());

        Station d = mTarget.load("testId01");
        assertEquals("testAscii_02", d.ascii_name);
        assertEquals("testSiteUrl_02", d.siteUrl);
    }

    private long insertTestData(String id) {

        Station.Builder testData = new Station.Builder();
        testData.id = "testId" + id;
        testData.ascii_name = "testAscii_" + id;
        testData.bannerUrl = "http://testdata" + id + ".com";
        testData.logoCache = "logoCache" + id;
        testData.logoCachePath = "logoCachePath" + id;
        testData.logoUrl = "http://testlogo" + id + ".com";
        testData.name = "testName" + id;
        testData.siteUrl = "testSiteUrl_" + id;

        return mTarget.insert(testData.create());
    }

}
