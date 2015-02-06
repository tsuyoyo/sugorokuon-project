/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.settings;

import java.util.List;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.constant.Area;
import tsuyogoro.sugorokuon.constant.NotifyTiming;
import tsuyogoro.sugorokuon.settings.compat.AreaSettingPreferenceActivity;
import tsuyogoro.sugorokuon.settings.compat.BrowserCacheSettingActivity;
import tsuyogoro.sugorokuon.settings.compat.KeepStationFocusPreferenceActivity;
import tsuyogoro.sugorokuon.settings.compat.OnAirSearchSettingActivity;
import tsuyogoro.sugorokuon.settings.compat.RecommendWordSettingActivity;
import tsuyogoro.sugorokuon.settings.compat.ReminderSettingActivity;
import tsuyogoro.sugorokuon.settings.preference.AreaSettingPreference;
import tsuyogoro.sugorokuon.settings.preference.RecommendWordPreference;
import tsuyogoro.sugorokuon.settings.preference.RemindBehaviorPreference;
import tsuyogoro.sugorokuon.settings.preference.RemindTimePreference;
import tsuyogoro.sugorokuon.util.SugorokuonUtils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuItem;

import com.google.analytics.tracking.android.EasyTracker;

/**
 * 設定画面のActivity。
 *
 * @author Tsuyoyo
 */
public class SugorokuonSettingActivity extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int MENU_HELP_MANUAL = 0;

    private static final String TAG_HELP_DIALOG = "help_dialog";

    private static final int DIALOG_ID_HELP = 100;

    public static final int RESULT_SETTINGS_NO_UPDATE 	 = 1;
    public static final int RESULT_AREA_SETTINGS_UPDATED = 2;
    public static final int RESULT_KEYWORD_UPDATED       = 3;
    public static final int RESULT_REMINDER_UPDATED      = 4;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 設定が変わるとResultが変わる。ここで初期化。
        setResult(RESULT_SETTINGS_NO_UPDATE);

        // 設定値変更の通知を受け取るためにregister
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        // Gingerbreadの場合は、ここでxmlを読み込ませて画面を作る。
        if(!SugorokuonUtils.isHigherThanGingerBread()) {
            addPreferencesFromResource(R.xml.settings_preference);
        }

        //------- for test --------//
//        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
//        String log = pref.getString(RecommendReminderReserver.KEY_NOTIFY_TIME, "");
//        Toast.makeText(this, log, Toast.LENGTH_LONG).show();
    }

    /*
     * HC(ApiLevel 10)以上の場合は、
     * addPreferencesFromResourceの代わりにonBuildHeadersでxmlを読み込む。
     * 
     * （メモ：http://techbooster.org/android/application/3052/ より）
     * onBuildHeaders()はHeaderのBuildが必要になった際に呼び出されます。
     * 順を試してみたところ、「onCreate」→「onBuildHeaders」→「xmlで指定したfragment」→「onStart」… でした。
     * 
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onBuildHeaders(List<Header> target) {
        // ApiLevel 11以上ならばFragmentを読み込む。
        if(SugorokuonUtils.isHigherThanGingerBread()) {
            loadHeadersFromResource(R.xml.settings_preference_fragment_header, target);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // For mobile Google analytics.
        EasyTracker.getInstance().activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // For mobile Google analytics.
        EasyTracker.getInstance().activityStop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /*
     * GingerBread上での動作用。
     */
    @SuppressWarnings("deprecation")
    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        Dialog dialog = null;
        switch(id) {
            case DIALOG_ID_HELP:
                dialog = createHelpDialog(this);
                break;
            default:
                dialog = super.onCreateDialog(id, args);
                break;
        }
        return dialog;
    }

    /*
     * GingerBread上での動作用。
     */
    @SuppressWarnings("deprecation")
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
                                         Preference preference) {
        boolean res;
        String key = preference.getKey();
        if(key.equals("area_setting_category_key")) {
            startActivity(new Intent(this, AreaSettingPreferenceActivity.class));
            res = true;
        }
        else if(key.equals("recommend_keyword_setting_category_key")) {
            startActivity(new Intent(this, RecommendWordSettingActivity.class));
            res = true;
        }
        else if(key.equals("reminder_setting_category_key")) {
            startActivity(new Intent(this, ReminderSettingActivity.class));
            res = true;
        }
        else if(key.equals("onair_song_search_target_category_key")) {
            startActivity(new Intent(this, OnAirSearchSettingActivity.class));
            res = true;
        }
        else if(key.equals("browser_cache_settings_category_key")) {
            startActivity(new Intent(this, BrowserCacheSettingActivity.class));
            res = true;
        }
        else if(key.equals("keep_station_focus_target_key")) {
            startActivity(new Intent(this, KeepStationFocusPreferenceActivity.class));
            res = true;
        }
        else {
            res = super.onPreferenceTreeClick(preferenceScreen, preference);
        }
        return res;
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Helpのダイアログ
        MenuItem helpOption =
                menu.add(Menu.NONE, MENU_HELP_MANUAL, 0, getString(R.string.settings_menu_help))
                        .setIcon(R.drawable.action_help);

        if(SugorokuonUtils.isHigherThanGingerBread()) {
            helpOption.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case MENU_HELP_MANUAL:
                // GingerBreadの時はshowDialogをして、HC以上ならFragmentDialogをshow。
                if(SugorokuonUtils.isHigherThanGingerBread()) {
                    HelpDialogFragment dialog = new HelpDialogFragment();
                    dialog.show(getFragmentManager(), TAG_HELP_DIALOG);
                } else {
                    showDialog(DIALOG_ID_HELP, null);
                }
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * Preferenceの値が変更されたら通知を受け取る。
     * 呼び出し元に対して、RESULT_XXX を返すように。
     *
     * @param sharedPreferences
     * @param key
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        // Area設定が変わった。
        if(key.contains("pref_key_target_area") || key.contains("pref_key_target_region")) {
            setResult(RESULT_AREA_SETTINGS_UPDATED);

            // For mobile google analytics tracking.
            List<Area> areas = AreaSettingPreference.getTargetAreas(this);
            String areaInfo = "";
            for(Area a : areas) {
                areaInfo += getText(a.strId) + " , ";
            }
            EasyTracker.getInstance().setContext(this);
            EasyTracker.getTracker().trackEvent(
                    getText(R.string.ga_event_category_settings_changed).toString(),
                    getText(R.string.ga_event_action_change_area).toString(),
                    areaInfo, null);
        }
        // キーワードが変わった。
        else if(key.contains("pref_key_words_slot")) {
            setResult(RESULT_KEYWORD_UPDATED);

            // For mobile google analytics tracking.
            List<String> keyWords = RecommendWordPreference.getKeyWord(this);
            String keywordInfo = "";
            for(String k : keyWords) {
                keywordInfo += k + " , ";
            }
            EasyTracker.getInstance().setContext(this);
            EasyTracker.getTracker().trackEvent(
                    getText(R.string.ga_event_category_settings_changed).toString(),
                    getText(R.string.ga_event_action_change_keyword).toString(),
                    keywordInfo, null);
        }
        // Remind設定が変わった。
        else if(key.contains("pref_key_remind")) {
            setResult(RESULT_REMINDER_UPDATED);

            // For mobile google analytics tracking.
            NotifyTiming timing = RemindTimePreference.getNotifyTime(this);
            EasyTracker.getInstance().setContext(this);
            EasyTracker.getTracker().trackEvent(
                    getText(R.string.ga_event_category_settings_changed).toString(),
                    getText(R.string.ga_event_action_change_reminder).toString(),
                    timing.name() +
                            "(" + RemindBehaviorPreference.wayToNotify(this) + ")",
                    null);
        }
    }

    /**
     * Help情報を表示するDialogクラス。
     *
     * @author Tsuyoyo
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class HelpDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return createHelpDialog(getActivity());
        }
    }

    private static AlertDialog createHelpDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.settings_manual_title);
        builder.setMessage(R.string.settings_manual);
        builder.setPositiveButton(context.getString(R.string.ok), null);
        builder.setCancelable(true);
        return builder.create();
    }

}