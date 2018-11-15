/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon;

import android.util.Log;

public class SugorokuonLog {

    static public void d(String msg) {
        if (LogSettings.LOG_LEVEL <= Log.DEBUG) {
            Log.d(LogSettings.TAG, msg);
        }
    }

    static public void w(String msg) {
        if (LogSettings.LOG_LEVEL <= Log.WARN) {
            Log.w(LogSettings.TAG, msg);
        }
    }

    static public void e(String msg) {
        Log.e(LogSettings.TAG, msg);
    }

}
