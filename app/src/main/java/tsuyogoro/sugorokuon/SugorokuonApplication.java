/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class SugorokuonApplication extends Application {

    private Tracker mTracker;

    // https://developers.google.com/analytics/devguides/collection/android/v4/?hl=ja
    synchronized public Tracker getTracker() {
        if (null == mTracker) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return  mTracker;
    }

}
