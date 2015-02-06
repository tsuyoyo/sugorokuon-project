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
 * 番組情報のdownload進捗をNotification上に発行するクラス。
 * 開始、進捗（<取得済みのStation>/<全Station>）、エラー、完了を通知する。
 *
 * @author Tsuyoyo
 *
 */
class LoadingProgressSubmitter {

    private Calendar mPublishedTime;

    /**
     * コンストラクタ。
     * ProgressをNotification barへ通知する際、setWhenは常に固定にしないと
     * Jelly Beansでちらつきが発声してしまうので、最初のNotification発行時間を記憶する。
     *
     * @param publishedTime
     */
    public LoadingProgressSubmitter(Calendar publishedTime) {
        mPublishedTime = publishedTime;
    }

    /**
     * loadが始まったことを通知するNotificationを発行。
     * ここで発行したNotificationが次々に更新されていく。
     *
     * @param context
     */
    public void submitNotification(Context context) {
        doSubmitNotification(context, createNotification(context));
    }

    /**
     * loadの進捗を通知。
     *
     * @param context
     */
    public void updateNotification(Context context, int max, int progress) {
        doSubmitNotification(context, createProgressNotification(context, max, progress));
    }

    /**
     * load完了を通知。
     *
     * @param context
     */
    public void completeNotifiation(Context context) {
        doSubmitNotification(context, createCompleteNotification(context));
    }

    /**
     * 番組情報Donwloadがエラーで止まった事を伝えるnotificationを発行。
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
        // (番組表更新をcompleteまでちゃんとできた）
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
        // (番組表更新がcompleteまでちゃんとできなかった）
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