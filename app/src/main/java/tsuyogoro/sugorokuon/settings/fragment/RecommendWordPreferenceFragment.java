/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.settings.fragment;

import tsuyogoro.sugorokuon.settings.preference.RecommendWordPreference;
import android.annotation.TargetApi;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

/**
 * おまかせキーワードを設定するFragment。 TargetApiは11（HC）以上。
 *
 */
@TargetApi(11)
public class RecommendWordPreferenceFragment extends PreferenceFragment {

    public static final String PREF_KEY_WORDS = "pref_key_words_slot_%d";

    // おすすめワードを入れるSlot数
    public static final int SLOT_NUM = 5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceScreen screen =
                getPreferenceManager().createPreferenceScreen(getActivity());
        RecommendWordPreference.addPreferenceTo(screen);
        setPreferenceScreen(screen);
    }

}