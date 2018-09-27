/**
 * Copyright (c)
 * 2018 tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.recommend.keyword

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.preference.EditTextPreference
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.View
import tsuyogoro.sugorokuon.recommend.R

class RecommendKeywordFragment : PreferenceFragmentCompat() {

    companion object {
        // Note :
        // Let's use this static method to get this fragment instead of calling constructor.
        // To call constructor, the module needs to depend on support preference lib.
        fun createInstance(): Fragment = RecommendKeywordFragment()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.keyword_settings, rootKey)
        setupViews()
        registerPreferenceChangeListener()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(ResourcesCompat.getColor(resources, android.R.color.white, null))
    }

    private fun setupViews() {
        context?.let {
            RecommendKeywordPreferenceKeys
                .getAll(it)
                .forEachIndexed(this::setupEditTextPreference)
        }
    }

    private fun setupEditTextPreference(index: Int, preferenceKey: String) {
        getEditTextPreference(preferenceKey)?.let {
            it.summary = getSlotName(index)
            it.dialogTitle = getSlotName(index)
            it.title = getCurrentValue(preferenceKey).let(this::getSlotTitle)
        }
    }

    private fun getEditTextPreference(preferenceKey: String) =
        preferenceScreen.findPreference(preferenceKey) as? EditTextPreference

    private fun getSlotName(index: Int) = String.format(
        resources.getString(R.string.settings_keyword_slot), index + 1)

    private fun getCurrentValue(preferenceKey: String) =
        preferenceManager.sharedPreferences.getString(preferenceKey, "")

    private fun updateEditTextPreferenceTitle(sharedPreferences: SharedPreferences,
                                              preferenceKey: String) {
        getEditTextPreference(preferenceKey)?.title = getSlotTitle(
            sharedPreferences.getString(preferenceKey, ""))
    }

    private fun getSlotTitle(keyword: String) = if (keyword.isEmpty()) {
        resources.getString(R.string.settings_keyword_unset)
    } else {
        keyword
    }

    private fun registerPreferenceChangeListener() {
        preferenceManager.sharedPreferences
            .registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
                context?.let {
                    if (RecommendKeywordPreferenceKeys.getAll(it).contains(key)) {
                        updateEditTextPreferenceTitle(sharedPreferences, key)
                    }
                }
            }
    }
}