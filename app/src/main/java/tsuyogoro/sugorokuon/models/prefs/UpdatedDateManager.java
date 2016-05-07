/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.models.prefs;

import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import tsuyogoro.sugorokuon.constants.Area;
import tsuyogoro.sugorokuon.constants.Region;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * 前回Updateした日時を管理し、次にいつupdateすべきか？を教えてくれるクラス。
 *
 * @author Tsuyoyo
 */
public class UpdatedDateManager {

    private static UpdatedDateManager sInstance;

    private final String PREF_KEY_WEEKLY_UPDATE = "pref_key_last_update_weekly";

    private final String PREF_KEY_DAILY_UPDATE = "pref_key_last_update_daily";

    private static final String PREF_KEY_LAST_UPDATED = "pref_key_last_updated";

    private Context mContext;

    private UpdatedDateManager(Context context) {
        mContext = context;
    }

    public static boolean shouldUpdate(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        long lastUpdated = pref.getLong(PREF_KEY_LAST_UPDATED, -1);
        long now = Calendar.getInstance(Locale.JAPAN).getTimeInMillis();

        long oneDay = 24 * 60 * 60 * 1000;
        if ((now - lastUpdated) > oneDay) {
            return true;
        } else {
            return false;
        }
    }

    public static long getLastUpdateTime(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getLong(PREF_KEY_LAST_UPDATED, -1);
    }

    /**
     * メモ :
     * 2.0では、updateの種類 (weekly, daily) 問わず、updateを行ったらlastUpdateを更新。
     * 現在時刻と比較して、１日以上間が開いたらupdateしてもOKと判断。
     * 手動アップデートでこの判断を使う。
     *
     * @param context
     */
    public static void updateLastUpdateTime(Context context) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putLong(PREF_KEY_LAST_UPDATED, Calendar.getInstance(Locale.JAPAN).getTimeInMillis());
        editor.apply();
    }

    /**
     * UpdatedDataManagerインスタンスを取得。
     * インスタンス生成の際、Preferenceの変更を受け取るためのlistener登録が走る。
     *
     * @param context
     * @return
     */
    public static UpdatedDateManager getInstance(Context context) {
        if(null == sInstance) {
            sInstance = new UpdatedDateManager(context.getApplicationContext());
        }
        return sInstance;
    }

    private void clearPreferences() {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
        editor.putLong(PREF_KEY_WEEKLY_UPDATE, -1);
        editor.putLong(PREF_KEY_DAILY_UPDATE, -1);
        editor.commit();
    }

    /**
     * 現在時刻を基準に、次に番組表を更新すべき時間を計算する。
     *
     * @return
     */
    public long nextWeeklyUpdateTime() {
        return nextWeeklyUpdateTime(Calendar.getInstance(Locale.JAPAN));
    }

    /**
     * baseTimeを基準に、次に番組表を更新すべき時間を計算する。
     *
     * @return
     */
    public long nextWeeklyUpdateTime(Calendar baseTime) {

        Calendar c = (Calendar) baseTime.clone();

        Random random = new Random();

        // 今が月曜日で6時より前ならば、その直後の5時10分〜50分のどこかが更新タイム。
        // 今が月曜じゃない、もしくは、月曜6時以降であれば、翌週の月曜早朝が更新タイム。
        if((Calendar.MONDAY != c.get(Calendar.DAY_OF_WEEK)) || (6 < c.get(Calendar.HOUR_OF_DAY))) {
            do {
                c.add(Calendar.DATE, 1);
            } while(Calendar.MONDAY != c.get(Calendar.DAY_OF_WEEK));
        }

        // サーバへのアクセスを散らすために、5時10分0秒〜5時59分59秒の間にランダムでセット
        c.set(Calendar.HOUR_OF_DAY, 5);
        c.set(Calendar.MINUTE, 10 + random.nextInt(50));
        c.set(Calendar.SECOND, random.nextInt(60));

        return c.getTimeInMillis();
    }

    /**
     * baseTimeを基準に、次に番組表を更新すべき時間を計算する。
     *
     * @return 次の "一日分の番組表更新" の時刻
     */
    public long nextDailyUpdateTime(long baseTimeInMillis) {
        Calendar c = Calendar.getInstance(Locale.JAPAN);
        c.setTimeInMillis(baseTimeInMillis);
        return nextDailyUpdateTime(c);
    }

    /**
     * 現在時刻を基準に、次に番組表を更新すべき時間を計算する。
     *
     * @return 次の "一日分の番組表更新" の時刻
     */
    public long nextDailyUpdateTime() {
        return nextDailyUpdateTime(Calendar.getInstance(Locale.JAPAN));
    }

    /**
     * baseTimeを基準に、次に番組表を更新すべき時間を計算する。
     *
     * @return 次の "一日分の番組表更新" の時刻
     */
    public long nextDailyUpdateTime(Calendar baseTime) {

        Calendar c = (Calendar) baseTime.clone();

        // 6時以降であれば、次の日の朝6時
        // (当日6時より前だったら、その日の朝6時)
        if (c.get(Calendar.HOUR_OF_DAY) >= 6) {
            c.add(Calendar.DATE, 1);
        }

        c.set(Calendar.HOUR_OF_DAY, 6);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTimeInMillis();
    }

}