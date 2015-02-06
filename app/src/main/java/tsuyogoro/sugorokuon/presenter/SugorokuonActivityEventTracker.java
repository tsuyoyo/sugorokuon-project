/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.presenter;

import tsuyogoro.sugorokuon.R;
import android.content.Context;

import com.google.analytics.tracking.android.EasyTracker;

class SugorokuonActivityEventTracker {

    /**
     * Main screenで発生したイベントをGAに送る。
     * イベントのcategoryは共通して、
     * <string name="ga_event_category_main_screen_operation">Main screen operation</string>
     * を使う。
     *
     * @param categoryStrId strings_for_ga_tracking.xmlに定義したcategoryのstring ID。
     * @param context
     * @param label あれば。なければnullで良い。
     */
    static public void submitGAEvent(int categoryStrId, Context context, String label) {
        EasyTracker.getTracker().trackEvent(
                context.getText(R.string.ga_event_category_main_screen_operation).toString(),
                context.getText(categoryStrId).toString(),
                label, null);
    }

}