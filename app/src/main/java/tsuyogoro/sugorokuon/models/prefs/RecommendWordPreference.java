/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.models.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.text.InputType;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import tsuyogoro.sugorokuon.R;

/**
 * オススメ番組検索のキーワードに関する設定のpreference。
 *
 */
public class RecommendWordPreference {

    public static final String PREF_KEY_WORDS_PREFIX = "pref_key_words_slot";

    private static final String PREF_KEY_WORDS = PREF_KEY_WORDS_PREFIX + "_%d";

    // おすすめワードを入れるSlot数
    public static final int SLOT_NUM = 5;

    /**
     * targetScreenにオススメキーワード設定のPreferenceを追加する。
     *
     * @param targetScreen
     */
    public static void addPreferenceTo(final PreferenceScreen targetScreen) {

        for(int i=0; i<SLOT_NUM; i++) {
            // このslotのpreference key
            final String prefKey = assignValueToString(PREF_KEY_WORDS, i);

            // Slotを作る
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

            // Slotの名前
            String slotName = assignValueToString(
                    targetScreen.getContext().getString(R.string.settings_keyword_slot), i+1);
            editTextPref.setSummary(slotName);
            editTextPref.setDialogTitle(slotName);
            editTextPref.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);

            // SlotのtitleとEditTextの設定
            setTitleAccordingToSettings(targetScreen.getContext(), editTextPref, prefKey);

            // Slotを入れる
            targetScreen.addPreference(editTextPref);
        }
    }

    /**
     * Preferenceに保存されたおまかせkeywordを取得。
     *
     * @param context
     * @return おまかせキーワードのlist。登録されているものがない場合は空のlist。
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

        // キーワードが入っていなければ「未登録」を表示。
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