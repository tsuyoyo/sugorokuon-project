/**
 * Copyright (c)
 * 2018 tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.recommend.reminder

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.preference.CheckBoxPreference
import android.support.v7.preference.PreferenceCategory
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.View
import tsuyogoro.sugorokuon.base.R

class ReminderSettingsFragment : PreferenceFragmentCompat() {

    companion object {
        // Note :
        // Let's use this static method to get this fragment instead of calling constructor.
        // To call constructor, the module needs to depend on support preference lib.
        fun createInstance(): Fragment = ReminderSettingsFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(ResourcesCompat.getColor(resources, android.R.color.white, null))
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(tsuyogoro.sugorokuon.base.R.xml.reminder_settings, rootKey)

        preferenceScreen.addPreference(
            PreferenceCategory(context!!).apply {
                title = getString(R.string.settings_remind_behavior_title)
            }
        )
        preferenceScreen.addPreference(ReminderSettingsPreference(context!!))

        preferenceScreen.addPreference(
            PreferenceCategory(context!!).apply {
                title = getString(R.string.settings_remind_behavior_title)
            }
        )
        preferenceScreen.addPreference(
            CheckBoxPreference(context!!).apply {
                key = getString(R.string.pref_key_reminder_light)
                title = getString(R.string.settings_remind_behavior_light)
            })
        preferenceScreen.addPreference(
            CheckBoxPreference(context!!).apply {
                key = getString(R.string.pref_key_reminder_sound)
                title = getString(R.string.settings_remind_behavior_sound)
            }
        )
        preferenceScreen.addPreference(
            CheckBoxPreference(context!!).apply {
                key = getString(R.string.pref_key_reminder_vibration)
                title = getString(R.string.settings_remind_behavior_vibration)
            }
        )

    }
}