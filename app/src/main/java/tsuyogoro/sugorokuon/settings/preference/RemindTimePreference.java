/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.settings.preference;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.constant.NotifyTiming;
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

public class RemindTimePreference {

    public static final String PREF_KEY_REMIND_TIME = "pref_key_remind_time";

    /**
     * targetScreenに、RemindTimeの設定Preferenceを追加する。
     *
     * @param targetScreen
     */
    public static void addPreferenceTo(PreferenceScreen targetScreen) {
        Context context = targetScreen.getContext();
        PreferenceCategory category = new PreferenceCategory(context);
        category.setTitle(context.getString(R.string.settings_remindtiming_title));

        RadioBoxPreference preference = new RadioBoxPreference(context);

        targetScreen.addPreference(category);
        targetScreen.addPreference(preference);
    }

    /**
     * 番組の始まる何分前に通知するかの設定を取得。
     *
     * @param context
     * @return {@link NotifyTiming}のどれか。
     */
    public static NotifyTiming getNotifyTime(Context context) {
        SharedPreferences defPref = PreferenceManager.getDefaultSharedPreferences(context);
        int index = defPref.getInt(PREF_KEY_REMIND_TIME, 0);
        return NotifyTiming.values()[index];
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
            int currentSettings = pref.getInt(PREF_KEY_REMIND_TIME, 1); // 1は「10分前」。

            RadioGroup options = new RadioGroup(getContext());
            for(NotifyTiming time : NotifyTiming.values()) {
                RadioButton radioBtn = new RadioButton(getContext());
                radioBtn.setText(getContext().getText(time.optionStrId));
                radioBtn.setId(time.ordinal());
                if(time.ordinal() == currentSettings) {
                    radioBtn.setChecked(true);
                }
                options.addView(radioBtn);
            }

            // checkされたら設定値を変更。
            options.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    SharedPreferences pref = getPreferenceManager().getSharedPreferences();
                    pref.edit().putInt(PREF_KEY_REMIND_TIME, checkedId).commit();
                }
            });

            return options;
        }
    }

}