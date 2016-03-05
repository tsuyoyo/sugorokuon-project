/**
 * Copyright (c)
 * 2016 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network;

import okhttp3.OkHttpClient;

public class OkHttpWrapper {

    public static OkHttpClient buildClient() {
        return new OkHttpClient.Builder()
                .build();
    }

}
