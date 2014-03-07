/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.settings.fragment;

import tsuyogoro.sugorokuon.settings.preference.BrowserCacheSettingPreference;
import android.annotation.TargetApi;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

/**
 * �L���b�V����on/off��ݒ肷�邽�߂�Preference�B
 * TargetApi��11�iHC�j�ȏ�
 */
@TargetApi(11)
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
