/**
 * Copyright (c)
 * 2018 tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.recommend.keyword

import android.content.Context
import tsuyogoro.sugorokuon.recommend.R

internal object RecommendKeywordPreferenceKeys {
    fun getAll(context: Context): List<String> =
        arrayListOf(
            context.resources.getString(R.string.pref_key_keyword_01),
            context.resources.getString(R.string.pref_key_keyword_02),
            context.resources.getString(R.string.pref_key_keyword_03),
            context.resources.getString(R.string.pref_key_keyword_04),
            context.resources.getString(R.string.pref_key_keyword_05),
            context.resources.getString(R.string.pref_key_keyword_06),
            context.resources.getString(R.string.pref_key_keyword_07),
            context.resources.getString(R.string.pref_key_keyword_08),
            context.resources.getString(R.string.pref_key_keyword_09),
            context.resources.getString(R.string.pref_key_keyword_10)
        )
}