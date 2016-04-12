/**
 * Copyright (c)
 * 2016 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon;

import android.content.Context;
import android.util.Log;

import com.facebook.stetho.Stetho;

// DebugビルドでのみStethoを有効にするため、このようなwrapperを用意
public class StethoWrapper {
    public static void setup(Context context) {
        Stetho.initializeWithDefaults(context);
    }
}
