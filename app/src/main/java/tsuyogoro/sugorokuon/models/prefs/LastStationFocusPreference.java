/**
 * Copyright (c) 
 * 2013 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.models.prefs;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class LastStationFocusPreference {

    private static final String PREF_KEY_LAST_FOCUSED_STATION = "pref_key_last_focused_station";

    /**
     * 保存した最後のstationのindexを取得。
     *
     * @param context
     * @return
     */
    public static int lastFocusedIndex(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(PREF_KEY_LAST_FOCUSED_STATION, 0);
    }

    /**
     * 選択したstationのindexを保存。
     *
     * @param context
     */
    public static void saveLastFocusedIndex(Context context, int focusedIndex) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putInt(PREF_KEY_LAST_FOCUSED_STATION, focusedIndex);
        editor.apply();
    }
}