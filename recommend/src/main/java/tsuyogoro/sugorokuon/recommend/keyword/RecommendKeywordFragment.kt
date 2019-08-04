/**
 * Copyright (c)
 * 2018 tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.recommend.keyword

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.content.res.ResourcesCompat
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import android.view.View
import tsuyogoro.sugorokuon.recommend.R

class RecommendKeywordFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        // Note :
        // Let's use this static method to get this fragment instead of calling constructor.
        // To call constructor, the module needs to depend on support preference lib.
        fun createInstance(): androidx.fragment.app.Fragment = RecommendKeywordFragment()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.keyword_settings, rootKey)
        setupViews()
        PreferenceManager.getDefaultSharedPreferences(context)
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(ResourcesCompat.getColor(resources, android.R.color.white, null))
    }

    override fun onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(context)
            .unregisterOnSharedPreferenceChangeListener(this)
        super.onDestroy()
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
        PreferenceManager.getDefaultSharedPreferences(context)
            .getString(preferenceKey, "")

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

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences,
                                           key: String) {
        context?.let {
            if (RecommendKeywordPreferenceKeys.getAll(it).contains(key)) {
                updateEditTextPreferenceTitle(sharedPreferences, key)
            }
        }
    }
}