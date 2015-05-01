/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.models.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;

import tsuyogoro.sugorokuon.R;

public class AutoUpdateSettingPreference {

    public static final String PREF_KEY_AUTO_UPDATE_PREFIX = "pref_key_auto_update";

    private static final String PREF_KEY_AUTO_UPDATE_WEEKLY =
            PREF_KEY_AUTO_UPDATE_PREFIX + "_weekly";

    private static final String PREF_KEY_AUTO_UPDATE_TODAY =
            PREF_KEY_AUTO_UPDATE_PREFIX + "_today";

    private static final String PREF_KEY_AUTO_UPDATE_ONAIR_SONG =
            PREF_KEY_AUTO_UPDATE_PREFIX + "_onair_song";

    // defaultの動作
    private static final boolean DEFAULT_WEEKLY = true;
    private static final boolean DEFAULT_TODAY = false;
    private static final boolean DEFAULT_ON_AIR_SONG = true;

    public static void addPreferenceTo(PreferenceScreen targetScreen) {
        Context context = targetScreen.getContext();

        // Preferenceカテゴリ
        PreferenceCategory category = new PreferenceCategory(context);
        category.setTitle(context.getString(R.string.settings_auto_update));

        // 週間番組表
        SwitchPreference weeklyUpdate = new SwitchPreference(context);
        weeklyUpdate.setDefaultValue(DEFAULT_WEEKLY);
        weeklyUpdate.setTitle(R.string.settings_auto_update_weekly);
        weeklyUpdate.setSummary(R.string.settings_auto_update_weekly_desc);
        weeklyUpdate.setSwitchTextOn(R.string.settings_auto_update_on);
        weeklyUpdate.setSwitchTextOff(R.string.settings_auto_update_off);
        weeklyUpdate.setKey(PREF_KEY_AUTO_UPDATE_WEEKLY);
        weeklyUpdate.setDefaultValue(DEFAULT_WEEKLY);

        // TODO : 仕様がややこしいので、とりあえずweeklyの設定一本に絞る
        // 苦情が来たらdailyとweeklyを分けるように仕様

        // 今日の番組表
//        SwitchPreference todaysUpdate = new SwitchPreference(context);
//        todaysUpdate.setDefaultValue(DEFAULT_TODAY);
//        todaysUpdate.setTitle(R.string.settings_auto_update_today);
//        todaysUpdate.setSummary(R.string.settings_auto_update_today_desc);
//        todaysUpdate.setSwitchTextOn(R.string.settings_auto_update_on);
//        todaysUpdate.setSwitchTextOff(R.string.settings_auto_update_off);
//        todaysUpdate.setKey(PREF_KEY_AUTO_UPDATE_TODAY);
//        todaysUpdate.setDefaultValue(DEFAULT_TODAY);

        // OnAir曲
        SwitchPreference onAirSongsUpdate = new SwitchPreference(context);
        onAirSongsUpdate.setDefaultValue(DEFAULT_ON_AIR_SONG);
        onAirSongsUpdate.setTitle(R.string.settings_auto_update_onair_songs);
        onAirSongsUpdate.setSummary(R.string.settings_auto_update_onair_songs_desc);
        onAirSongsUpdate.setSwitchTextOn(R.string.settings_auto_update_on);
        onAirSongsUpdate.setSwitchTextOff(R.string.settings_auto_update_off);
        onAirSongsUpdate.setKey(PREF_KEY_AUTO_UPDATE_ONAIR_SONG);
        onAirSongsUpdate.setDefaultValue(DEFAULT_ON_AIR_SONG);

        targetScreen.addPreference(category);
        targetScreen.addPreference(weeklyUpdate);
//        targetScreen.addPreference(todaysUpdate);
        targetScreen.addPreference(onAirSongsUpdate);
    }

    /**
     * 週間番組表の自動更新をするかしないかの設定値
     *
     * @param context
     * @return
     */
    public static boolean autoUpdateWeekly(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                PREF_KEY_AUTO_UPDATE_WEEKLY, DEFAULT_WEEKLY);
    }

    /**
     * 今日の番組表の自動更新をするかしないかの設定値
     *
     * @param context
     * @return
     */
    public static boolean autoUpdateToday(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                PREF_KEY_AUTO_UPDATE_WEEKLY, DEFAULT_WEEKLY);
//        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
//                PREF_KEY_AUTO_UPDATE_TODAY, DEFAULT_TODAY);
    }

    /**
     * OnAir曲の自動更新をするかしないかの設定値
     *
     * @param context
     * @return
     */
    public static boolean autoUpdateOnAirSongs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                PREF_KEY_AUTO_UPDATE_ONAIR_SONG, DEFAULT_ON_AIR_SONG);
    }

}
