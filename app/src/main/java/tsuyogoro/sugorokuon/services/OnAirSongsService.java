/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;

import com.google.android.gms.tagmanager.Container;
import com.google.android.gms.tagmanager.ContainerHolder;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.SugorokuonApplication;
import tsuyogoro.sugorokuon.models.apis.OnAirSongsApi;
import tsuyogoro.sugorokuon.models.apis.StationApi;
import tsuyogoro.sugorokuon.models.entities.Feed;
import tsuyogoro.sugorokuon.models.entities.Station;
import tsuyogoro.sugorokuon.models.prefs.AutoUpdateSettingPreference;
import tsuyogoro.sugorokuon.network.IRadikoFeedFetcher;
import tsuyogoro.sugorokuon.network.gtm.ContainerHolderLoader;
import tsuyogoro.sugorokuon.network.gtm.ContainerHolderSingleton;
import tsuyogoro.sugorokuon.utils.SugorokuonLog;

/**
 * 番組表の更新処理を全てここに入れる
 *
 * - Intentを投げて、あとはヨロシク系
 *   - 最新の曲情報を取得してDBへ登録
 *     - 定期的にタイマーを発行して更新をしていく
 *       - 間隔をgoogle tag managerからコントロールできるようにしたい
 *     - この機能が不要なユーザはon/offできる
 *   - 週に一度とか、定期的に「よくかかった曲」のような情報を出す
 *     - 集計
 *     - Notificationの発行
 *   - タイマーのセットをする
 *     - 地域更新など行った後に叩けるようにしたい
 *
 */
public class OnAirSongsService extends IntentService {

    /**
     * 最新のonAir曲を取ってきてDBを更新するAction
     * どの局から情報を取ってくるか、などは全てServiceの中で処理
     */
    public static final String ACTION_FETCH_ON_AIR_SONGS =
            SugorokuonServiceUtil.action("action_fetch_on_air_songs");

    /**
     * 曲情報の更新等を行うTimerをセットする
     */
    public static final String ACTION_SET_ON_AIR_SONGS_TIMER =
            SugorokuonServiceUtil.action("action_set_on_air_songs_timer");

    /**
     * 曲情報の更新等を行うTimerをキャンセルする
     */
    public static final String ACTION_CANCEL_ON_AIR_SONGS_TIMER =
            SugorokuonServiceUtil.action("action_cancel_on_air_songs_timer");

    /**
     * よくかかっている曲の情報などをNotificationでユーザに通知
     */
    public static final String ACTION_NOTIFY_ON_AIR_SONGS_INFO =
            SugorokuonServiceUtil.action("action_notify_on_air_songs_info");

    /**
     * 更新の際に、現在保存されているデータを消すかどうか
     *
     */
    public static final String EXTRA_CLEAR_OLD_DATA = "extra_clear_old_data";

    /**
     * Fetchした時の通知
     */
    public static final String NOTIFY_ON_FETCH_LATEST_SETLIST =
            SugorokuonServiceUtil.action("notify_on_fetch_latest_setlist");

    private static final int DEFAULT_FETCH_PERIOD_HOUR = 2;

    @Inject
    IRadikoFeedFetcher feedFetcher;

    public OnAirSongsService() {
        // (Memo) IntentServiceのコンストラクタ、worker threadの名前を渡す
        // https://groups.google.com/forum/#!topic/android-developers/HVBnJ15amVc
        super("tsuyogoro.sugorokuon.service.OnAirSongsService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((SugorokuonApplication) getApplication()).component().inject(this);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {

        // Google tag managerの準備ができていなかったら、loadしてから処理開始
        if (null != ContainerHolderSingleton.getContainerHolder()) {
            SugorokuonLog.d("ContainerHolder has been prepared");
            doHandleIntent(intent);
        } else {
            SugorokuonLog.d("ContainerHolder has NOT been prepared");
            ContainerHolderLoader.load(this, new ContainerHolderLoader.OnLoadListener() {
                @Override
                public void onContainerHolderAvailable() {
                    // Main threadへ通知が返ってくるので、worker threadを生成
                    // (Memo : main threadでのhttp通信はExceptionが発生する)
                    Thread workerThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            doHandleIntent(intent);
                        }
                    });
                    workerThread.start();
                }

                @Override
                public void onLatestContainerAvailable(String newVersion) {
                    // TODO : 何かする？
                }
            });
        }
    }

    private void doHandleIntent(Intent intent) {

        final String action = intent.getAction();

        if (ACTION_FETCH_ON_AIR_SONGS.equals(action)) {
            cancelFetchTimer();
            setNextFetchTimer();
            fetchLatestSetlist(intent.getBooleanExtra(EXTRA_CLEAR_OLD_DATA, false));
        }
        else if (ACTION_SET_ON_AIR_SONGS_TIMER.equals(action)) {
            cancelFetchTimer();
            setNextFetchTimer();
        }
        else if (ACTION_CANCEL_ON_AIR_SONGS_TIMER.equals(action)) {
            cancelFetchTimer();
        }
        else if (ACTION_NOTIFY_ON_AIR_SONGS_INFO.equals(action)) {
            // TODO : 何を通知したら良いか決めたら実装
        }
    }

    private void fetchLatestSetlist(boolean clearData) {
        StationApi stationDb = new StationApi(this);
        OnAirSongsApi onAirSongDb = new OnAirSongsApi(this);

        if (clearData) {
            onAirSongDb.clear();
        }

        // OnAir曲を提供する局をload
        List<Station> onAirSongProviders = stationDb.load(true);

        // Feedを取得してDBに局情報を入れる
        for (Station s : onAirSongProviders) {
            Feed f = feedFetcher.fetch(s.id);
            if (null != f) {
                int added = onAirSongDb.insert(f.onAirSongs).size();
                SugorokuonLog.d("Fetch latest songs : " + s.name + " - " + added);
            }
        }

        sendBroadcast(new Intent(NOTIFY_ON_FETCH_LATEST_SETLIST));
    }

    private int fetchSetlistAfterByHour() {
        int fetchPeriod = DEFAULT_FETCH_PERIOD_HOUR;
        ContainerHolder holder = ContainerHolderSingleton.getContainerHolder();
        if (null != holder) {
            Container container = holder.getContainer();
            String containerKey = getString(R.string.gtm_key_interval_song_update_by_hour);
            fetchPeriod = (int) container.getLong(containerKey);
        }
        return (0 < fetchPeriod) ? fetchPeriod : DEFAULT_FETCH_PERIOD_HOUR;
    }

    private PendingIntent pendingIntentToFetchSetlist() {
        Intent intent = new Intent(ACTION_FETCH_ON_AIR_SONGS);
        intent.setPackage(getPackageName());
        return PendingIntent.getService(this, -1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private void setNextFetchTimer() {
        if (AutoUpdateSettingPreference.autoUpdateOnAirSongs(this)) {
            Calendar nextFetchTime = Calendar.getInstance(Locale.JAPAN);
            nextFetchTime.add(Calendar.HOUR_OF_DAY, fetchSetlistAfterByHour());
            SugorokuonServiceUtil.setNextTimer(pendingIntentToFetchSetlist(),
                    nextFetchTime.getTimeInMillis(), this);
        }
    }

    private void cancelFetchTimer() {
        SugorokuonServiceUtil.cancelTimer(pendingIntentToFetchSetlist(), this);
    }

}
