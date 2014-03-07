/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.service;

import java.util.Calendar;
import java.util.List;

import tsuyogoro.sugorokuon.constant.NotifyTiming;
import tsuyogoro.sugorokuon.datatype.Program;
import tsuyogoro.sugorokuon.settings.preference.RemindTimePreference;
import tsuyogoro.sugorokuon.util.SugorokuonUtils;
import tsuyogoro.sugorokuon.viewflow.DataViewFlow;
import tsuyogoro.sugorokuon.viewflow.ProgramDataManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * オススメ番組放送開始前のリマインダのためのTimerをセットするクラス。
 * 
 * @author Tsuyoyo
 */
public class RecommendReminderReserver {
	
	// For debug
	static public final String KEY_NOTIFY_TIME = "key_notify_time";
	
	/**
	 * 次に放送される番組のAlarmを設定する。 
	 * lastNotifiedOnAirTimeがnullならリストの先頭、
	 * そうでなければlastNotifiedOnAirTimeの次に放送が開始する番組を探してTimer設定。
	 * 次が無ければ何もしない。
	 * 
	 * @param context
	 * @param lastNotifiedOnAirTime 最後に通知された番組のonAir時刻。Serverから取れる形式。
	 */
	public void setNextNotification(Context context, String lastNotifiedOnAirTime) {	
		ProgramDataManager progMgr = DataViewFlow.getInstance().getProgramDataMgr();
		List<Program> recommends = progMgr.getRecommendProgramsBaforeOnAir(context);
		
		if(0 < recommends.size()) {
			// 次に通知する時間を取得（次の番組のonAirTimeから、設定の「～時間前」を引いた値が来る）。
			String onAirTime = null;
			if(null == lastNotifiedOnAirTime) {
				onAirTime = recommends.get(0).start;
			} else {
				Calendar lastNotifiedOnAirCal = 
						SugorokuonUtils.changeOnAirTimeToCalendar(lastNotifiedOnAirTime);
				for(Program p : recommends) {
					Calendar startCal = SugorokuonUtils.changeOnAirTimeToCalendar(p.start);
					// startがlastNotifiedOnAirより後になるまえでforを回す。
					if(1 == startCal.compareTo(lastNotifiedOnAirCal)) {
						onAirTime = p.start;
						break;
					}
				}
			}
			// onAirTimeがnullということは、lastNotifyTimeが最後だったということ。
			if(null == onAirTime) {
				return;
			}
							
			// AlermManagerに、nextNotifyTimeにintentを飛ばすように設定。
			Calendar nextNotifyTimeCal = getNextNotificationTime(context, onAirTime);
			if(null != nextNotifyTimeCal) {
				long nextNotifyTime = nextNotifyTimeCal.getTimeInMillis();			
				PendingIntent sender = createNotifyOperationSender(context, onAirTime);
				AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
				am.set(AlarmManager.RTC_WAKEUP, nextNotifyTime, sender);
				
				// ------- just for test ---------- //
//				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
//				String log = pref.getString(KEY_NOTIFY_TIME, "");
//				Calendar nextTime = getNextNotificationTime(context, onAirTime);
//				log += "," + nextTime.get(Calendar.MONTH) +
//						"/" + nextTime.get(Calendar.DATE) +
//						" " + nextTime.get(Calendar.HOUR_OF_DAY) +
//						":" + nextTime.get(Calendar.MINUTE);
//				Log.d("SugorokuonTest", log);
//				Editor editor = pref.edit();
//				editor.putString(KEY_NOTIFY_TIME, log);
//				editor.commit();
				// ------- just for test ---------- //			
			}
						
		}
		
	}
	
	/**
	 * 現在設定されているAlarmをcancelする。
	 * 
	 * @param context
	 */
	public void cancelNextNotification(Context context) {
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.cancel(createNotifyOperationSender(context, null));
	}
	
	private Calendar getNextNotificationTime(Context context, String onAirTimeStr) {
		// 設定から、次の通知タイミング設定を取得し、時刻を計算させる。
		Calendar onAirTime = SugorokuonUtils.changeOnAirTimeToCalendar(onAirTimeStr);
		NotifyTiming notifyTiming = RemindTimePreference.getNotifyTime(context);
		return notifyTiming.calculateNotifyTime(onAirTime);		
	}	
			
	/*
	 * ServiceへACTION_NOTIFY_ONAIR_SOONを投げるpendingIntentを生成。
	 */
	private PendingIntent createNotifyOperationSender(Context context, String onAirTime) {
		Intent intent = new Intent(SugorokuonService.ACTION_NOTIFY_ONAIR_SOON);
		if(null != onAirTime) {
			intent.putExtra(SugorokuonService.EXTRA_ON_AIR_TIME, onAirTime);
		}
		
		return PendingIntent.getService(context, -1, intent, 
				PendingIntent.FLAG_CANCEL_CURRENT); // 第2引数は使われてないらしい。
	}
		
}
