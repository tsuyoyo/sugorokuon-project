/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.fragments.settings;

import tsuyogoro.sugorokuon.models.prefs.RemindBehaviorPreference;
import tsuyogoro.sugorokuon.models.prefs.RemindTimePreference;
import android.annotation.TargetApi;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

public class ReminderSettingFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager mgr = getPreferenceManager();
        PreferenceScreen screen = mgr.createPreferenceScreen(getActivity());

        // 時間設定のRadioボタンを作成
        RemindTimePreference.addPreferenceTo(screen);

        // Notificationの設定（光らせるとか）
        RemindBehaviorPreference.addPreferenceTo(screen);

        setPreferenceScreen(screen);
    }

}