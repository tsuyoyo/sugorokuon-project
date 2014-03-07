/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.settings.fragment;

import tsuyogoro.sugorokuon.settings.preference.RemindBehaviorPreference;
import tsuyogoro.sugorokuon.settings.preference.RemindTimePreference;
import android.annotation.TargetApi;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

@TargetApi(11)
public class ReminderSettingFragment extends PreferenceFragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PreferenceManager mgr = getPreferenceManager();
		PreferenceScreen screen = mgr.createPreferenceScreen(getActivity());
		
		// ���Ԑݒ��Radio�{�^�����쐬
		RemindTimePreference.addPreferenceTo(screen);
		
		// Notification�̐ݒ�i���点��Ƃ��j
		RemindBehaviorPreference.addPreferenceTo(screen);
		
		setPreferenceScreen(screen);
	}
	
}
