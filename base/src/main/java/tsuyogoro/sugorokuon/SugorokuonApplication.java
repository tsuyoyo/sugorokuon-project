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
import com.tomoima.debot.DebotConfigurator;
import com.tomoima.debot.DebotStrategyBuilder;

import tsuyogoro.sugorokuon.base.R;
import tsuyogoro.sugorokuon.debug.DebugMenuStrategy;
import tsuyogoro.sugorokuon.debug.RecommendDebugStrategy;
import tsuyogoro.sugorokuon.di.DaggerSugorokuonAppComponent;
import tsuyogoro.sugorokuon.radiko.RadikoApiModule;
import tsuyogoro.sugorokuon.di.RepositoryModule;
import tsuyogoro.sugorokuon.di.SugorokuonAppComponent;
import tsuyogoro.sugorokuon.di.SugorokuonAppModule;

public class SugorokuonApplication extends Application {

    private Tracker mTracker;

    private RefWatcher mRefWatcher;

    private SugorokuonAppComponent appComponent;

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
        setupDebugMenu();
        SugorokuonLog.d("SugorokuonApplication : onCreate()");
        StethoWrapper.setup(this);
        mRefWatcher = LeakCanary.install(this);

        appComponent = DaggerSugorokuonAppComponent.builder()
                .sugorokuonAppModule(new SugorokuonAppModule(this))
                .radikoApiModule(new RadikoApiModule())
                .repositoryModule(new RepositoryModule())
                .build();

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

    private void setupDebugMenu() {
        // Debot (Debug menu which is displayed by shaking device)
        DebotConfigurator.configureWithCustomizedMenu(
                new DebotStrategyBuilder.Builder()
                        .registerMenu("Debug menu",
                                new DebugMenuStrategy())
                        .registerMenu("Recommend debug menu",
                                new RecommendDebugStrategy())
                        .build()
                        .getStrategyList()
        );
    }
}
