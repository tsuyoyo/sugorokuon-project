/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.service;

import java.util.Calendar;
import java.util.Locale;

import tsuyogoro.sugorokuon.settings.UpdatedDateManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * 定期的に番組情報を更新するためのTimerを発行するクラス。
 *　今のところは毎週月曜早朝（weeklyの番組表が更新されるのが、月曜深夜っぽい）。
 * （具体的な時間は {@link UpdatedDateManager} クラスが管理）
 * 
 * @author Tsuyoyo 
 */
class ProgramUpdateReserver {
	
	/**
	 * 次回更新を行うTimerを設定する。
	 * 
	 * @param context
	 */
	public void setNextNotification(Context context) {
		// 次に通知する時間を取得。
		long nextTime = getNextNotificationTime(context);
		
		// AlermManagerに、
		// nextTimeにintent(ServiceへACTION_UPDATE_DATABASE)を飛ばすように設定。
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, nextTime, createNotifyOperationSender(context));
	}
	
	/**
	 * 現在設定されているAlarmをcancelする。
	 * 
	 * @param context
	 */
	public void cancelNextNotification(Context context) {
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.cancel(createNotifyOperationSender(context));
	}
	
	private PendingIntent createNotifyOperationSender(Context context) {
		Intent intent = new Intent(SugorokuonService.ACTION_LOAD_PROGRAM_DATA);
		PendingIntent sender = PendingIntent.getService(
				context, -1, intent, PendingIntent.FLAG_CANCEL_CURRENT);		
		return sender;
	}
	
	private long getNextNotificationTime(Context context) {
		return UpdatedDateManager.getInstance(context)
			.calculateNextUpdateTime(Calendar.getInstance(Locale.JAPAN));
	}
	
}
