/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * �A�v����background�ɋ���ۂɑ��蓾�鏈���̓����ɂȂ�Service�B
 * 
 * @author Tsuyoyo
 *
 */
public class SugorokuonService extends Service {
	
	/** DataViewFlow�ɋǂƔԑg��Data��load����B
	    DB����ǂݏo����������Ȃ����AWeb�Ɏ��ɂ�����������Ȃ��B */
	public static final String ACTION_LOAD_PROGRAM_DATA = 
		"tsuyogoro.sugorokuon.service.action_load_program_data";

	// ���낻����������ԑg��Notification�Œʒm
	public static final String ACTION_NOTIFY_ONAIR_SOON   = 
		"tsuyogoro.sugorokuon.service.action_notify_onair_soon";
	
	// ���W�I�A�v�����N������B
	public static final String ACTION_LAUNCH_RADIO_APP = 
		"tsuyogoro.sugorokuon.service.action_launch_radioapp";

	// �Ȃ�web���猟������B
	public static final String ACTION_SEARCH_SONG_ON_WEB = 
		"tsuyogoro.sugorokuon.service.action_search_song_on_web";	
	
	// Reminder�A�ԑg�\�X�V��Timer��update����B
	public static final String ACTION_UPDATE_TIMER = 
		"tsuyogoro.sugorokuon.service.action_timer_update";
	
	// �ԑgReminder��intent�̒��Ɋ܂߂�B�T�[�o�������AyyyyMMddhhmmss�̌`���B
	public static final String EXTRA_ON_AIR_TIME = 
		"tsuyogoro.sugorokuon.service.extra_on_air_time";
	
	// Notification��ID
	static final int PROGRESS_NOTIFICATION_ID = 100;
	
	// ACTION_SEARCH_SONG_ON_WEB�ɂ���Extra field
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
		 * Recommend���X�g�̍X�V
		 */
		if(ACTION_LOAD_PROGRAM_DATA.equals(action)) {
			ActionProcessorUpdateDatabase proc = 
					new ActionProcessorUpdateDatabase(this, mRemindReserver, mUpdateReserver);
			proc.invokeUpdateProgramDatabase();
		}
		/*
		 * �܂��Ȃ������J�n�̔ԑg��ʒm����B
		 */
		else if(ACTION_NOTIFY_ONAIR_SOON.equals(action)) {
			String onAirTime = intent.getExtras().getString(SugorokuonService.EXTRA_ON_AIR_TIME);
			ActionProcessorNotifyOnAirSoon proc = new ActionProcessorNotifyOnAirSoon(this);
			proc.invokeNotifyOnAirSoon(onAirTime);
		}
		/*
		 * ���W�I�A�v���̋N���B
		 * (�����FNotification�Ȃǂ���N�����郆�[�X�P�[�X�����゠�邩���Ȃ̂ŁAService�ɂ��̋@�\��u��)
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
		 * Timer���Z�b�g���Ȃ����B
		 */
		else if(ACTION_UPDATE_TIMER.equals(action)) {
			ActionProcessorUpdateTimer proc = 
					new ActionProcessorUpdateTimer(this, mRemindReserver, mUpdateReserver);
			proc.processTimerUpdate();
		}
		
		// �i���� : http://d.hatena.ne.jp/adsaria/20100914/1284435095)
		// START_NOT_STICKY �܂��� START_REDELIVER_INTENT �͑����Ă����R�}���h����������Ԃ������s����Service�B
		// �� �܂�A���N�G�X�g���ꂽ�������I�������A���̃T�[�r�X�͊��Ƒ����^�C�~���O�Ŏ��ʁB
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
