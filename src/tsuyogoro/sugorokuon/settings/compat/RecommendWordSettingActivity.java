/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.settings.compat;

import tsuyogoro.sugorokuon.settings.preference.RecommendWordPreference;
import android.annotation.TargetApi;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

@TargetApi(10)
public class RecommendWordSettingActivity extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(this);
		RecommendWordPreference.addPreferenceTo(screen);
		setPreferenceScreen(screen);
	}
	
}
