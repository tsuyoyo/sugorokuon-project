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

import tsuyogoro.sugorokuon.v3.api.RadikoApiModule;
import tsuyogoro.sugorokuon.utils.SugorokuonLog;
import tsuyogoro.sugorokuon.v3.di.DaggerSugorokuonAppComponent;
import tsuyogoro.sugorokuon.v3.di.RepositoryModule;
import tsuyogoro.sugorokuon.v3.di.SugorokuonAppComponent;
import tsuyogoro.sugorokuon.v3.di.SugorokuonAppModule;

public class SugorokuonApplication extends Application {

    private Tracker mTracker;

    private RefWatcher mRefWatcher;

    private SugorokuonAppComponent appComponent;

    // v2.3.1 : アプリが再起動しなくなったり、動きが怪しいので消した
//    public static FirebaseAnalytics firebaseAnalytics;

    synchronized public Tracker getTracker() {
        // https://developers.google.com/analytics/devguides/collection/android/v4/?hl=ja
        if (null == mTracker) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SugorokuonLog.d("SugorokuonApplication : onCreate()");
        StethoWrapper.setup(this);
        mRefWatcher = LeakCanary.install(this);

        appComponent = DaggerSugorokuonAppComponent.builder()
                .sugorokuonAppModule(new SugorokuonAppModule(this))
                .radikoApiModule(new RadikoApiModule())
                .repositoryModule(new RepositoryModule())
                .build();

        // v2.3.1 : アプリが再起動しなくなったり、動きが怪しいので消した
//        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    public static RefWatcher getRefWatcher(Context context) {
        return ((SugorokuonApplication) context.getApplicationContext()).mRefWatcher;
    }

    public static SugorokuonApplication application(Context context) {
        return (SugorokuonApplication) context.getApplicationContext();
    }

    public SugorokuonAppComponent appComponent() {
        return appComponent;
    }
}
