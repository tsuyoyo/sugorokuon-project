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
 * ����I�ɔԑg�����X�V���邽�߂�Timer�𔭍s����N���X�B
 *�@���̂Ƃ���͖��T���j�����iweekly�̔ԑg�\���X�V�����̂��A���j�[����ۂ��j�B
 * �i��̓I�Ȏ��Ԃ� {@link UpdatedDateManager} �N���X���Ǘ��j
 * 
 * @author Tsuyoyo 
 */
class ProgramUpdateReserver {
	
	/**
	 * ����X�V���s��Timer��ݒ肷��B
	 * 
	 * @param context
	 */
	public void setNextNotification(Context context) {
		// ���ɒʒm���鎞�Ԃ��擾�B
		long nextTime = getNextNotificationTime(context);
		
		// AlermManager�ɁA
		// nextTime��intent(Service��ACTION_UPDATE_DATABASE)���΂��悤�ɐݒ�B
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, nextTime, createNotifyOperationSender(context));
	}
	
	/**
	 * ���ݐݒ肳��Ă���Alarm��cancel����B
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
