/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.settings.fragment;

import tsuyogoro.sugorokuon.settings.preference.AreaSettingPreference;
import android.annotation.TargetApi;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

/**
 * �����G���A��ݒ肷�邽�߂�Preference�B
 * TargetApi��11�iHC�j�ȏ�
 */
@TargetApi(11)
public class AreaSettingPreferenceFragment extends PreferenceFragment {
	
	public AreaSettingPreferenceFragment() {
		super();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Area�ݒ��Preference����ʂɒǉ��B
		PreferenceScreen screen = 
			getPreferenceManager().createPreferenceScreen(getActivity());
		AreaSettingPreference.addPreferenceTo(screen);
		
		setPreferenceScreen(screen);		
	}
	
}
