/**
 * Copyright (c) 
 * 2013 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.settings.preference;

import tsuyogoro.sugorokuon.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class OnAirSearchTargetPreference {

    public static final String PREF_KEY_SEARCH_TARGET = "onair_song_search_target_key";

    public static final int SEARCH_TARGET_YOUTUBE = 0;
    public static final int SEARCH_TARGET_GOOGLE = 1;

    /**
     * targetScreenに、onAir検索の設定Preferenceを追加する。
     *
     * @param targetScreen
     */
    public static void addPreferenceTo(PreferenceScreen targetScreen) {
        Context context = targetScreen.getContext();

        PreferenceCategory category = new PreferenceCategory(context);
        category.setTitle(context.getString(R.string.settings_search_target_settings_title));

        RadioBoxPreference preference = new RadioBoxPreference(context);

        targetScreen.addPreference(category);
        targetScreen.addPreference(preference);
    }

    /**
     * 番組の始まる何分前に通知するかの設定を取得。
     *
     * @param context
     * @return SEARCH_TARGET_YOUTUBEかSEARCH_TARGET_GOOGLEのどっちか。defaultはSEARCH_TARGET_YOUTUBE。
     */
    public static int getSearchTarget(Context context) {
        SharedPreferences defPref = PreferenceManager.getDefaultSharedPreferences(context);
        return defPref.getInt(PREF_KEY_SEARCH_TARGET, SEARCH_TARGET_YOUTUBE);
    }

    private static class RadioBoxPreference extends Preference {

        public RadioBoxPreference(Context context) {
            super(context);
        }

        @Override
        protected View onCreateView(ViewGroup parent) {
            super.onCreateView(parent);
            return createOptions();
        }

        private RadioGroup createOptions() {
            // これまでの設定を見て、focusを当てる
            SharedPreferences pref = getPreferenceManager().getSharedPreferences();
            // defaultはyoutube。
            int currentSettings = pref.getInt(PREF_KEY_SEARCH_TARGET, 0);

            RadioGroup options = new RadioGroup(getContext());

            // Youtubeボタン
            RadioButton radioBtnYoutube = new RadioButton(getContext());
            radioBtnYoutube.setText(getContext().getText(
                    R.string.settings_search_target_settings_youtube));
            radioBtnYoutube.setId(SEARCH_TARGET_YOUTUBE);
            if(SEARCH_TARGET_YOUTUBE == currentSettings) {
                radioBtnYoutube.setChecked(true);
            }
            options.addView(radioBtnYoutube);

            // Googleボタン
            RadioButton radioBtnGoogle = new RadioButton(getContext());
            radioBtnGoogle.setText(getContext().getText(
                    R.string.settings_search_target_settings_google));
            radioBtnGoogle.setId(SEARCH_TARGET_GOOGLE);
            if(SEARCH_TARGET_GOOGLE == currentSettings) {
                radioBtnGoogle.setChecked(true);
            }
            options.addView(radioBtnGoogle);

            // checkされたら設定値を変更。
            options.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    SharedPreferences pref = getPreferenceManager().getSharedPreferences();
                    pref.edit().putInt(PREF_KEY_SEARCH_TARGET, checkedId).commit();
                }
            });

            return options;
        }
    }
}