/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.settings.preference;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * このアプリを起動したことがあるか？のpreferenceへのaccessor。
 * 
 * @author Tsuyoyo
 *
 */
public class LaunchedCheckPreference {

	private static final String PREF_KEY_WORDS = "pref_key_launched_ver1_0";	
	
	/**
	 * このアプリを起動したことがあるかどうか？
	 * 
	 * @param context
	 * @return
	 */
	public static boolean hasLaunched(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
        	.getBoolean(PREF_KEY_WORDS, false);
	}
	
	/**
	 * アプリを起動したらこの関数を呼んで、「起動したことがあるフラグ」を立てる。
	 * 
	 * @param context
	 */
	public static void setLaunched(Context context) {
		Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean(PREF_KEY_WORDS, true);
		editor.commit();
	}
	
}
