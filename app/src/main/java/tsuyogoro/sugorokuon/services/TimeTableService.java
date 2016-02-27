/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.constants.Area;
import tsuyogoro.sugorokuon.constants.StationLogoSize;
import tsuyogoro.sugorokuon.models.apis.ProgramSearchKeywordFilter;
import tsuyogoro.sugorokuon.models.apis.StationApi;
import tsuyogoro.sugorokuon.models.apis.TimeTableApi;
import tsuyogoro.sugorokuon.models.entities.OnedayTimetable;
import tsuyogoro.sugorokuon.models.entities.Program;
import tsuyogoro.sugorokuon.models.entities.Station;
import tsuyogoro.sugorokuon.models.prefs.AreaSettingPreference;
import tsuyogoro.sugorokuon.models.prefs.AutoUpdateSettingPreference;
import tsuyogoro.sugorokuon.models.prefs.RecommendWordPreference;
import tsuyogoro.sugorokuon.models.prefs.RemindTimePreference;
import tsuyogoro.sugorokuon.models.prefs.UpdatedDateManager;
import tsuyogoro.sugorokuon.network.gtm.ContainerHolderLoader;
import tsuyogoro.sugorokuon.network.gtm.ContainerHolderSingleton;
import tsuyogoro.sugorokuon.network.radikoapi.StationsFetcher;
import tsuyogoro.sugorokuon.network.radikoapi.TimeTableFetcher;
import tsuyogoro.sugorokuon.utils.SugorokuonLog;

/**
 * 必要ならプロセス分けよう
 * http://android.keicode.com/basics/services-bound-with-ipc-aidl.php
 */
public class TimeTableService extends Service {

    /**
     * 対象の局をupdateした後、
     * ACTION_UPDATE_WEEKLY_TIME_TABLE 同様の処理を行う
     */
    public static final String ACTION_UPDATE_STATION_AND_TIME_TABLE =
            SugorokuonServiceUtil.action("action_update_station_and_time_table");

    /**
     * 一週間分の番組表をupdate
     * (今日の分は最新のものを取得)
     */
    public static final String ACTION_UPDATE_WEEKLY_TIME_TABLE =
            SugorokuonServiceUtil.action("action_update_weekly_time_table");

    /**
     * 今日の番組表をupdate
     */
    public static final String ACTION_UPDATE_TODAYS_TIME_TABLE =
            SugorokuonServiceUtil.action("action_update_todays_time_table");

    /**
     * そろそろ放送される番組をNotificationで通知
     */
    public static final String ACTION_NOTIFY_ONAIR_SOON =
            SugorokuonServiceUtil.action("action_notify_onair_soon");

    /**
     * Reminder、番組表更新のTimerをupdateする。
     */
    public static final String ACTION_UPDATE_TIMER =
            SugorokuonServiceUtil.action("action_update_timer");

    /**
     * Reminder、番組表更新のTimerをclearする。
     */
    public static final String ACTION_UPDATE_RECOMMENDS =
            SugorokuonServiceUtil.action("action_update_recommends");

    /**
     * Reminder、番組表更新のTimerをclearする。
     */
    public static final String ACTION_CANCEL_TIMER =
            SugorokuonServiceUtil.action("action_cancel_timer");

    /**
     * trueにすると、進捗をnotificationで出すようになる
     *
     */
    public static final String EXTRA_NOTIFY_PROGRESS = "extra_notify_progress";

    /**
     * Serviceの結果を通知する際のaction
     * 下記Actionのbroadcast登録をすることでserviceからの通知を受け取ることができる
     */
    public static class NotifyAction {

        public static final String ACTION_NOTIFY_UPDATE_COMPLETED =
                SugorokuonServiceUtil.action("result_update_completed");

        public static final String ACTION_NOTIFY_UPDATE_FAILED =
                SugorokuonServiceUtil.action("result_update_failed");

        public static final String ACTION_NOTIFY_WEEKLY_FETCH_PROGRESS =
                SugorokuonServiceUtil.action("weekly_timetable_fetch_progress");

        public static final String ACTION_NOTIFY_RECOMMEND_UPDATED =
                SugorokuonServiceUtil.action("recommend_updated");

        public static final String EXTRA_FAILED_REASON = "failed_reason_failed_update";

        public static final String EXTRA_WEEKLY_FETCH_PROGRESS_TOTAL =
                "weekly_fetch_progress_total";

        public static final String EXTRA_WEEKLY_FETCH_PROGRESS_FETCHED =
                "weekly_fetch_progress_fetched";

    }

    /**
     * Updateの進捗通知に使うnotificationのID
     *
     */
    public static final int NOTIFICATION_ID_FOR_UPDATE_PROGRESS = 100;

    private static final StationLogoSize LOGO_SIZE = StationLogoSize.LARGE;

    private static String LOGO_CACHE_DIR_NAME = "stationlogo";

    private StationApi mStationApi;

    private TimeTableApi mTimeTableApi;

    private AsyncTask<Void, List<Station>, Boolean> mWeeklyUpdateTask;

    private AsyncTask<Void, Void, Boolean> mTodaysUpdateTask;

    public class TimeTableServiceBinder extends Binder {
        /**
         * {@link TimeTableService#ACTION_UPDATE_STATION_AND_TIME_TABLE}
         * もしくは
         * {@link TimeTableService#ACTION_UPDATE_WEEKLY_TIME_TABLE} が処理中かどうかを返す
         *
         * @return
         */
        public boolean runningWeeklyUpdate() {
            return TimeTableService.this.runningWeeklyUpdate();
        }

        /**
         * {@link TimeTableService#ACTION_UPDATE_TODAYS_TIME_TABLE} が処理中かどうかを返す
         *
         * @return
         */
        public boolean runningTodaysUpdate() {
            return TimeTableService.this.runningTodaysUpdate();
        }
    }

    private IBinder mBinder = new TimeTableServiceBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        mStationApi = new StationApi(this);
        mTimeTableApi = new TimeTableApi(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimeTableApi = null;
        mStationApi = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        // Google tag managerの準備ができていなかったら、loadしてから処理開始
        if (null != intent) {
            if (null != ContainerHolderSingleton.getContainerHolder()) {
                doStartCommand(intent);
            } else {
                ContainerHolderLoader.load(this, new ContainerHolderLoader.OnLoadListener() {
                    @Override
                    public void onContainerHolderAvailable() {
                        // Main threadへ通知が返ってくる
                        doStartCommand(intent);
                    }
                });
            }
        }

        return Service.START_NOT_STICKY;
    }

    private void doStartCommand(Intent intent) {
        String action = intent.getAction();

        if (action.equals(ACTION_UPDATE_STATION_AND_TIME_TABLE) ||
                action.equals(ACTION_UPDATE_WEEKLY_TIME_TABLE)) {
            if (!runningWeeklyUpdate()) {
                synchronized (this) {
                    boolean updateStation = action.equals(ACTION_UPDATE_STATION_AND_TIME_TABLE);
                    mWeeklyUpdateTask = generateWeeklyUpdateTask(updateStation,
                            intent.getBooleanExtra(EXTRA_NOTIFY_PROGRESS, false));
                }
                mWeeklyUpdateTask.execute();
            } else {
                SugorokuonLog.d("Requested to start weekly update while another update is running");
            }
        } else if (ACTION_UPDATE_TODAYS_TIME_TABLE.equals(action)) {
            if (!runningTodaysUpdate()) {
                synchronized (this) {
                    mTodaysUpdateTask = generateTodaysUpdateTask();
                }
                mTodaysUpdateTask.execute();
            }
        } else if (ACTION_NOTIFY_ONAIR_SOON.equals(action)) {
            ProgramRemindSubmitter.notifyOnAirSoon(this);
            updateOnAirSoonTimer();
        } else if (ACTION_UPDATE_TIMER.equals(action)) {
            updateWeeklyTimer();
            updateTodaysTimer();
            updateOnAirSoonTimer();
        } else if (ACTION_CANCEL_TIMER.equals(action)) {
            cancelWeeklyTimer();
            cancelTodaysTimer();
            cancelOnAirSoonTimer();
        } else if (ACTION_UPDATE_RECOMMENDS.equals(action)) {
            updateRecommends();
            updateOnAirSoonTimer();
        }
    }


    private boolean runningWeeklyUpdate() {
        return (null != mWeeklyUpdateTask
                && AsyncTask.Status.RUNNING.equals(mWeeklyUpdateTask.getStatus()));
    }

    private boolean runningTodaysUpdate() {
        return (null != mTodaysUpdateTask
                && mTodaysUpdateTask.getStatus().equals(AsyncTask.Status.RUNNING));
    }

    private boolean updateStation() {

        String logoCachedDir;
        try {
            PackageManager m = getPackageManager();
            PackageInfo p = m.getPackageInfo(getPackageName(), 0);
            logoCachedDir = p.applicationInfo.dataDir + File.separator + LOGO_CACHE_DIR_NAME;
        } catch (PackageManager.NameNotFoundException e) {
            SugorokuonLog.w("Error Package name not found " + e);
            return false;
        }

        Area[] areas = AreaSettingPreference.getTargetAreas(this);

        List<Station> stations = StationsFetcher.fetch(areas, LOGO_SIZE, logoCachedDir);

        if (null == stations) {
            return false;
        }

        mStationApi.clear();
        mStationApi.insert(stations);

        return true;
    }

    // (メモ 15/4/9) :
    // 見た感じweeklyを取っても、todayの分はupdateされているっぽい
    // weeklyを取った後にあえてtodayの取得に行ったほうが良いかと思ったが、そうでもなさそう
    private boolean updateWeeklyTimeTable(
            TimeTableFetcher.IWeeklyFetchProgressListener progressListener) {

        List<Station> stations = mStationApi.load();

        List<OnedayTimetable> timeTable = TimeTableFetcher.fetchWeeklyTable(
                stations, progressListener);

        boolean isSuccess = (timeTable.size() == stations.size() * 7);
        if (isSuccess) {
            mTimeTableApi.clear();
            mTimeTableApi.insert(timeTable);

            UpdatedDateManager.updateLastUpdateTime(this);

            updateRecommends();
            updateOnAirSoonTimer();
        } else {
            SugorokuonLog.w("Number of weekly timetable is shorter than expected : " +
                    Integer.toString(stations.size() * 7) + " but " + timeTable.size());
        }

        updateWeeklyTimer();
        updateTodaysTimer();

        return isSuccess;
    }

    private boolean updateTodaysTimeTable() {

        List<Station> stations = mStationApi.load();

        List<OnedayTimetable> timeTables = TimeTableFetcher.fetchTodaysTable(stations);

        boolean isSuccess = (timeTables.size() == stations.size());
        if (isSuccess) {
            mTimeTableApi.update(timeTables);

            UpdatedDateManager.updateLastUpdateTime(this);

            updateRecommends();
            updateOnAirSoonTimer();
        } else {
            SugorokuonLog.w("Number of todays timetable is shorter than expected : " +
                    Integer.toString(stations.size() * 7) + " but " + timeTables.size());
        }

        updateTodaysTimer();

        return isSuccess;
    }

    private void updateRecommends() {

        List<String> setWords = RecommendWordPreference.getKeyWord(this);

        if (0 < setWords.size()) {
            String[] filterWords = new String[setWords.size()];
            for (int i = 0; i < setWords.size(); i++) {
                filterWords[i] = setWords.get(i);
            }

            mTimeTableApi.updateRecommends(new ProgramSearchKeywordFilter(filterWords));

            // ------------------------------------------------
            // TODO : この辺でupdateで使ったkeywordをGAに送りたい
            // ------------------------------------------------

        } else {
            mTimeTableApi.resetRecommend();
        }

        sendBroadcast(new Intent(NotifyAction.ACTION_NOTIFY_RECOMMEND_UPDATED));
    }

    private PendingIntent pendingIntentForWeeklyUpdate() {
        return SugorokuonServiceUtil.pendingIntentForTimer(ACTION_UPDATE_WEEKLY_TIME_TABLE, this);
    }

    private PendingIntent pendingIntentForTodaysUpdate() {
        return SugorokuonServiceUtil.pendingIntentForTimer(ACTION_UPDATE_TODAYS_TIME_TABLE, this);
    }

    private PendingIntent pendingIntentForOnAirSoon() {
        return SugorokuonServiceUtil.pendingIntentForTimer(ACTION_NOTIFY_ONAIR_SOON, this);
    }

    private void cancelWeeklyTimer() {
        SugorokuonServiceUtil.cancelTimer(pendingIntentForWeeklyUpdate(), this);
    }

    private void cancelTodaysTimer() {
        SugorokuonServiceUtil.cancelTimer(pendingIntentForTodaysUpdate(), this);
    }

    private void cancelOnAirSoonTimer() {
        SugorokuonServiceUtil.cancelTimer(pendingIntentForOnAirSoon(), this);
    }

    private void updateWeeklyTimer() {
        cancelWeeklyTimer();

        if (AutoUpdateSettingPreference.autoUpdateWeekly(this)) {
            SugorokuonServiceUtil.setNextTimer(pendingIntentForWeeklyUpdate(),
                    UpdatedDateManager.getInstance(this).nextWeeklyUpdateTime(), this);
        }
    }

    private void updateTodaysTimer() {
        cancelTodaysTimer();

        if (AutoUpdateSettingPreference.autoUpdateToday(this)) {
            SugorokuonServiceUtil.setNextTimer(pendingIntentForTodaysUpdate(),
                    UpdatedDateManager.getInstance(this).nextDailyUpdateTime(), this);
        }
    }

    private void updateOnAirSoonTimer() {
        cancelOnAirSoonTimer();

        Calendar currentTime = Calendar.getInstance(Locale.JAPAN);
        List<Program> recommends = mTimeTableApi.fetchRecommends(currentTime);

        if (!recommends.isEmpty()) {
            Calendar nextRemindTime = RemindTimePreference.getNotifyTime(this).
                    calculateNotifyTime(recommends);

            if (null != nextRemindTime) {
                SugorokuonServiceUtil.setNextTimer(
                        pendingIntentForOnAirSoon(), nextRemindTime.getTimeInMillis(), this);
            }
        }
    }

    private void broadcastUpdateComplete() {
        sendBroadcast(new Intent(NotifyAction.ACTION_NOTIFY_UPDATE_COMPLETED));
    }

    private void broadcastUpdateError(String errorMsg) {
        Intent notification = new Intent(NotifyAction.ACTION_NOTIFY_UPDATE_FAILED);
        notification.putExtra(NotifyAction.EXTRA_FAILED_REASON, "Failed to update station");
        sendBroadcast(notification);
    }

    private AsyncTask<Void, List<Station>, Boolean> generateWeeklyUpdateTask(
            final boolean toUpdateStation, final boolean notifyProgress) {

        return new AsyncTask<Void, List<Station>, Boolean>() {

            private UpdateProgressSubmitter mNotificationSubmitter;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                if (notifyProgress) {
                    mNotificationSubmitter = new UpdateProgressSubmitter(Calendar.getInstance());
                    mNotificationSubmitter.notifyStart(TimeTableService.this);
                }
            }

            @Override
            protected Boolean doInBackground(Void... params) {

                boolean result = false;

                if (toUpdateStation) {
                    result = updateStation();
                    if (!result) {
                        SugorokuonLog.e("Failed to update station in onStartCommand");
                        broadcastUpdateError("Failed to update station list");
                    }
                }

                if (!toUpdateStation || result) {
                    result = updateWeeklyTimeTable(new TimeTableFetcher.IWeeklyFetchProgressListener() {
                        @Override
                        public void onProgress(List<Station> fetched, List<Station> requested) {
                            publishProgress(fetched, requested);
                        }
                    });
                    if (!result) {
                        SugorokuonLog.e("Failed to update weekly timetable in onStartCommand");
                        broadcastUpdateError("Failed to update weekly timeTable list");
                    }
                }

                return result;
            }

            @Override
            protected void onProgressUpdate(List<Station>... values) {
                super.onProgressUpdate(values);

                List<Station> fetched = values[0];
                List<Station> requested = values[1];

                Intent progress = new Intent(NotifyAction.ACTION_NOTIFY_WEEKLY_FETCH_PROGRESS);
                progress.putExtra(NotifyAction.EXTRA_WEEKLY_FETCH_PROGRESS_TOTAL, requested.size());
                progress.putExtra(NotifyAction.EXTRA_WEEKLY_FETCH_PROGRESS_FETCHED, fetched.size());
                sendBroadcast(progress);

                if (notifyProgress && null != mNotificationSubmitter) {
                    mNotificationSubmitter.notifyProgress(
                            TimeTableService.this, requested.size(), fetched.size());
                }
            }

            @Override
            protected void onPostExecute(Boolean isSuccess) {
                super.onPostExecute(isSuccess);

                if (isSuccess) {
                    if (notifyProgress && null != mNotificationSubmitter) {
                        mNotificationSubmitter.notifyComplete(TimeTableService.this);
                    }
                    broadcastUpdateComplete();

                    // フルでupdateする時は、曲情報更新も行う
                    // (Weeklyの先頭で、OnAirSongのDBをクリアするようにしたいが…)
                    if (toUpdateStation) {
                        Intent intentForOnAirSongUpdate = new Intent(OnAirSongsService.ACTION_FETCH_ON_AIR_SONGS);
                        intentForOnAirSongUpdate.setPackage(getPackageName());
                        intentForOnAirSongUpdate.putExtra(OnAirSongsService.EXTRA_CLEAR_OLD_DATA, true);
                        startService(intentForOnAirSongUpdate);
                    }
                } else {
                    if (notifyProgress && null != mNotificationSubmitter) {
                        mNotificationSubmitter.notifyError(TimeTableService.this,
                                R.string.progress_notification_text_failed);
                    }
                    broadcastUpdateError("Failed to update " +
                            (toUpdateStation ? "station and weekly time table" : "weekly time table"));
                }
            }
        };
    }

    private AsyncTask<Void, Void, Boolean> generateTodaysUpdateTask() {

        // NOTE : today's time tableではNotificationを出さない (けど良いのか考える)
        return new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                return updateTodaysTimeTable();
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (result) {
                    broadcastUpdateComplete();
                } else {
                    broadcastUpdateError("Failed to update today's timetable");
                }
            }
        };
    }
}
