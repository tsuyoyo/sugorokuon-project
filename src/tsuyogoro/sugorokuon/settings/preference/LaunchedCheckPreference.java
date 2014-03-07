/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.settings.preference;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * ���̃A�v�����N���������Ƃ����邩�H��preference�ւ�accessor�B
 * 
 * @author Tsuyoyo
 *
 */
public class LaunchedCheckPreference {

	private static final String PREF_KEY_WORDS = "pref_key_launched_ver1_0";	
	
	/**
	 * ���̃A�v�����N���������Ƃ����邩�ǂ����H
	 * 
	 * @param context
	 * @return
	 */
	public static boolean hasLaunched(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
        	.getBoolean(PREF_KEY_WORDS, false);
	}
	
	/**
	 * �A�v�����N�������炱�̊֐����Ă�ŁA�u�N���������Ƃ�����t���O�v�𗧂Ă�B
	 * 
	 * @param context
	 */
	public static void setLaunched(Context context) {
		Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean(PREF_KEY_WORDS, true);
		editor.commit();
	}
	
}
