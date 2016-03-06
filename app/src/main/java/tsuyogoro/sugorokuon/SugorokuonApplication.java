/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import tsuyogoro.sugorokuon.di.DaggerNetworkApiComponent;
import tsuyogoro.sugorokuon.di.NetworkApiComponent;
import tsuyogoro.sugorokuon.di.NetworkApiModule;

public class SugorokuonApplication extends Application {

    private Tracker mTracker;

    private RefWatcher mRefWatcher;

    synchronized public Tracker getTracker() {
        // https://developers.google.com/analytics/devguides/collection/android/v4/?hl=ja
        if (null == mTracker) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

    private NetworkApiComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        StethoWrapper.setup(this);

        mRefWatcher = LeakCanary.install(this);

        mAppComponent = DaggerNetworkApiComponent.builder()
                .networkApiModule(new NetworkApiModule())
                .build();
    }

    public NetworkApiComponent component() {
        return mAppComponent;
    }

    public static RefWatcher getRefWatcher(Context context) {
        return ((SugorokuonApplication) context.getApplicationContext()).mRefWatcher;
    }
}
