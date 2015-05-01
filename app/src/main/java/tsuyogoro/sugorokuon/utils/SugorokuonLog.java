/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.utils;

import android.util.Log;

public class SugorokuonLog {

    static final private String TAG = "SugorokuonLog";

    static public void d(String msg) {
        Log.d(TAG, msg);
    }

    static public void w(String msg) {
        Log.w(TAG, msg);
    }

    static public void e(String msg) {
        Log.e(TAG, msg);
    }

}
