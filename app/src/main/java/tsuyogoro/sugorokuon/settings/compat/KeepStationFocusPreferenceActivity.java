/**
 * Copyright (c) 
 * 2013 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.settings.compat;

import tsuyogoro.sugorokuon.settings.preference.KeepStationFocusPreference;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public class KeepStationFocusPreferenceActivity extends PreferenceActivity {

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(this);
        KeepStationFocusPreference.addPreferenceTo(screen);
        setPreferenceScreen(screen);
    }

}