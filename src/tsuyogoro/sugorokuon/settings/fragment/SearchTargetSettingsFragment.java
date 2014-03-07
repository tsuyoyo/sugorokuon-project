/**
 * Copyright (c) 
 * 2013 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.settings.fragment;

import tsuyogoro.sugorokuon.settings.preference.OnAirSearchTargetPreference;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

public class SearchTargetSettingsFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PreferenceManager mgr = getPreferenceManager();
		PreferenceScreen screen = mgr.createPreferenceScreen(getActivity());
		
		OnAirSearchTargetPreference.addPreferenceTo(screen);
		
		setPreferenceScreen(screen);
	}	
	
}
