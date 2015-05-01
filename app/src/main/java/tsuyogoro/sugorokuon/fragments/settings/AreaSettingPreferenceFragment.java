/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.fragments.settings;

import tsuyogoro.sugorokuon.models.prefs.AreaSettingPreference;
import android.annotation.TargetApi;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

/**
 * 検索エリアを設定するためのPreference。
 */
public class AreaSettingPreferenceFragment extends PreferenceFragment {

    public AreaSettingPreferenceFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Area設定のPreferenceを画面に追加。
        PreferenceScreen screen =
                getPreferenceManager().createPreferenceScreen(getActivity());
        AreaSettingPreference.addPreferenceTo(screen);

        setPreferenceScreen(screen);
    }

}