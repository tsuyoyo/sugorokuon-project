/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.fragments.settings;

import tsuyogoro.sugorokuon.models.prefs.BrowserCacheSettingPreference;
import android.annotation.TargetApi;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

public class BrowserCacheSettingsPreferenceFragment extends PreferenceFragment {

    public BrowserCacheSettingsPreferenceFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceScreen screen =
                getPreferenceManager().createPreferenceScreen(getActivity());
        BrowserCacheSettingPreference.addPreferenceTo(screen);

        setPreferenceScreen(screen);
    }

}