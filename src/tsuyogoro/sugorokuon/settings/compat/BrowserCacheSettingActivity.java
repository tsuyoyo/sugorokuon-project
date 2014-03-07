/**
 * Copyright (c) 
 * 2013 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.settings.compat;

import tsuyogoro.sugorokuon.settings.preference.BrowserCacheSettingPreference;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public class BrowserCacheSettingActivity extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(this);
		BrowserCacheSettingPreference.addPreferenceTo(screen);
		setPreferenceScreen(screen);
	}

}
