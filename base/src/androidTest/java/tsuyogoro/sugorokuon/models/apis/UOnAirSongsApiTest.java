/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.models.apis;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import tsuyogoro.sugorokuon.models.entities.OnAirSong;

public class UOnAirSongsApiTest extends AndroidTestCase {

    private OnAirSongsApi mTarget;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mTarget = new OnAirSongsApi(getContext());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
//        // TODO : なぜかproguardをかけると「見つかりません」になるので、一旦コメントアウト
//        mTarget.deleteAll();
        mTarget = null;
    }

    private List<OnAirSong> makeTestData() {
        List<OnAirSong> testData = new ArrayList<OnAirSong>();

        testData.add(makeTestData(2000, 1, 23, 13, 0, "-01", "Station01"));
        testData.add(makeTestData(2000, 1, 23, 15, 0, "-02", "Station02"));
        testData.add(makeTestData(2000, 1, 23, 18, 0, "-01", "Station01"));
        testData.add(makeTestData(2000, 1, 23, 23, 59, "-04", "Station01"));
        testData.add(makeTestData(2000, 1, 24, 0, 0, "-05", "Station01"));
        testData.add(makeTestData(2000, 1, 24, 01, 0, "-06", "Station01"));
        testData.add(makeTestData(2000, 1, 24, 4, 59, "-07", "Station02"));
        testData.add(makeTestData(2000, 1, 24, 5, 0, "-01", "Station02"));
        testData.add(makeTestData(2000, 1, 24, 8, 0, "-09", "Station01"));

        return testData;
    }

    private OnAirSong makeTestData(int year, int month, int date, int hour, int minute,
                                   String prefix, String stationId) {
        Calendar testDate = Calendar.getInstance();
        testDate.set(year, month, date, hour, minute, 0);

        // メモ:
        // 下記行、あったほうが良いが、OnAirSongクラスできちんとリセットしているので無くても良い
//        testDate.set(Calendar.MILLISECOND, 0);

        return new OnAirSong(stationId, "testArtist" + prefix, "testTitle" + prefix,
                testDate, "testItemId" + prefix);
    }

    private int doInsertTestData() {
        List<OnAirSong> testData = makeTestData();
        List<Long> rows = mTarget.insert(testData);
        Assert.assertEquals(testData.size(), rows.size());

        return rows.size();
    }

    @SmallTest
    public void testInsert() throws Exception {
        int testDataSize = doInsertTestData();

        List<OnAirSong> all = mTarget.searchAll();
        Assert.assertEquals(testDataSize, all.size());
    }

    @SmallTest
    public void testInsertDuplicatedData() throws Exception {
        doInsertTestData();

        // DateとStationIdが被ってるデータ
        OnAirSong duplicatedData = makeTestData(2000, 1, 23, 13, 0, "-000", "Station01");
        long id = mTarget.insert(duplicatedData);
        Assert.assertEquals(-1, id);
    }

    @SmallTest
    public void testSearchByStationAndDate() throws Exception {
        doInsertTestData();

        // testdataの中の、2000/1/23のStation01の曲は3つ
        List<OnAirSong> result = mTarget.search(2000, 1, 23, "Station01");
        Assert.assertEquals(3, result.size());
    }

    @SmallTest
    public void testSearchByDate() throws Exception {
        doInsertTestData();

        // testdataの中の、2000/1/23の曲は3つ
        List<OnAirSong> result = mTarget.search(2000, 1, 23);
        Assert.assertEquals(4, result.size());
    }

    @SmallTest
    public void testSearchByArtist() throws Exception {
        doInsertTestData();

        List<OnAirSong> result = mTarget.search("testArtist-01");
        Assert.assertEquals(3, result.size());
    }

    @SmallTest
    public void testSearchByArtistAndDateRange() throws Exception {
        doInsertTestData();

        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();

        from.set(2000, 1, 23, 0, 0, 0);
        to.set(2000, 1, 24, 0, 0, 0);

        List<OnAirSong> result = mTarget.search("testArtist-01", from, to);
        Assert.assertEquals(2, result.size());
    }

    @SmallTest
    public void testSearchByDateRange() throws Exception {
        doInsertTestData();

        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();

        from.set(2000, 1, 23, 20, 0, 0);
        to.set(2000, 1, 24, 7, 0, 0);

        // testdataの中の、2000/1/23 20:00 ~ 1/24 7:00 の曲は5つ
        List<OnAirSong> result = mTarget.search(from, to);
        Assert.assertEquals(5, result.size());
    }

    @SmallTest
    public void testDelete() throws Exception {
        doInsertTestData();

        // 2000/1/23のStation01の曲を全て消す
        int deletedRows = mTarget.delete(2000, 1, 23, "Station01");
        Assert.assertEquals(3, deletedRows);
    }

    @SmallTest
    public void testUpdate() throws Exception {
        doInsertTestData();

        OnAirSong updateData = makeTestData(2000, 1, 23, 13, 0, "-000", "Station01");
        int updated = mTarget.update(updateData);
        Assert.assertEquals(1, updated);
    }

}
