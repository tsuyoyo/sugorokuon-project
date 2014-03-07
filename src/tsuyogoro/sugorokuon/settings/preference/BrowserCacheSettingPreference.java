/**
 * Copyright (c) 
 * 2013 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.settings.preference;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.util.SugorokuonUtils;
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

public class BrowserCacheSettingPreference {
	
	public static final String PREF_BROWSER_CACHE_SETTINGS = "browser_cache_settings_key";
	
	public static final int CACHE_ENABLE = 0;
	public static final int CACHE_DISABLE = 1;	
	
	/**
	 * targetScreen�ɁA�L���b�V���ݒ�Preference��ǉ�����B
	 * 
	 * @param targetScreen
	 */
	public static void addPreferenceTo(PreferenceScreen targetScreen) {
		Context context = targetScreen.getContext();

		PreferenceCategory category = new PreferenceCategory(context);
		category.setTitle(context.getString(R.string.settings_browser_cache_settings_title));
		
		RadioBoxPreference preference = new RadioBoxPreference(context);
		
		targetScreen.addPreference(category);
		targetScreen.addPreference(preference);
	}
	
	/**
	 * Browser�̈��cache��L���ɂ��邩�ǂ����̐ݒ�B
	 * 
	 * @param context
	 * @return CACHE_ENABLE��CACHE_DISABLE�̂ǂ��炩�B
	 */
	public static boolean isCacheEnabled(Context context) {
		SharedPreferences defPref = PreferenceManager.getDefaultSharedPreferences(context);
		int val = defPref.getInt(PREF_BROWSER_CACHE_SETTINGS, -1);
		if(-1 == val) {
			val = getDefaultValue();
		}
		
		if(CACHE_ENABLE == val) {
			return true;
		} else {
			return false;
		}
	}
	
	// GingerBread�ł́A�����l��cache�����B
	private static int getDefaultValue() {
		if(SugorokuonUtils.isHigherThanGingerBread()) {
			return CACHE_ENABLE;
		} else {
			return CACHE_DISABLE;
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
			int currentSettings = isCacheEnabled(getContext()) ?
									CACHE_ENABLE : CACHE_DISABLE;
			
			RadioGroup options = new RadioGroup(getContext());
			
			// Cache�L���̃{�^���B
			RadioButton radioBtnEnable = new RadioButton(getContext());
			radioBtnEnable.setText(getContext().getText(
					R.string.settings_browser_cache_settings_enable));
			radioBtnEnable.setId(CACHE_ENABLE);
			if(CACHE_ENABLE == currentSettings) {
				radioBtnEnable.setChecked(true);
			}
			options.addView(radioBtnEnable);
			
			// Cache�����̃{�^���B
			RadioButton radioBtnDisable = new RadioButton(getContext());
			radioBtnDisable.setText(getContext().getText(
					R.string.settings_browser_cache_settings_disable));
			radioBtnDisable.setId(CACHE_DISABLE);
			if(CACHE_DISABLE == currentSettings) {
				radioBtnDisable.setChecked(true);
			}
			options.addView(radioBtnDisable);			
			
			// check���ꂽ��ݒ�l��ύX�B
			options.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					SharedPreferences pref = getPreferenceManager().getSharedPreferences();
					pref.edit().putInt(PREF_BROWSER_CACHE_SETTINGS, checkedId).commit();					
				}
			});
			
			return options;
		}
	}
}
