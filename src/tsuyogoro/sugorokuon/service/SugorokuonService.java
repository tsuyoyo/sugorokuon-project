/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * アプリがbackgroundに居る際に走り得る処理の入口になるService。
 * 
 * @author Tsuyoyo
 *
 */
public class SugorokuonService extends Service {
	
	/** DataViewFlowに局と番組のDataをloadする。
	    DBから読み出すかもしれないし、Webに取りにいくかもしれない。 */
	public static final String ACTION_LOAD_PROGRAM_DATA = 
		"tsuyogoro.sugorokuon.service.action_load_program_data";

	// そろそろ放送される番組をNotificationで通知
	public static final String ACTION_NOTIFY_ONAIR_SOON   = 
		"tsuyogoro.sugorokuon.service.action_notify_onair_soon";
	
	// ラジオアプリを起動する。
	public static final String ACTION_LAUNCH_RADIO_APP = 
		"tsuyogoro.sugorokuon.service.action_launch_radioapp";

	// 曲をwebから検索する。
	public static final String ACTION_SEARCH_SONG_ON_WEB = 
		"tsuyogoro.sugorokuon.service.action_search_song_on_web";	
	
	// Reminder、番組表更新のTimerをupdateする。
	public static final String ACTION_UPDATE_TIMER = 
		"tsuyogoro.sugorokuon.service.action_timer_update";
	
	// 番組Reminderのintentの中に含める。サーバから取れる、yyyyMMddhhmmssの形式。
	public static final String EXTRA_ON_AIR_TIME = 
		"tsuyogoro.sugorokuon.service.extra_on_air_time";
	
	// NotificationのID
	static final int PROGRESS_NOTIFICATION_ID = 100;
	
	// ACTION_SEARCH_SONG_ON_WEBにつかうExtra field
	static public final String EXTRA_ARTIST = "extra_artist";
	static public final String EXTRA_SONG_TITLE = "extra_song_title";	
	
	private RecommendReminderReserver mRemindReserver;
	private ProgramUpdateReserver mUpdateReserver;
	
	@Override
	public void onCreate() {
		super.onCreate();
		mRemindReserver = new RecommendReminderReserver();
		mUpdateReserver = new ProgramUpdateReserver();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		
		String action = intent.getAction();
		
		/*
		 * Recommendリストの更新
		 */
		if(ACTION_LOAD_PROGRAM_DATA.equals(action)) {
			ActionProcessorUpdateDatabase proc = 
					new ActionProcessorUpdateDatabase(this, mRemindReserver, mUpdateReserver);
			proc.invokeUpdateProgramDatabase();
		}
		/*
		 * まもなく放送開始の番組を通知する。
		 */
		else if(ACTION_NOTIFY_ONAIR_SOON.equals(action)) {
			String onAirTime = intent.getExtras().getString(SugorokuonService.EXTRA_ON_AIR_TIME);
			ActionProcessorNotifyOnAirSoon proc = new ActionProcessorNotifyOnAirSoon(this);
			proc.invokeNotifyOnAirSoon(onAirTime);
		}
		/*
		 * ラジオアプリの起動。
		 * (メモ：Notificationなどから起動するユースケースも今後あるかもなので、Serviceにこの機能を置く)
		 */
		else if(ACTION_LAUNCH_RADIO_APP.equals(action)) {
			ActionProcessorLaunchRadioApp launcher = new ActionProcessorLaunchRadioApp();
			launcher.launchRadioApp(this);
		}
		else if(ACTION_SEARCH_SONG_ON_WEB.equals(action)) {
			String artist = intent.getExtras().getString(EXTRA_ARTIST);
			String song   = intent.getExtras().getString(EXTRA_SONG_TITLE);
			ActionProcessSearchSongOnWeb launcher = new ActionProcessSearchSongOnWeb();
			launcher.launchSearchApp(this, artist, song);
		}
		/*
		 * Timerをセットしなおす。
		 */
		else if(ACTION_UPDATE_TIMER.equals(action)) {
			ActionProcessorUpdateTimer proc = 
					new ActionProcessorUpdateTimer(this, mRemindReserver, mUpdateReserver);
			proc.processTimerUpdate();
		}
		
		// （メモ : http://d.hatena.ne.jp/adsaria/20100914/1284435095)
		// START_NOT_STICKY または START_REDELIVER_INTENT は送られてきたコマンドを処理する間だけ実行するService。
		// ⇒ つまり、リクエストされた処理が終わったら、このサービスは割と早いタイミングで死ぬ。
		return Service.START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
