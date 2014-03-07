/**
 * Copyright (c) 
 * 2013 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.settings.fragment;

import tsuyogoro.sugorokuon.settings.preference.KeepStationFocusPreference;
import android.annotation.TargetApi;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

/**
 * �ǂ̃t�H�[�J�X���L�����邩�ǂ����̐ݒ���s��Fragment�BTargetApi��11�iHC�j�ȏ�B
 * 
 */
@TargetApi(11)
public class KeepStationFocusPreferenceFragment extends PreferenceFragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PreferenceManager mgr = getPreferenceManager();
		PreferenceScreen screen = mgr.createPreferenceScreen(getActivity());
		
		KeepStationFocusPreference.addPreferenceTo(screen);
		
		setPreferenceScreen(screen);
	}

}
