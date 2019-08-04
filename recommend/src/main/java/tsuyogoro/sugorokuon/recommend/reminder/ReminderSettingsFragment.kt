/**
 * Copyright (c)
 * 2018 tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.recommend.reminder

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.content.res.ResourcesCompat
import androidx.preference.PreferenceFragmentCompat
import android.view.View
import tsuyogoro.sugorokuon.recommend.R

class ReminderSettingsFragment : PreferenceFragmentCompat() {

    companion object {
        // Note :
        // Let's use this static method to get this fragment instead of calling constructor.
        // To call constructor, the module needs to depend on support preference lib.
        fun createInstance(): androidx.fragment.app.Fragment = ReminderSettingsFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(ResourcesCompat.getColor(resources, android.R.color.white, null))
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.reminder_settings, rootKey)
    }
}