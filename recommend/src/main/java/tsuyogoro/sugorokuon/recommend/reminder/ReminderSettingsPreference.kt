package tsuyogoro.sugorokuon.recommend.reminder

import android.content.Context
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceManager
import android.support.v7.preference.PreferenceViewHolder
import android.util.AttributeSet
import android.widget.RadioButton
import tsuyogoro.sugorokuon.recommend.R

class ReminderSettingsPreference : Preference {

    companion object {
        const val PREFERENCE_KEY = "reminder_settings"
        fun defaultValue() = ReminderTiming.BEFORE_10_MIN
    }

    private val checkBoxViewIds = arrayListOf(
        R.id.no_reminder,
        R.id.before_10_minutes,
        R.id.before_30_minutes,
        R.id.before_1_hour,
        R.id.before_2_hours,
        R.id.before_5_hours
    )

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attributeSet: AttributeSet?): super(context, attributeSet) {
        key = PREFERENCE_KEY
        layoutResource = R.layout.preference_reminder_timing_radiobox
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)

        val currentSettings = PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(PREFERENCE_KEY, defaultValue().ordinal)

        checkBoxViewIds.forEachIndexed { index, id ->
            (holder?.itemView?.findViewById(id) as? RadioButton)
                ?.let {
                    it.isChecked = (currentSettings == index)
                    it.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            updatePreference(index)
                        }
                    }
                }
        }
    }

    private fun updatePreference(index: Int) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
            .putInt(PREFERENCE_KEY, index)
            .apply()
    }
}