/**
 * Copyright (c)
 * 2016 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;

public class OkHttpWrapper {

    /**
     * DebugビルドではStethoInterceptorを差し込んで、Network通信がdumpできるようにする
     *
     * @return
     */
    public static OkHttpClient buildClient() {
        return new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
    }

}
