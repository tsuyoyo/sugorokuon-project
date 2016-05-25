/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.models.prefs;

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

    private static final String PREF_KEY_WORDS_V2 = "pref_key_launched_ver2_x";

    private static final String PREF_KEY_WORDS_V2_2 = "pref_key_launched_ver2_2";

    private static final String PREF_KEY_WORDS_V2_3 = "pref_key_launched_ver2_3";

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
        editor.apply();
    }

    /**
     * v2.x系のアプリを起動したことがあるか?
     *
     * @param context
     * @return
     */
    public static boolean hasV2Launched(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_KEY_WORDS_V2, false);
    }

    public static void setLaunchedV2(Context context) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(PREF_KEY_WORDS_V2, true);
        editor.apply();
    }

    /**
     * v2.2のアプリを起動したことがあるか?
     *
     * @param context
     * @return
     */
    public static boolean hasV22Launched(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_KEY_WORDS_V2_2, false);
    }

    public static void setLaunchedV22(Context context) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(PREF_KEY_WORDS_V2_2, true);
        editor.apply();
    }

    public static boolean hasV23Launched(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_KEY_WORDS_V2_3, false);
    }

    public static void setLaunchedV23(Context context) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(PREF_KEY_WORDS_V2_3, true);
        editor.apply();
    }
}