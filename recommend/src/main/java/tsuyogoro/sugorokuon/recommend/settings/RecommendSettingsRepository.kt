package tsuyogoro.sugorokuon.recommend.settings

import android.content.Context
import android.content.SharedPreferences
import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import tsuyogoro.sugorokuon.recommend.keyword.RecommendKeyword
import tsuyogoro.sugorokuon.recommend.keyword.RecommendKeywordPreferenceKeys

class RecommendSettingsRepository(
    context: Context,
    sharedPreferences: SharedPreferences
) {
    private val keywordPreferenceKeys = RecommendKeywordPreferenceKeys.getAll(context)

    private val recommendKeywords = BehaviorProcessor.create<List<RecommendKeyword>>()

    init {
        // Set initial value
        updateRecommendKeywords(sharedPreferences)

        // Observe sharedPreference to keep recommendKeywords the latest
        sharedPreferences.registerOnSharedPreferenceChangeListener { pref, key ->
            if (keywordPreferenceKeys.contains(key)) {
                updateRecommendKeywords(pref)
            }
        }
    }

    fun observeRecommendKeywords(): Flowable<List<RecommendKeyword>> = recommendKeywords.hide()

    fun getRecommentKeywords() : List<RecommendKeyword> = recommendKeywords.value

    private fun updateRecommendKeywords(sharedPreferences: SharedPreferences) {
        val keywords = mutableListOf<RecommendKeyword>().apply {
            keywordPreferenceKeys.forEachIndexed { index, key ->
                add(RecommendKeyword(index, sharedPreferences.getString(key, "")))
            }
        }
        recommendKeywords.onNext(keywords)
    }
}