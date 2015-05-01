package tsuyogoro.sugorokuon.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

class SugorokuonServiceUtil {

    /**
     * Actionのprefixをつける
     *
     * @param a
     * @return
     */
    static String action(String a) {
        return "tsuyogoro.sugorokuon.service." + a;
    }

    /**
     * TimerにセットするPendingIntentを作成するutilityメソッド
     *
     * @param action
     * @param context
     * @return
     */
    static PendingIntent pendingIntentForTimer(String action, Context context) {
        Intent intent = new Intent(action);
        intent.setPackage(context.getPackageName());
        intent.putExtra(TimeTableService.EXTRA_NOTIFY_PROGRESS, true);
        return PendingIntent.getService(context, -1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    /**
     * AlarmManagerにTimerを仕掛けるutilityメソッド
     *
     * @param intent
     * @param nextTimeInMillis
     * @param context
     */
    static void setNextTimer(PendingIntent intent, long nextTimeInMillis, Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, nextTimeInMillis, intent);
    }

    /**
     * AlarmManagerからTimerをcancelするutilityメソッド
     *
     * @param intent
     * @param context
     */
    static void cancelTimer(PendingIntent intent, Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(intent);
    }
}
