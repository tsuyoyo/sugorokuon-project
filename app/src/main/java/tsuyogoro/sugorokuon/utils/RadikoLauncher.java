/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

public class RadikoLauncher {

    private static final String GOOGLE_PLAY_URL = "market://details?id=jp.radiko.Player";

    /**
     * ラジオアプリの起動を行う。
     * radikoアプリがinstallされていなかったらmarketへ誘導。
     *
     * @param context
     */
    public static void launch(Context context) {
        PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage("jp.radiko.Player");
        try {
            context.startActivity(intent);
        } catch(Exception e) {
            // ダメだったらマーケットのinstallページへ。
            Uri googleplayuri = Uri.parse(GOOGLE_PLAY_URL);
            Intent googlePlayIntent = new Intent(Intent.ACTION_VIEW,  googleplayuri);
            googlePlayIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(googlePlayIntent);
        }
    }

}
