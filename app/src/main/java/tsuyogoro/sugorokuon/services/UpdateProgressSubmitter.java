/**
 * Copyright (c) 
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.activities.SugorokuonActivity;

/**
 * 番組情報のdownload進捗をNotification上に発行するクラス。
 * 開始、進捗（<取得済みのStation>/<全Station>）、エラー、完了を通知する。
 *
 * @author Tsuyoyo
 */
class UpdateProgressSubmitter {

    private Calendar mPublishedTime;

    /**
     * コンストラクタ。
     * ProgressをNotification barへ通知する際、setWhenは常に固定にしないと
     * Jelly Beansでちらつきが発生してしまうので、最初のNotification発行時間を記憶する。
     *
     * @param publishedTime
     */
    public UpdateProgressSubmitter(Calendar publishedTime) {
        mPublishedTime = publishedTime;
    }

    /**
     * updateが始まったことを通知するNotificationを発行。
     *
     * @param context
     */
    public void notifyStart(Context context) {
        Notification notification = new Notification.Builder(context)
                .setContentTitle(context.getString(R.string.progress_notification_title))
                .setContentText(context.getString(R.string.progress_notification_text))
                .setTicker(context.getString(R.string.progress_notification_ticker))
                .setSmallIcon(R.mipmap.ic_statusbar)
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setWhen(mPublishedTime.getTimeInMillis())
                .setContentIntent(operationIntent(context))
                .getNotification();

        submit(context, notification);
    }

    /**
     * updateの進捗を通知。
     *
     * @param context
     */
    public void notifyProgress(Context context, int max, int progress) {
        String progStr = context.getString(R.string.progress_notification_text_progress);

        Notification notification = new Notification.Builder(context)
                .setContentTitle(context.getString(R.string.progress_notification_title))
                .setContentText(String.format(progStr, max, progress))
                .setProgress(max, progress, false)
                .setSmallIcon(R.mipmap.ic_statusbar)
                .setAutoCancel(false)
                .setWhen(mPublishedTime.getTimeInMillis())
                .setContentIntent(operationIntent(context))
                .getNotification();

        submit(context, notification);
    }

    /**
     * update完了を通知。
     *
     * @param context
     */
    public void notifyComplete(Context context) {

//        // For mobile google analytics tracking.
//        // (番組表更新をcompleteまでちゃんとできた）
//        EasyTracker.getInstance().setContext(context);
//        EasyTracker.getTracker().trackEvent(
//                context.getText(R.string.ga_event_category_program_update).toString(),
//                context.getText(R.string.ga_event_action_program_update_completed).toString(),
//                GATrackingUtil.getCurrentTime(context), null);

        Notification notification = new Notification.Builder(context)
                .setContentTitle(context.getString(R.string.progress_notification_title))
                .setContentText(context.getString(R.string.progress_notification_text_complete))
                .setTicker(context.getString(R.string.progress_notification_ticker_complete))
                .setSmallIcon(R.mipmap.ic_statusbar)
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setAutoCancel(true)
                .setWhen(Calendar.getInstance(Locale.JAPAN).getTimeInMillis())
                .setContentIntent(operationIntent(context))
                .getNotification();

        submit(context, notification);
    }

    /**
     * 番組情報Donwloadがエラーで止まった事を伝えるnotificationを発行。
     *
     * @param context
     * @param messageId
     */
    public void notifyError(Context context, int messageId) {

//        // For mobile google analytics tracking.
//        // (番組表更新がcompleteまでちゃんとできなかった）
//        EasyTracker.getInstance().setContext(context);
//        EasyTracker.getTracker().trackEvent(
//                context.getText(R.string.ga_event_category_program_update).toString(),
//                context.getText(R.string.ga_event_action_program_update_Failed).toString(),
//                GATrackingUtil.getCurrentTime(context), null);

        Notification notification = new Notification.Builder(context)
                .setContentTitle(context.getString(R.string.progress_notification_title))
                .setContentText(context.getString(R.string.progress_notification_text_failed))
                .setTicker(context.getString(R.string.progress_notification_ticker_failed))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setAutoCancel(true)
                .setWhen(Calendar.getInstance(Locale.JAPAN).getTimeInMillis())
                .setContentIntent(operationIntent(context))
                .getNotification();

        submit(context, notification);
    }

    private void submit(Context context, Notification notification) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfoList = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : processInfoList) {
            if (!info.processName.equals(context.getPackageName()) ||
                    info.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                NotificationManager notificationMgr =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationMgr.notify(TimeTableService.NOTIFICATION_ID_FOR_UPDATE_PROGRESS, notification);

            }
        }
    }

    private PendingIntent operationIntent(Context context) {
        Intent intent = new Intent(context, SugorokuonActivity.class);
        return PendingIntent.getActivity(context, 0, intent, 0);
    }

}