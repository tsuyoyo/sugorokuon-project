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
 * �I�X�X���ԑg�����J�n�O�̃��}�C���_�̂��߂�Timer���Z�b�g����N���X�B
 * 
 * @author Tsuyoyo
 */
public class RecommendReminderReserver {
	
	// For debug
	static public final String KEY_NOTIFY_TIME = "key_notify_time";
	
	/**
	 * ���ɕ��������ԑg��Alarm��ݒ肷��B 
	 * lastNotifiedOnAirTime��null�Ȃ烊�X�g�̐擪�A
	 * �����łȂ����lastNotifiedOnAirTime�̎��ɕ������J�n����ԑg��T����Timer�ݒ�B
	 * ����������Ή������Ȃ��B
	 * 
	 * @param context
	 * @param lastNotifiedOnAirTime �Ō�ɒʒm���ꂽ�ԑg��onAir�����BServer�������`���B
	 */
	public void setNextNotification(Context context, String lastNotifiedOnAirTime) {	
		ProgramDataManager progMgr = DataViewFlow.getInstance().getProgramDataMgr();
		List<Program> recommends = progMgr.getRecommendProgramsBaforeOnAir(context);
		
		if(0 < recommends.size()) {
			// ���ɒʒm���鎞�Ԃ��擾�i���̔ԑg��onAirTime����A�ݒ�́u�`���ԑO�v���������l������j�B
			String onAirTime = null;
			if(null == lastNotifiedOnAirTime) {
				onAirTime = recommends.get(0).start;
			} else {
				Calendar lastNotifiedOnAirCal = 
						SugorokuonUtils.changeOnAirTimeToCalendar(lastNotifiedOnAirTime);
				for(Program p : recommends) {
					Calendar startCal = SugorokuonUtils.changeOnAirTimeToCalendar(p.start);
					// start��lastNotifiedOnAir����ɂȂ�܂���for���񂷁B
					if(1 == startCal.compareTo(lastNotifiedOnAirCal)) {
						onAirTime = p.start;
						break;
					}
				}
			}
			// onAirTime��null�Ƃ������Ƃ́AlastNotifyTime���Ōゾ�����Ƃ������ƁB
			if(null == onAirTime) {
				return;
			}
							
			// AlermManager�ɁAnextNotifyTime��intent���΂��悤�ɐݒ�B
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
	 * ���ݐݒ肳��Ă���Alarm��cancel����B
	 * 
	 * @param context
	 */
	public void cancelNextNotification(Context context) {
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.cancel(createNotifyOperationSender(context, null));
	}
	
	private Calendar getNextNotificationTime(Context context, String onAirTimeStr) {
		// �ݒ肩��A���̒ʒm�^�C�~���O�ݒ���擾���A�������v�Z������B
		Calendar onAirTime = SugorokuonUtils.changeOnAirTimeToCalendar(onAirTimeStr);
		NotifyTiming notifyTiming = RemindTimePreference.getNotifyTime(context);
		return notifyTiming.calculateNotifyTime(onAirTime);		
	}	
			
	/*
	 * Service��ACTION_NOTIFY_ONAIR_SOON�𓊂���pendingIntent�𐶐��B
	 */
	private PendingIntent createNotifyOperationSender(Context context, String onAirTime) {
		Intent intent = new Intent(SugorokuonService.ACTION_NOTIFY_ONAIR_SOON);
		if(null != onAirTime) {
			intent.putExtra(SugorokuonService.EXTRA_ON_AIR_TIME, onAirTime);
		}
		
		return PendingIntent.getService(context, -1, intent, 
				PendingIntent.FLAG_CANCEL_CURRENT); // ��2�����͎g���ĂȂ��炵���B
	}
		
}
