/**
 * Copyright (c)
 * 2018 tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.recommend.settings

import android.content.Context
import android.content.SharedPreferences
import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import tsuyogoro.sugorokuon.SugorokuonLog
import tsuyogoro.sugorokuon.recommend.R
import tsuyogoro.sugorokuon.recommend.keyword.RecommendKeyword
import tsuyogoro.sugorokuon.recommend.keyword.RecommendKeywordPreferenceKeys
import tsuyogoro.sugorokuon.recommend.reminder.ReminderSettingsPreference
import tsuyogoro.sugorokuon.recommend.reminder.ReminderTiming
import tsuyogoro.sugorokuon.recommend.reminder.ReminderType

class RecommendSettingsRepository(
    context: Context,
    private val sharedPreferences: SharedPreferences
) {
    private val keywordPreferenceKeys = RecommendKeywordPreferenceKeys.getAll(context)

    private val reminderTypePreferenceKeys = HashMap<String, ReminderType>().apply {
        put(context.getString(R.string.pref_key_reminder_light), ReminderType.LIGHT)
        put(context.getString(R.string.pref_key_reminder_sound), ReminderType.SOUND)
        put(context.getString(R.string.pref_key_reminder_vibration), ReminderType.VIBRATION)
    }
    
    private val notifyTimingPreferenceKey = ReminderSettingsPreference.PREFERENCE_KEY
    
    private val recommendKeywords = BehaviorProcessor.create<List<RecommendKeyword>>()

    private val reminderTypes = BehaviorProcessor.create<List<ReminderType>>()

    private val reminderTiming: BehaviorProcessor<ReminderTiming> =
        BehaviorProcessor.createDefault(ReminderTiming.NOT_SET)

    // Note :
    // anonymous inner class should not be passed to registerOnSharedPreferenceChangeListener.
    // Because sharedPreferennce manages change listener with WeakReference,
    // so, it's easy to be involved to GC.
    // https://stackoverflow.com/questions/2542938/sharedpreferences-onsharedpreferencechangelistener-not-being-called-consistently/3104265#3104265
    private val sharedPreferenceChangeListener = object :
        SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onSharedPreferenceChanged(pref: SharedPreferences, key: String) {
            SugorokuonLog.d("Preference is updated - key = $key : $this")
            if (keywordPreferenceKeys.contains(key)) {
                updateRecommendKeywords(pref)
            }
            if (reminderTypePreferenceKeys.map { it.key }.contains(key)) {
                updateReminderTypes(sharedPreferences)
            }
            if (key == notifyTimingPreferenceKey) {
                updateReminderTypes(sharedPreferences)
            }
        }
    }

    init {
        SugorokuonLog.d("Initialize RecommendSettingsRepository : $this (preference : $sharedPreferences)")

        // Set initial value
        updateRecommendKeywords(sharedPreferences)
        updateReminderTypes(sharedPreferences)
        updateReminderTiming(sharedPreferences)

        // Observe sharedPreference to keep recommendKeywords the latest
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
    }

    fun observeRecommendKeywords(): Flowable<List<RecommendKeyword>> = recommendKeywords.hide()

    fun observeReminderTypes(): Flowable<List<ReminderType>> = reminderTypes.hide()

    fun observeReminderTiming(): Flowable<ReminderTiming> = reminderTiming.hide()

    fun getRecommentKeywords(): List<RecommendKeyword> = recommendKeywords.value ?: emptyList()

    fun getReminderTypes(): List<ReminderType> = reminderTypes.value ?: emptyList()

    fun getReminderTiming(): ReminderTiming = reminderTiming.value ?: ReminderTiming.NOT_SET

    private fun updateRecommendKeywords(sharedPreferences: SharedPreferences) {
        val keywords = mutableListOf<RecommendKeyword>().apply {
            keywordPreferenceKeys.forEachIndexed { index, key ->
                add(RecommendKeyword(index, sharedPreferences.getString(key, "")))
            }
        }
        recommendKeywords.onNext(keywords)
    }

    private fun updateReminderTypes(sharedPreferences: SharedPreferences) {
        val types = mutableListOf<ReminderType>().apply {
            reminderTypePreferenceKeys.forEach {
                if (sharedPreferences.getBoolean(it.key, false)) {
                    add(it.value)
                }
            }
        }
        reminderTypes.onNext(types)
    }
    
    private fun updateReminderTiming(sharedPreferences: SharedPreferences) {
        reminderTiming.onNext(
            ReminderTiming.values()[
                sharedPreferences.getInt(
                    ReminderSettingsPreference.PREFERENCE_KEY,
                    ReminderSettingsPreference.defaultValue().ordinal
                )
            ]
        )
    }
}