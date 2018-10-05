package tsuyogoro.sugorokuon.recommend.reminder

import android.content.Context
import android.support.v7.preference.Preference
import android.util.AttributeSet
import android.widget.RadioButton
import android.widget.RadioGroup
import tsuyogoro.sugorokuon.recommend.R


// これ参考にしてradioボタンを実装するか
// http://tech.chitgoks.com/2015/06/12/android-checkboxpreference-as-radiobutton/
class ReminderTimingRadioBoxPreference : Preference {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        widgetLayoutResource = R.layout.preference_raminder_timing_radiobox
    }


//    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): this(context, attrs, defStyleAttr) {
////        widgetLayoutResource = R.layout.preference_theme
//    }

    companion object {
        const val PREFERENCE_KEY = "reminder_timing"
    }


    //    fun onCreateView(parent: ViewGroup): View = createOptions()

    private fun createOptions(): RadioGroup {
        val sharedPreferences = preferenceManager.sharedPreferences
        val currentSettings = sharedPreferences.getInt(PREFERENCE_KEY, 1)

        val radioGroup = RadioGroup(context)
        NotifyTiming.values().forEach {
            radioGroup.addView(
                RadioButton(context).apply {
                    text = context.getString(it.optionStrId)
                    id = it.ordinal
                    isChecked = (id == currentSettings)
                }
            )
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            preferenceManager.sharedPreferences
                .edit()
                .putInt(PREFERENCE_KEY, checkedId)
                .apply()
        }

        return radioGroup
    }
}

//    private RadioGroup createOptions() {
//        // これまでの設定を見て、focusを当てる
//        SharedPreferences pref = getPreferenceManager().getSharedPreferences();
//        int currentSettings = pref.getInt(PREF_KEY_REMIND_TIME, 1); // 1は「10分前」。
//
//        RadioGroup options = new RadioGroup(getContext());
//        for(NotifyTiming time : NotifyTiming.values()) {
//            RadioButton radioBtn = new RadioButton(getContext());
//            radioBtn.setText(getContext().getText(time.optionStrId));
//            radioBtn.setId(time.ordinal());
//            if(time.ordinal() == currentSettings) {
//                radioBtn.setChecked(true);
//            }
//            options.addView(radioBtn);
//        }
//
//        // checkされたら設定値を変更。
//        options.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                SharedPreferences pref = getPreferenceManager().getSharedPreferences();
//                pref.edit().putInt(PREF_KEY_REMIND_TIME, checkedId).commit();
//            }
//        });
//
//        return options;
//    }