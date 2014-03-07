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
 * オススメ番組のリマインダNotificationを発行するクラス。
 * 
 * @author Tsuyoyo
 */
class RecommendReminderSubmitter {
	
	/**
	 * オススメ番組のリマインダNotificationを発行する。
	 * 
	 * @param context
	 * @param onAirTime その番組の開始時刻。サーバから取れる形式（yyyyMMddhhmmss).
	 */
	public void submitNotification(Context context, String onAirTimeStr) {
		
		// onAirTimeStrがnullだった場合は現在時刻をonAirTimeとする。
		Calendar onAirTime = (null == onAirTimeStr) ? Calendar.getInstance(Locale.JAPAN) 
						: SugorokuonUtils.changeOnAirTimeToCalendar(onAirTimeStr);
		
		// onAirTimeから始まる番組のlistを取得。
		ProgramDataManager progDataMgr = DataViewFlow.getInstance().getProgramDataMgr();
		List<Program> progs = progDataMgr.getRecommendPrograms(context, onAirTime);
		
		// onAirTimeから始める番組がある、ということをNotificationで通知。
		// (メモ：この第一引数の値が、notificationを消すのに必要）
		if(0 < progs.size()) { // 一応failセーフを。
			NotificationManager notificationMgr = 
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			
			Notification notification = createNotification(context, progs);
			notificationMgr.notify(R.string.app_name, notification);
			
			// For mobile google analytics tracking.
			// (番組リマインダ通知をしたらtrack。番組名も集計をとってみる）
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
	
		// 通知音を鳴らすための設定。
		notification.audioStreamType = AudioManager.STREAM_NOTIFICATION;
				
		return notification;
	}
	
	private PendingIntent createOperation(Context context) {
		Intent intent = new Intent(context, SugorokuonActivity.class);
		return PendingIntent.getActivity(context, 0, intent, 0);
	}
	
	private String createContentSubTitle(Context context, List<Program> progs) {
		// 放送開始時間の文字列を作成。
		Calendar onAir = 
			SugorokuonUtils.changeOnAirTimeToCalendar(progs.get(0).start);
		String date = context.getText(R.string.date_mmddeeehhmm).toString();
		SimpleDateFormat sdfTo = new SimpleDateFormat(date, Locale.JAPANESE);
		
		// 「~からonAir」のstringを作る。
		String subTitle = String.format(
				context.getText(R.string.recommend_reminder_text).toString(), 
				sdfTo.format(new Date(onAir.getTimeInMillis())));
		
		// もし2件以上同時にonAirの場合は「他～件」もつける。
		if(1 < progs.size()) {
			subTitle += String.format(
					context.getText(R.string.recommend_reminder_more).toString(),
					progs.size() - 1);
		}
		
		return subTitle;		
	}
	
}
