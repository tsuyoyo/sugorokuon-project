/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.settings.preference;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import tsuyogoro.sugorokuon.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.text.InputType;

/**
 * �I�X�X���ԑg�����̃L�[���[�h�Ɋւ���ݒ��preference�B
 * 
 * @author Tsuyoyo
 *
 */
public class RecommendWordPreference {
	
	public static final String PREF_KEY_WORDS = "pref_key_words_slot_%d";
	
	// �������߃��[�h������Slot��
	public static final int SLOT_NUM = 5;
	
	/**
	 * targetScreen�ɃI�X�X���L�[���[�h�ݒ��Preference��ǉ�����B
	 * 
	 * @param targetScreen
	 */
	public static void addPreferenceTo(final PreferenceScreen targetScreen) {
		
		for(int i=0; i<SLOT_NUM; i++) {
			// ����slot��preference key
			final String prefKey = assignValueToString(PREF_KEY_WORDS, i);
			
			// Slot�����
			EditTextPreference editTextPref = 
				new EditTextPreference(targetScreen.getContext()) {
				@Override
				protected void onDialogClosed(boolean positiveResult) {
					super.onDialogClosed(positiveResult);
					if(positiveResult) {
						setTitleAccordingToSettings(
								targetScreen.getContext(), this, prefKey);
					}
				}
			};
			editTextPref.setKey(prefKey);
			
			// Slot�̖��O
			String slotName = assignValueToString(
					targetScreen.getContext().getString(R.string.settings_keyword_slot), i+1);
			editTextPref.setSummary(slotName);
			editTextPref.setDialogTitle(slotName);
			editTextPref.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
			
			// Slot��title��EditText�̐ݒ�
			setTitleAccordingToSettings(targetScreen.getContext(), editTextPref, prefKey);
			
			// Slot������
			targetScreen.addPreference(editTextPref);
		}
	}
	
	/**
	 * Preference�ɕۑ����ꂽ���܂���keyword���擾�B
	 * 
	 * @param context
	 * @return ���܂����L�[���[�h��list�B�o�^����Ă�����̂��Ȃ��ꍇ�͋��list�B
	 */
	public static List<String> getKeyWord(Context context) {
		List<String> keyWords = new ArrayList<String>();		
		SharedPreferences defPref = PreferenceManager.getDefaultSharedPreferences(context);
		for(int i=0; i<SLOT_NUM; i++) {
			String key = getKey(i);
			String word = defPref.getString(key, "");
			if(!word.equals("")) {
				keyWords.add(word);
			}
		}
		return keyWords;
	}
	
	private static void setTitleAccordingToSettings(Context context,
			EditTextPreference editTextPref, String prefKey) {
		String storedText = 
			PreferenceManager.getDefaultSharedPreferences(context)
			.getString(prefKey, "");
		editTextPref.setText(storedText);
		
		// �L�[���[�h�������Ă��Ȃ���΁u���o�^�v��\���B
		if(0 == storedText.length()) {
			editTextPref.setTitle(
					context.getString(R.string.settings_keyword_unset));
		} else {
			editTextPref.setTitle(storedText);				
		}
		
	}

	public static String getKey(int value) {
		return assignValueToString(PREF_KEY_WORDS, value);
	}
	
	private static String assignValueToString(String str, int value) {
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb, Locale.US);
		formatter.format(str, value);
		formatter.close();
		return sb.toString();	
	}
	
}
