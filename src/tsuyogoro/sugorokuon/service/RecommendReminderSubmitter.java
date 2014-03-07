/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.datatype.Program;
import tsuyogoro.sugorokuon.presenter.SugorokuonActivity;
import tsuyogoro.sugorokuon.settings.preference.RemindBehaviorPreference;
import tsuyogoro.sugorokuon.util.SugorokuonUtils;
import tsuyogoro.sugorokuon.viewflow.DataViewFlow;
import tsuyogoro.sugorokuon.viewflow.ProgramDataManager;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import com.google.analytics.tracking.android.EasyTracker;

/**
 * �I�X�X���ԑg�̃��}�C���_Notification�𔭍s����N���X�B
 * 
 * @author Tsuyoyo
 */
class RecommendReminderSubmitter {
	
	/**
	 * �I�X�X���ԑg�̃��}�C���_Notification�𔭍s����B
	 * 
	 * @param context
	 * @param onAirTime ���̔ԑg�̊J�n�����B�T�[�o�������`���iyyyyMMddhhmmss).
	 */
	public void submitNotification(Context context, String onAirTimeStr) {
		
		// onAirTimeStr��null�������ꍇ�͌��ݎ�����onAirTime�Ƃ���B
		Calendar onAirTime = (null == onAirTimeStr) ? Calendar.getInstance(Locale.JAPAN) 
						: SugorokuonUtils.changeOnAirTimeToCalendar(onAirTimeStr);
		
		// onAirTime����n�܂�ԑg��list���擾�B
		ProgramDataManager progDataMgr = DataViewFlow.getInstance().getProgramDataMgr();
		List<Program> progs = progDataMgr.getRecommendPrograms(context, onAirTime);
		
		// onAirTime����n�߂�ԑg������A�Ƃ������Ƃ�Notification�Œʒm�B
		// (�����F���̑������̒l���Anotification�������̂ɕK�v�j
		if(0 < progs.size()) { // �ꉞfail�Z�[�t���B
			NotificationManager notificationMgr = 
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			
			Notification notification = createNotification(context, progs);
			notificationMgr.notify(R.string.app_name, notification);
			
			// For mobile google analytics tracking.
			// (�ԑg���}�C���_�ʒm��������track�B�ԑg�����W�v���Ƃ��Ă݂�j
			EasyTracker.getInstance().setContext(context);
			EasyTracker.getTracker().trackEvent(
					context.getText(R.string.ga_event_category_program_reminder).toString(),
					context.getText(R.string.ga_event_action_submitted_reminder).toString(),
					progs.get(0).title, null);
		}
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private Notification createNotification(Context context, List<Program> progs) {

		Notification notification;
		if(SugorokuonUtils.isHigherThanGingerBread()) {
			notification = new Notification.Builder(context)
        	.setContentTitle(progs.get(0).title)
        	.setContentText(createContentSubTitle(context, progs))
        	.setTicker(context.getText(R.string.recommend_reminder_ticker))
        	.setSmallIcon(R.drawable.ic_launcher)
        	.setContentIntent(createOperation(context))
        	.setAutoCancel(true)
        	.setDefaults(RemindBehaviorPreference.wayToNotify(context))
        	.getNotification();			
		} else {
			notification = new Notification(R.drawable.ic_launcher, 
					context.getText(R.string.recommend_reminder_ticker), 
					Calendar.getInstance(Locale.JAPAN).getTimeInMillis());
			notification.defaults = RemindBehaviorPreference.wayToNotify(context);
			notification.setLatestEventInfo(context, progs.get(0).title, 
					createContentSubTitle(context, progs), createOperation(context));
		}
	
		// �ʒm����炷���߂̐ݒ�B
		notification.audioStreamType = AudioManager.STREAM_NOTIFICATION;
				
		return notification;
	}
	
	private PendingIntent createOperation(Context context) {
		Intent intent = new Intent(context, SugorokuonActivity.class);
		return PendingIntent.getActivity(context, 0, intent, 0);
	}
	
	private String createContentSubTitle(Context context, List<Program> progs) {
		// �����J�n���Ԃ̕�������쐬�B
		Calendar onAir = 
			SugorokuonUtils.changeOnAirTimeToCalendar(progs.get(0).start);
		String date = context.getText(R.string.date_mmddeeehhmm).toString();
		SimpleDateFormat sdfTo = new SimpleDateFormat(date, Locale.JAPANESE);
		
		// �u~����onAir�v��string�����B
		String subTitle = String.format(
				context.getText(R.string.recommend_reminder_text).toString(), 
				sdfTo.format(new Date(onAir.getTimeInMillis())));
		
		// ����2���ȏ㓯����onAir�̏ꍇ�́u���`���v������B
		if(1 < progs.size()) {
			subTitle += String.format(
					context.getText(R.string.recommend_reminder_more).toString(),
					progs.size() - 1);
		}
		
		return subTitle;		
	}
	
}
