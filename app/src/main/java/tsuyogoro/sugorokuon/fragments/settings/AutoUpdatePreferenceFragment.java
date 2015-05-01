/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.fragments.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import tsuyogoro.sugorokuon.models.prefs.AutoUpdateSettingPreference;

public class AutoUpdatePreferenceFragment extends PreferenceFragment {

    public AutoUpdatePreferenceFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceScreen screen =
                getPreferenceManager().createPreferenceScreen(getActivity());
        AutoUpdateSettingPreference.addPreferenceTo(screen);

        setPreferenceScreen(screen);
    }
}
