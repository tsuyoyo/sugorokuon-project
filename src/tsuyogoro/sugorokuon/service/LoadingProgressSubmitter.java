/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.service;

import java.util.Calendar;
import java.util.Locale;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.presenter.SugorokuonActivity;
import tsuyogoro.sugorokuon.util.GATrackingUtil;
import tsuyogoro.sugorokuon.util.SugorokuonUtils;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.google.analytics.tracking.android.EasyTracker;

/**
 * �ԑg����download�i����Notification��ɔ��s����N���X�B
 * �J�n�A�i���i<�擾�ς݂�Station>/<�SStation>�j�A�G���[�A������ʒm����B
 * 
 * @author Tsuyoyo
 *
 */
class LoadingProgressSubmitter {
	
	private Calendar mPublishedTime;
	
	/**
	 * �R���X�g���N�^�B
	 * Progress��Notification bar�֒ʒm����ہAsetWhen�͏�ɌŒ�ɂ��Ȃ���
	 * Jelly Beans�ł�������������Ă��܂��̂ŁA�ŏ���Notification���s���Ԃ��L������B
	 * 
	 * @param publishedTime
	 */
	public LoadingProgressSubmitter(Calendar publishedTime) {
		mPublishedTime = publishedTime;
	}	
	
	/**
	 * load���n�܂������Ƃ�ʒm����Notification�𔭍s�B
	 * �����Ŕ��s����Notification�����X�ɍX�V����Ă����B
	 * 
	 * @param context
	 */
	public void submitNotification(Context context) {
		doSubmitNotification(context, createNotification(context));
	}
	
	/**
	 * load�̐i����ʒm�B
	 * 
	 * @param context
	 */
	public void updateNotification(Context context, int max, int progress) {
		doSubmitNotification(context, createProgressNotification(context, max, progress));
	}
	
	/**
	 * load������ʒm�B
	 * 
	 * @param context
	 */
	public void completeNotifiation(Context context) {
		doSubmitNotification(context, createCompleteNotification(context));
	}
	
	/**
	 * �ԑg���Donwload���G���[�Ŏ~�܂�������`����notification�𔭍s�B
	 * 
	 * @param context
	 * @param messageId
	 */
	public void errorNotification(Context context, int messageId) {
		doSubmitNotification(context, createFailedNotification(context));
	}

	private void doSubmitNotification(Context context, Notification notification) {
		NotificationManager notificationMgr = 
			(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationMgr.notify(SugorokuonService.PROGRESS_NOTIFICATION_ID, notification);
	}
	
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private Notification createNotification(Context context) {
		Notification notification;
		if(SugorokuonUtils.isHigherThanGingerBread()) {
			notification = new Notification.Builder(context)
			.setContentTitle(context.getString(R.string.progress_notification_title))
         	.setContentText(context.getString(R.string.progress_notification_text))
         	.setTicker(context.getString(R.string.progress_notification_ticker))
         	.setSmallIcon(R.drawable.ic_launcher)
         	.setDefaults(Notification.DEFAULT_LIGHTS)
          	.setWhen(mPublishedTime.getTimeInMillis())
         	.getNotification();
		} else {
			notification = new Notification(R.drawable.ic_launcher, 
					context.getString(R.string.progress_notification_ticker), 
					mPublishedTime.getTimeInMillis());
			
			notification.defaults = Notification.DEFAULT_LIGHTS;
			notification.setLatestEventInfo(context, 
					context.getString(R.string.progress_notification_title), 
					context.getString(R.string.progress_notification_text),
					null);
			
			notification.contentIntent = createOperation(context);
		}
		
		return notification;
	}
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@SuppressWarnings("deprecation")
	private Notification createProgressNotification(Context context, int max, int progress) {
		String progStr = context.getString(R.string.progress_notification_text_progress);
		Notification notification;		
		if(SugorokuonUtils.isHigherThanHoneyComb()) {
			notification = new Notification.Builder(context)
         	.setContentTitle(context.getString(R.string.progress_notification_title))
         	.setContentText(String.format(progStr, max, progress))
         	.setProgress(max, progress, false)
         	.setSmallIcon(R.drawable.ic_launcher)
         	.setAutoCancel(false)
          	.setWhen(mPublishedTime.getTimeInMillis())
          	.setContentIntent(createOperation(context))
         	.getNotification();
		} else {
			notification = new Notification();
			notification.icon = R.drawable.ic_launcher;
			notification.setLatestEventInfo(context, 
					context.getString(R.string.progress_notification_title), 
					String.format(progStr, max, progress), createOperation(context));
		}
		return notification;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressWarnings("deprecation")
	private Notification createCompleteNotification(Context context) {
		
		// For mobile google analytics tracking.
		// (�ԑg�\�X�V��complete�܂ł����Ƃł����j 
		EasyTracker.getInstance().setContext(context);
		EasyTracker.getTracker().trackEvent(
				context.getText(R.string.ga_event_category_program_update).toString(),
				context.getText(R.string.ga_event_action_program_update_completed).toString(),
				GATrackingUtil.getCurrentTime(context), null);		
		
		Notification notification;
		if(SugorokuonUtils.isHigherThanGingerBread()) {
			notification = new Notification.Builder(context)
			.setContentTitle(context.getString(R.string.progress_notification_title))
			.setContentText(context.getString(R.string.progress_notification_text_complete))
         	.setTicker(context.getString(R.string.progress_notification_ticker_complete))
         	.setSmallIcon(R.drawable.ic_launcher)
         	.setDefaults(Notification.DEFAULT_LIGHTS)
         	.setAutoCancel(true)
         	.setWhen(Calendar.getInstance(Locale.JAPAN).getTimeInMillis())
         	.setContentIntent(createOperation(context))         	
         	.getNotification();
		} else {
			notification = new Notification();
			notification.icon = R.drawable.ic_launcher;
			notification.defaults = Notification.DEFAULT_LIGHTS;
			notification.setLatestEventInfo(context, 
					context.getString(R.string.progress_notification_title), 
					context.getString(R.string.progress_notification_text_complete), 
					createOperation(context));
		}
		return notification;
	}	
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressWarnings("deprecation")
	private Notification createFailedNotification(Context context) {
		// For mobile google analytics tracking.
		// (�ԑg�\�X�V��complete�܂ł����Ƃł��Ȃ������j
		EasyTracker.getInstance().setContext(context);
		EasyTracker.getTracker().trackEvent(
				context.getText(R.string.ga_event_category_program_update).toString(),
				context.getText(R.string.ga_event_action_program_update_Failed).toString(),
				GATrackingUtil.getCurrentTime(context), null);				
		
		Notification notification;
		if(SugorokuonUtils.isHigherThanGingerBread()) {
			notification = new Notification.Builder(context)
	     	.setContentTitle(context.getString(R.string.progress_notification_title))
	     	.setContentText(context.getString(R.string.progress_notification_text_failed))
	     	.setTicker(context.getString(R.string.progress_notification_ticker_failed))
	     	.setSmallIcon(R.drawable.ic_launcher)
	     	.setDefaults(Notification.DEFAULT_LIGHTS)
	     	.setAutoCancel(true)
	     	.setWhen(Calendar.getInstance(Locale.JAPAN).getTimeInMillis())
	      	.setContentIntent(createOperation(context))
	     	.getNotification();
		} else {
			notification = new Notification(R.drawable.ic_launcher, 
					context.getString(R.string.progress_notification_ticker_failed), 
					Calendar.getInstance(Locale.JAPAN).getTimeInMillis());
			notification.setLatestEventInfo(context, 
					context.getString(R.string.progress_notification_title), 
					context.getString(R.string.progress_notification_text_failed),
					createOperation(context));
		}
		return notification;
	}
	
	private PendingIntent createOperation(Context context) {
		Intent intent = new Intent(context, SugorokuonActivity.class);
		return PendingIntent.getActivity(context, 0, intent, 0);
	}
	
}
