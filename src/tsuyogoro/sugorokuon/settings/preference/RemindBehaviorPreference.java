/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.settings.preference;

import tsuyogoro.sugorokuon.R;
import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

public class RemindBehaviorPreference {
	
	public static final String PREF_KEY_REMIND_LIGHT = "pref_key_remind_light";
	public static final String PREF_KEY_REMIND_SOUND = "pref_key_remind_sound";
	public static final String PREF_KEY_REMIND_VIBRATE = "pref_key_remind_vibrate";

	/**
	 * Notificationを出す際の動きの設定（LED、SOUND、VIBRATION）Preferenceを
	 * targetScreenに追加する。
	 * 
	 * @param targetScreen
	 */
	public static void addPreferenceTo(PreferenceScreen targetScreen) {
		Context context = targetScreen.getContext();
		
		// Preferenceカテゴリ
		PreferenceCategory category = new PreferenceCategory(context);
		category.setTitle(context.getString(R.string.settings_remind_behavior_settings));
		
		// LED光らせる？
		CheckBoxPreference lightSetting = new CheckBoxPreference(context);
		lightSetting.setTitle(
				context.getString(R.string.settings_remind_behavior_light));
		lightSetting.setKey(PREF_KEY_REMIND_LIGHT);
		
		// 音ならす？
		CheckBoxPreference soundSetting = new CheckBoxPreference(context);
		soundSetting.setTitle(
				context.getString(R.string.settings_remind_behavior_sound));
		soundSetting.setKey(PREF_KEY_REMIND_SOUND);

		// バイブレーションさせる？
		CheckBoxPreference vibrateSetting = new CheckBoxPreference(context);
		vibrateSetting.setTitle(
				context.getString(R.string.settings_remind_behavior_vibration));
		vibrateSetting.setKey(PREF_KEY_REMIND_VIBRATE);
		
		targetScreen.addPreference(category);
		targetScreen.addPreference(lightSetting);
		targetScreen.addPreference(soundSetting);
		targetScreen.addPreference(vibrateSetting);
	}
	
	/**
	 * どうやって通知するか（LEDつけるとか音鳴らすとかバイブレーションされるとか）の設定値を取得。
	 * 
	 * {@link Notification#DEFAULT_LIGHTS}、{@link　Notification.DEFAULT_SOUND}、
	 * {@link Notification.DEFAULT_VIBRATE}のORを取って返す。
	 * 
	 * @param context
	 * @return 
	 */
	public static int wayToNotify(Context context) {
		SharedPreferences defPref = PreferenceManager.getDefaultSharedPreferences(context);
		
		int light = defPref.getBoolean(PREF_KEY_REMIND_LIGHT, false) ?
				Notification.DEFAULT_LIGHTS : 0;
		int sound = defPref.getBoolean(PREF_KEY_REMIND_SOUND, false) ?
				Notification.DEFAULT_SOUND : 0;
		int vibration = defPref.getBoolean(PREF_KEY_REMIND_VIBRATE, false) ?
				Notification.DEFAULT_VIBRATE : 0;
		
		return light | sound | vibration;
	}
	
}
