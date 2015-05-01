/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import junit.framework.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import tsuyogoro.sugorokuon.constants.Area;
import tsuyogoro.sugorokuon.models.apis.OnAirSongsApi;
import tsuyogoro.sugorokuon.models.apis.StationApi;
import tsuyogoro.sugorokuon.models.apis.TimeTableApi;
import tsuyogoro.sugorokuon.models.prefs.AreaSettingPreference;
import tsuyogoro.sugorokuon.models.prefs.RecommendWordPreference;

public class UTimeTableServiceTest extends AndroidTestCase {

    private StationApi mStationApi;

    private OnAirSongsApi mOnAirSongApi;

    private TimeTableApi mTimeTableApi;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mStationApi = new StationApi(getContext());
        mOnAirSongApi = new OnAirSongsApi(getContext());
        mTimeTableApi = new TimeTableApi(getContext());

        mStationApi.clear();
        mOnAirSongApi.clear();
        mTimeTableApi.clear();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(getContext());
        p.edit().clear();

        mStationApi.clear();
        mOnAirSongApi.clear();
        mTimeTableApi.clear();

        mStationApi = null;
        mOnAirSongApi = null;
        mTimeTableApi = null;
    }

    @LargeTest
    public void testFetchActions() throws Exception {

        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(getContext());
        p.edit().putBoolean(AreaSettingPreference.getAreaPreferenceKey(Area.CHIBA), true).commit();
        p.edit().putString(RecommendWordPreference.getKey(0), "岡村隆史").commit();
        p.edit().putString(RecommendWordPreference.getKey(1), "music").commit();

        // 適宜切り替えて動作確認
        boolean testActionWeekly = false;
        boolean testActionDaily = false;

        doTestStationAndWeeklyUpdate();

        if (testActionWeekly) {
            doTestWeeklyUpdate();
        }

        if (testActionDaily) {
            doTestTodaysUpdate();
        }

        // OnAirSoonの通知
        Intent intent = new Intent(TimeTableService.ACTION_NOTIFY_ONAIR_SOON);
        intent.setPackage("tsuyogoro.sugorokuon");
        getContext().startService(intent);
    }

    private void doTestStationAndWeeklyUpdate() throws Exception {

        final CountDownLatch latchForStationWeekly = new CountDownLatch(1);

        IntentFilter filter = new IntentFilter(
                TimeTableService.NotifyAction.ACTION_NOTIFY_UPDATE_COMPLETED);
        IntentFilter errorFilter = new IntentFilter(
                TimeTableService.NotifyAction.ACTION_NOTIFY_UPDATE_FAILED);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (TimeTableService.NotifyAction.ACTION_NOTIFY_UPDATE_COMPLETED.equals(action)) {
                    latchForStationWeekly.countDown();
                }
                else if (TimeTableService.NotifyAction.ACTION_NOTIFY_UPDATE_FAILED.equals(action)) {
                    Assert.assertTrue("Failed is notified", false);
                }
            }
        };
        getContext().registerReceiver(receiver, filter);
        getContext().registerReceiver(receiver, errorFilter);

        Intent intent = new Intent(TimeTableService.ACTION_UPDATE_STATION_AND_TIME_TABLE);
        intent.setPackage("tsuyogoro.sugorokuon");
        getContext().startService(intent);

        // Serviceが走る前は空っぽ
        Assert.assertTrue(mStationApi.load().isEmpty());

        latchForStationWeekly.await(5, TimeUnit.MINUTES);

        // Serviceが走ったら何か入っているはず (musicみたいなキーワードだし)
        Assert.assertFalse(mStationApi.load().isEmpty());
        Assert.assertNotSame(0, mTimeTableApi.fetchRecommends().size());
    }

    private void doTestWeeklyUpdate() throws Exception {

        final CountDownLatch latch = new CountDownLatch(1);

        IntentFilter filter = new IntentFilter(
                TimeTableService.NotifyAction.ACTION_NOTIFY_UPDATE_COMPLETED);
        IntentFilter errorFilter = new IntentFilter(
                TimeTableService.NotifyAction.ACTION_NOTIFY_UPDATE_FAILED);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (TimeTableService.NotifyAction.ACTION_NOTIFY_UPDATE_COMPLETED.equals(action)) {
                    latch.countDown();
                }
                else if (TimeTableService.NotifyAction.ACTION_NOTIFY_UPDATE_FAILED.equals(action)) {
                    Assert.assertTrue("Failed to update weekly timeTable is notified", false);
                }
            }
        };
        getContext().registerReceiver(receiver, filter);
        getContext().registerReceiver(receiver, errorFilter);

        Intent intent = new Intent(TimeTableService.ACTION_UPDATE_WEEKLY_TIME_TABLE);
        intent.setPackage("tsuyogoro.sugorokuon");
        getContext().startService(intent);

        latch.await(5, TimeUnit.MINUTES);

        Assert.assertFalse(mStationApi.load().isEmpty());
        Assert.assertNotSame(0, mTimeTableApi.fetchRecommends().size());
    }

    private void doTestTodaysUpdate() throws Exception {

        final CountDownLatch latch = new CountDownLatch(1);

        IntentFilter filter = new IntentFilter(
                TimeTableService.NotifyAction.ACTION_NOTIFY_UPDATE_COMPLETED);
        IntentFilter errorFilter = new IntentFilter(
                TimeTableService.NotifyAction.ACTION_NOTIFY_UPDATE_FAILED);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (TimeTableService.NotifyAction.ACTION_NOTIFY_UPDATE_COMPLETED.equals(action)) {
                    latch.countDown();
                }
                else if (TimeTableService.NotifyAction.ACTION_NOTIFY_UPDATE_FAILED.equals(action)) {
                    Assert.assertTrue("Failed to update today's timeTable is notified", false);
                }
            }
        };
        getContext().registerReceiver(receiver, filter);
        getContext().registerReceiver(receiver, errorFilter);

        Intent intent = new Intent(TimeTableService.ACTION_UPDATE_TODAYS_TIME_TABLE);
        intent.setPackage("tsuyogoro.sugorokuon");
        getContext().startService(intent);

        latch.await(5, TimeUnit.MINUTES);

        Assert.assertFalse(mStationApi.load().isEmpty());
        Assert.assertNotSame(0, mTimeTableApi.fetchRecommends().size());
    }



}
