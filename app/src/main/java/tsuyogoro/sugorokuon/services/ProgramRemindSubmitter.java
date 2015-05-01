/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.AudioManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.activities.SugorokuonActivity;
import tsuyogoro.sugorokuon.models.apis.TimeTableApi;
import tsuyogoro.sugorokuon.models.entities.Program;
import tsuyogoro.sugorokuon.models.prefs.RemindBehaviorPreference;
import tsuyogoro.sugorokuon.utils.SugorokuonLog;
import tsuyogoro.sugorokuon.utils.SugorokuonUtils;

class ProgramRemindSubmitter {

    private static final float NOTIFICATION_LARGE_ICON_SIZE_DP = 64;

    /**
     * オススメ番組の中でそろそろonAirされる番組のreminder (Notification) を発行
     *
     * @param context
     */
    static public void notifyOnAirSoon(Context context) {

        TimeTableApi timeTableApi = new TimeTableApi(context);

        List<Program> recommends =
                timeTableApi.fetchRecommends(Calendar.getInstance(Locale.JAPAN));

        if (!recommends.isEmpty()) {
            Notification notification = reminderNotification(context, recommends);

            // onAirTimeから始める番組がある、ということをNotificationで通知。
            // (メモ：この第一引数の値が、notificationを消すのに必要）
            NotificationManager notificationMgr =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationMgr.notify(R.string.app_name, notification);

        } else {
            SugorokuonLog.w("Try to submit onAir reminder but no recommend was found");
        }
    }

    private static Notification reminderNotification(Context context, List<Program> recommends) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                context.getText(R.string.date_mmddeeehhmm).toString(), Locale.JAPANESE);

        // 「~からonAir」のstringを作る。
        String subTitle = String.format(
                context.getText(R.string.recommend_reminder_text).toString(),
                dateFormat.format(new Date(recommends.get(0).startTime.getTimeInMillis())));

        // もし2件以上同時にonAirの場合は「他～件」もつける。
        // (recommendsは放送開始順に並んでいるので、下記の方法で同じ時刻の番組がいくつかあるかを調べる)
        if (recommends.size() > 1) {
            int onAirSameTime = 0;
            for (int i = 1; i < recommends.size(); i++) {
                if (recommends.get(0).startTime.equals(recommends.get(i).startTime)) {
                    onAirSameTime++;
                } else {
                    break;
                }
            }

            if (0 < onAirSameTime) {
                subTitle += String.format(
                        context.getText(R.string.recommend_reminder_more).toString(),
                        onAirSameTime);
            }
        }

        // Intentからアプリを開いた時に、オススメラジオ局にフォーカスが当たるよう、Extraを設定
        Intent intent = new Intent(context, SugorokuonActivity.class);
        intent.setAction(SugorokuonActivity.ACTION_OPEN_TIMETABLE);
        intent.putExtra(SugorokuonActivity.EXTRA_STATION_ID, recommends.get(0).stationId);
        PendingIntent operation = PendingIntent.getActivity(context, 0, intent, 0);

        Bitmap largeIcon = adjustLargeIcon(recommends.get(0).getSymbolIcon(context), context);

        Notification notification = new Notification.Builder(context)
                .setContentTitle(recommends.get(0).title)
                .setContentText(subTitle)
                .setTicker(context.getText(R.string.recommend_reminder_ticker))
                .setLargeIcon(largeIcon)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(operation)
                .setAutoCancel(true)
                .setDefaults(RemindBehaviorPreference.wayToNotify(context))
                .getNotification();

        notification.audioStreamType = AudioManager.STREAM_NOTIFICATION;

        return notification;
    }

    private static Bitmap adjustLargeIcon(Bitmap icon, Context context) {

        float sourceWDp = SugorokuonUtils.calculateDpfromPx(context, (float) icon.getWidth());
        float sourceHDp = SugorokuonUtils.calculateDpfromPx(context, (float) icon.getHeight());

        float scale;
        if ((NOTIFICATION_LARGE_ICON_SIZE_DP / sourceHDp)
                < (NOTIFICATION_LARGE_ICON_SIZE_DP / sourceWDp)) {
            scale = NOTIFICATION_LARGE_ICON_SIZE_DP / sourceHDp;
        } else {
            scale = NOTIFICATION_LARGE_ICON_SIZE_DP / sourceWDp;
        }

        Matrix matrix = new Matrix();

        matrix.postScale(scale, scale);

        return Bitmap.createBitmap(icon, 0, 0, icon.getWidth(), icon.getHeight(), matrix, true);
    }
}
