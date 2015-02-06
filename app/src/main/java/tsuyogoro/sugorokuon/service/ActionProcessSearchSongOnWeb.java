/**
 * Copyright (c) 
 * 2013 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.service;

import tsuyogoro.sugorokuon.settings.preference.OnAirSearchTargetPreference;
import android.content.Context;
import android.content.Intent;

class ActionProcessSearchSongOnWeb {

    private static final String YOUTUBE_PACKAGE = "com.google.android.youtube";

    public void launchSearchApp(Context context, String artist, String songTitle) {

        int target = OnAirSearchTargetPreference.getSearchTarget(context);

        // Youtube検索を起動。
        if(OnAirSearchTargetPreference.SEARCH_TARGET_YOUTUBE == target) {
            Intent intent = new Intent(Intent.ACTION_SEARCH);
            intent.setPackage(YOUTUBE_PACKAGE);
            intent.putExtra("query", artist + " " + songTitle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        // Web検索を起動。
        else if(OnAirSearchTargetPreference.SEARCH_TARGET_GOOGLE == target) {
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra("query", artist + " " + songTitle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

    }
}