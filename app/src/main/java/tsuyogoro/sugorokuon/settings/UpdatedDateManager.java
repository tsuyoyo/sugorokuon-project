/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.settings;

import java.util.Calendar;
import java.util.Locale;

import tsuyogoro.sugorokuon.constant.Area;
import tsuyogoro.sugorokuon.constant.Region;
import tsuyogoro.sugorokuon.settings.preference.AreaSettingPreference;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * 前回Updateした日時を管理し、次にいつupdateすべきか？を教えてくれるクラス。
 *
 * @author Tsuyoyo
 */
public class UpdatedDateManager
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static UpdatedDateManager sInstance;

    private final String PREF_KEY = "pref_key_last_update";

    private Context mContext;

    private UpdatedDateManager(Context context) {
        mContext = context;

        // 設定値変更の通知を受け取るためにregister
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        // TODO : Area設定＞Areaを空にする＞settings＞Areaを入れる＞updateがかからない
        // というバグがあったので要注意。再現するなら直す。

        // Area設定の変更だったら最終更新日時をクリア。次にMainに戻ったときにupdateを促す。
        for(Area a : Area.values()) {
            if(key.equals(AreaSettingPreference.getAreaPreferenceKey(a))) {
                clearLastUpdate();
            }
        }
        for(Region r : Region.values()) {
            if(key.equals(AreaSettingPreference.getRegionPreferenceKey(r))) {
                clearLastUpdate();
            }
        }
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
            sInstance = new UpdatedDateManager(context);
        }
        return sInstance;
    }

    /**
     * LastUpdateの日時を更新。
     *
     */
    public void updateLastUpdate() {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
        editor.putLong(PREF_KEY, Calendar.getInstance(Locale.JAPAN).getTimeInMillis());
        editor.commit();
    }

    /**
     * LastUpdateの日時をクリア。
     *
     */
    public void clearLastUpdate() {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
        editor.putLong(PREF_KEY, -1);
        editor.commit();
    }

    /**
     * 今update（webからデータをdownload）すべきかどうかを調べる。
     * 前回updateした時刻がpreferenceに格納されているので、引数のnowと比べて判定。
     *
     * @param now 今updateすべき時刻か？の「今」。
     * @return
     */
    public boolean shouldUpdate(Calendar now) {
        // -1になるということは、まだ一度もサーバからデータを取得していない。
        long lastUpdated = getLastUpdatedByMilSec();
        if(0 > lastUpdated) {
            return true;
        }

        // 前回updateした時間から、次にupdateをかけるべき時刻を計算し、
        // 現在時刻がそれよりも先だったら、updateをかける。
        long nextUpdate = calculateNextUpdateTime(lastUpdated);
        if(now.getTimeInMillis() > nextUpdate) {
            return true;
        }
        return false;
    }

    /**
     * 最後に更新した日時を取得。
     * まだ一度も更新したことがなかったら-1が返る。
     *
     * @return
     */
    public long getLastUpdatedByMilSec() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        return pref.getLong(PREF_KEY, -1);
    }

    /**
     * 最後に更新した日時を取得。
     * まだ一度も更新したことがなかったらnullが返る。
     *
     * @return
     */
    public Calendar getLastUpdatedByCalendar() {
        long lastUpdatedTime = getLastUpdatedByMilSec();
        if(0 > lastUpdatedTime) {
            return null;
        }
        Calendar c = Calendar.getInstance(Locale.JAPAN);
        c.setTimeInMillis(lastUpdatedTime);
        return c;
    }

    /**
     * 次に番組表を更新すべき時間を計算する。
     * 基本的に、定期的な番組表の更新は、毎週月曜日の早朝とする。
     *
     * @param now 今の時刻をミリsecで。
     * @return
     */
    public long calculateNextUpdateTime(long now) {
        Calendar c = Calendar.getInstance(Locale.JAPAN);
        c.setTimeInMillis(now);
        return calculateNextUpdateTime(c);
    }

    /**
     * 次に番組表を更新すべき時間を計算する。
     * 基本的に、定期的な番組表の更新は、毎週月曜日の早朝とする。
     *
     * @param now 今の時刻をCalendarインスタンスで。
     * @return
     */
    public long calculateNextUpdateTime(Calendar now) {

        // 月曜日で、5時10分より前に更新をかけた場合は、その直後の5時10分が更新タイム。
        if(Calendar.MONDAY == now.get(Calendar.DAY_OF_WEEK)) {
            if((4 > now.get(Calendar.HOUR_OF_DAY))
                    || (4 == now.get(Calendar.HOUR_OF_DAY) && 50 < now.get(Calendar.MINUTE))) {
                now.set(Calendar.HOUR_OF_DAY, 5);
                now.set(Calendar.MINUTE, 10);
                return now.getTimeInMillis();
            }
        }

        // 上記に当てはまらなかったら、次の月曜の早朝5時10分。
        do {
            now.add(Calendar.DATE, 1);
        } while(Calendar.MONDAY != now.get(Calendar.DAY_OF_WEEK));
        now.set(Calendar.HOUR_OF_DAY, 5);
        now.set(Calendar.MINUTE, 10);

        return now.getTimeInMillis();
    }

}