package tsuyogoro.sugorokuon.models.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.List;

import tsuyogoro.sugorokuon.R;

public class NhkAreaSettingsPreference {

    public static final String PREF_KEY_NHK_AREA = "pref_key_nhk_area";

    private static final String DEFAULT_AREA = "130"; // 東京

    public static class NhkArea {
        public String code;
        public String displayName;
    }

    private static class RadioBoxPreference extends Preference {

        final List<NhkArea> mNhkAreaList;

        public RadioBoxPreference(Context context, List<NhkArea> nhkAreaList) {
            super(context);
            mNhkAreaList = nhkAreaList;
        }

        @Override
        protected View onCreateView(ViewGroup parent) {
            super.onCreateView(parent);
            return createOptions();
        }

        private RadioGroup createOptions() {
            // これまでの設定を見て、focusを当てる
            SharedPreferences pref = getPreferenceManager().getSharedPreferences();
            String currentSettings = pref.getString(PREF_KEY_NHK_AREA, DEFAULT_AREA);

            RadioGroup options = new RadioGroup(getContext());
            for (int i = 0; i < mNhkAreaList.size(); i++) {
                RadioButton radioBtn = new RadioButton(getContext());
                radioBtn.setText(mNhkAreaList.get(i).displayName);
                radioBtn.setId(i);

                if (currentSettings.equals(mNhkAreaList.get(i).code)) {
                    radioBtn.setChecked(true);
                }

                options.addView(radioBtn);
            }

            // checkされたら設定値を変更。
            options.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    SharedPreferences pref = getPreferenceManager().getSharedPreferences();

                    String selectedAreaCode = mNhkAreaList.get(checkedId).code;
                    pref.edit().putString(PREF_KEY_NHK_AREA, selectedAreaCode).apply();
                }
            });

            return options;
        }
    }

    public static void addPreferenceTo(final PreferenceScreen targetScreen,
                                       List<NhkArea> nhkAreaList) {
        Context context = targetScreen.getContext();

        PreferenceCategory category = new PreferenceCategory(context);
        category.setTitle(context.getString(R.string.settings_nhk_area_title));

        RadioBoxPreference preference = new RadioBoxPreference(context, nhkAreaList);

        targetScreen.addPreference(category);
        targetScreen.addPreference(preference);
    }

    /**
     * 設定されたNHKのAreaコードをpreferenceから取得。
     * ServerのAPIを叩く時に利用する想定。
     *
     * @param context
     * @return 必ず値が返る (設定値が無ければdefault値が返る)
     */
    public static String getNhkAreaCode(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_KEY_NHK_AREA, DEFAULT_AREA);
    }

}
