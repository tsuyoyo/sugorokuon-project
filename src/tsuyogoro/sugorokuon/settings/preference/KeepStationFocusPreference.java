/**
 * Copyright (c) 
 * 2013 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.settings.preference;

import tsuyogoro.sugorokuon.R;
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
import android.widget.RadioGroup.OnCheckedChangeListener;

public class KeepStationFocusPreference {
	
	public static final String PREF_KEY_KEEP_STATION_FOCUS = "keep_station_focus_target_key";

	public static final int NOT_KEEP_FOCUS = 0;
	public static final int KEEP_FOCUS = 1;
	
	/**
	 * targetScreen�ɁAStation�t�H�[�J�X��keep���邩�ǂ����̐ݒ��ǉ��B
	 * 
	 * @param targetScreen
	 */
	public static void addPreferenceTo(PreferenceScreen targetScreen) {
		Context context = targetScreen.getContext();

		PreferenceCategory category = new PreferenceCategory(context);
		category.setTitle(context.getString(R.string.settings_keep_station_focus_settings_title));
		
		RadioBoxPreference preference = new RadioBoxPreference(context);		
		targetScreen.addPreference(category);
		targetScreen.addPreference(preference);
	}
	
	/**
	 * �Ō�Ɍ����ǂ̃t�H�[�J�X���L�����邩�ǂ����̐ݒ��Ԃ��B
	 * 
	 * @param context
	 * @return 
	 */
	public static boolean getKeepStationFocusSettings(Context context) {
		SharedPreferences defPref = PreferenceManager.getDefaultSharedPreferences(context);
		int settings = defPref.getInt(PREF_KEY_KEEP_STATION_FOCUS, KEEP_FOCUS);
		if(KEEP_FOCUS == settings) {
			return true;
		} else {
			return false;
		}
	}
	
	private static class RadioBoxPreference extends Preference {

		public RadioBoxPreference(Context context) {
			super(context);
		}
		
		@Override
		protected View onCreateView(ViewGroup parent) {
			super.onCreateView(parent);
			return createOptions();
		}
				
		private RadioGroup createOptions() {
			// ����܂ł̐ݒ�����āAfocus�𓖂Ă�
			SharedPreferences pref = getPreferenceManager().getSharedPreferences();
			
			// default�� "Keep" �̐ݒ�B
			int currentSettings = pref.getInt(PREF_KEY_KEEP_STATION_FOCUS, KEEP_FOCUS);
			
			RadioGroup options = new RadioGroup(getContext());
			
			// �uKeep����v�{�^��
			options.addView(createRadioBtn(R.string.settings_keep_station_focus_keep,
					KEEP_FOCUS, currentSettings));
			
			// �uKeep���Ȃ��v�{�^��
			options.addView(createRadioBtn(R.string.settings_keep_station_focus_notkeep,
					NOT_KEEP_FOCUS, currentSettings));
			
			// check���ꂽ��ݒ�l��ύX�B
			options.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					SharedPreferences pref = getPreferenceManager().getSharedPreferences();
					pref.edit().putInt(PREF_KEY_KEEP_STATION_FOCUS, checkedId).commit();					
				}
			});
			
			return options;
		}
		
		private RadioButton createRadioBtn(int textId, int id, int currentSettings) {
			RadioButton radioBtn = new RadioButton(getContext());
			radioBtn.setText(getContext().getText(textId));
			radioBtn.setId(id);
			if(id == currentSettings) {
				radioBtn.setChecked(true);
			}
			return radioBtn;
		}
	}
}
