/**
 * Copyright (c) 
 * 2013 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.settings.preference;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class LastStationFocusPreference {
	
	private static final String PREF_KEY_LAST_FOCUSED_STATION = "pref_key_last_focused_station";	
	
	/**
	 * �ۑ������Ō��station��index���擾�B
	 * 
	 * @param context
	 * @return
	 */
	public static int lastFocusedIndex(Context context) {
		// default�l�̓I�X�X���ԑg(0)
		return PreferenceManager.getDefaultSharedPreferences(context)
        	.getInt(PREF_KEY_LAST_FOCUSED_STATION, 0);
	}
	
	/**
	 * �I������station��index��ۑ��B
	 * 
	 * @param context
	 */
	public static void saveLastFocusedIndex(Context context, int focusedIndex) {
		Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putInt(PREF_KEY_LAST_FOCUSED_STATION, focusedIndex);
		editor.commit();
	}
}
