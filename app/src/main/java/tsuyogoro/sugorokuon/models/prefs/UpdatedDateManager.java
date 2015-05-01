/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.models.prefs;

import java.util.Calendar;
import java.util.Locale;

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
//        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static UpdatedDateManager sInstance;

    private final String PREF_KEY_WEEKLY_UPDATE = "pref_key_last_update_weekly";

    private final String PREF_KEY_DAILY_UPDATE = "pref_key_last_update_daily";

    private static final String PREF_KEY_LAST_UPDATED = "pref_key_last_updated";

    private Context mContext;

    private UpdatedDateManager(Context context) {
        mContext = context;

//        // 設定値変更の通知を受け取るためにregister
//        PreferenceManager.getDefaultSharedPreferences(mContext)
//                .registerOnSharedPreferenceChangeListener(this);
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

//    @Override
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
//                                          String key) {
//        // TODO : Area設定＞Areaを空にする＞settings＞Areaを入れる＞updateがかからない
//        // というバグがあったので要注意。再現するなら直す。
//
//        // Area設定の変更だったら最終更新日時をクリア。次にMainに戻ったときにupdateを促す。
//        for(Area a : Area.values()) {
//            if(key.equals(AreaSettingPreference.getAreaPreferenceKey(a))) {
//                clearPreferences();
//            }
//        }
//        for(Region r : Region.values()) {
//            if(key.equals(AreaSettingPreference.getRegionPreferenceKey(r))) {
//                clearPreferences();
//            }
//        }
//    }

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

//    public void updateLastUpdatedTimeWeekly() {
//        Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
//        editor.putLong(PREF_KEY_WEEKLY_UPDATE, Calendar.getInstance(Locale.JAPAN).getTimeInMillis());
//        editor.commit();
//    }
//
//    public void updateLastUpdatedTimeDaily() {
//        Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
//        editor.putLong(PREF_KEY_DAILY_UPDATE, Calendar.getInstance(Locale.JAPAN).getTimeInMillis());
//        editor.commit();
//    }

    private void clearPreferences() {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
        editor.putLong(PREF_KEY_WEEKLY_UPDATE, -1);
        editor.putLong(PREF_KEY_DAILY_UPDATE, -1);
        editor.commit();
    }

    /**
     * 今update（webからデータをdownload）すべきかどうかを調べる。
     * 前回updateした時刻がpreferenceに格納されているので、現在時刻と比較して判定。
     *
     * @return
     */
//    public boolean shouldUpdateWeeklyTable() {
//
//        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
//        long lastUpdated = pref.getLong(PREF_KEY_WEEKLY_UPDATE, -1);
//
//        // -1になるということは、まだ一度もサーバからデータを取得していない。
//        if(-1 > lastUpdated) {
//            return true;
//        }
//
//        // 前回updateした時間から、次にupdateをかけるべき時刻を計算し、
//        // 現在時刻がそれよりも先だったら、updateをかける。
//        long currentTime = Calendar.getInstance(Locale.JAPAN).getTimeInMillis();
//        if(currentTime > nextWeeklyUpdateTime(lastUpdated)) {
//            return true;
//        }
//
//        return false;
//    }

//    /**
//     * 今update（webからデータをdownload）すべきかどうかを調べる。
//     * 前回updateした時刻がpreferenceに格納されているので、現在時刻と比較して判定。
//     *
//     * @return
//     */
//    public boolean shouldUpdateTodaysTable() {
//
//        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
//        long lastUpdated = pref.getLong(PREF_KEY_DAILY_UPDATE, -1);
//
//        // -1になるということは、まだ一度もサーバからデータを取得していない。
//        if(-1 > lastUpdated) {
//            return true;
//        }
//
//        // 前回updateした時間から、次にupdateをかけるべき時刻を計算し、
//        // 現在時刻がそれよりも先だったら、updateをかける。
//        long currentTime = Calendar.getInstance(Locale.JAPAN).getTimeInMillis();
//        if(currentTime > nextDailyUpdateTime(lastUpdated)) {
//            return true;
//        }
//
//        return false;
//    }

//    /**
//     * baseTimeを基準に、次に番組表を更新すべき時間を計算する。
//     *
//     * @return
//     */
//    public long nextWeeklyUpdateTime(long baseTimeInMillis) {
//        Calendar c = Calendar.getInstance(Locale.JAPAN);
//        c.setTimeInMillis(baseTimeInMillis);
//
//        return nextWeeklyUpdateTime(c);
//    }

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

        // 今が月曜日で、
        if((Calendar.MONDAY == c.get(Calendar.DAY_OF_WEEK))
            // 5時10分より前ならば、その直後の5時10分が更新タイム。
                && (4 > c.get(Calendar.HOUR_OF_DAY) ||
                   (4 == c.get(Calendar.HOUR_OF_DAY) && 50 < c.get(Calendar.MINUTE)))) {

                c.set(Calendar.HOUR_OF_DAY, 5);
                c.set(Calendar.MINUTE, 10);
        }
        // 上記に当てはまらなかったら、次の月曜の早朝5時10分
        else {
            do {
                c.add(Calendar.DATE, 1);
            } while(Calendar.MONDAY != c.get(Calendar.DAY_OF_WEEK));

            c.set(Calendar.HOUR_OF_DAY, 5);
            c.set(Calendar.MINUTE, 10);
        }

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