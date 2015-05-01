/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.fragments.settings.SettingsListFragment;
import tsuyogoro.sugorokuon.models.prefs.AreaSettingPreference;
import tsuyogoro.sugorokuon.models.prefs.AutoUpdateSettingPreference;
import tsuyogoro.sugorokuon.models.prefs.RecommendWordPreference;
import tsuyogoro.sugorokuon.models.prefs.RemindBehaviorPreference;
import tsuyogoro.sugorokuon.services.OnAirSongsService;
import tsuyogoro.sugorokuon.services.TimeTableService;

/**
 * 設定画面のActivity。
 */
public class SugorokuonSettingActivity extends Activity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int MENU_HELP_MANUAL = 0;

    private static final String TAG_HELP_DIALOG = "help_dialog";

    public static final int RESULT_SETTINGS_NO_UPDATE = 1;
    public static final int RESULT_AREA_SETTINGS_UPDATED = 2;
    public static final int RESULT_KEYWORD_UPDATED = 3;
    public static final int RESULT_REMINDER_UPDATED = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setResult(RESULT_SETTINGS_NO_UPDATE);

        setContentView(R.layout.settings_layout);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // 設定値変更の通知を受け取るためにregister
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        if (null == savedInstanceState) {
            ListFragment options = new SettingsListFragment();

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.setting_fragment_container, options, "SettingsOptions");
            transaction.commit();
        }
    }


//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        menu.add(Menu.NONE, MENU_HELP_MANUAL, 0, getString(R.string.settings_menu_help))
//                .setIcon(R.drawable.ic_help_white_24dp)
//                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//
//        return super.onCreateOptionsMenu(menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
//            case MENU_HELP_MANUAL:
//                HelpDialogFragment dialog = new HelpDialogFragment();
//                dialog.show(getFragmentManager(), TAG_HELP_DIALOG);
//                break;
        }
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        // Area設定が変わった。
        if(key.contains(AreaSettingPreference.PREF_KEY_AREAS_PREFIX)
                || key.contains(AreaSettingPreference.PREF_KEY_REGIONS_PREFIX)) {
            setResult(RESULT_AREA_SETTINGS_UPDATED);
        }
        // キーワードが変わった。
        else if(key.contains(RecommendWordPreference.PREF_KEY_WORDS_PREFIX)) {

            Intent intent = new Intent(TimeTableService.ACTION_UPDATE_RECOMMENDS);
            intent.setPackage(getPackageName());
            startService(intent);
        }
        // Remind設定、もしくは、自動更新設定が変わった
        else if(key.contains(RemindBehaviorPreference.PREF_KEY_REMIND_PREFIX)
                || key.contains(AutoUpdateSettingPreference.PREF_KEY_AUTO_UPDATE_PREFIX)) {

            Intent intent = new Intent(TimeTableService.ACTION_UPDATE_TIMER);
            intent.setPackage(getPackageName());
            startService(intent);

            Intent songInfoFetchIntent = new Intent(OnAirSongsService.ACTION_SET_ON_AIR_SONGS_TIMER);
            songInfoFetchIntent.setPackage(getPackageName());
            startService(songInfoFetchIntent);
        }
    }

//    public static class HelpDialogFragment extends DialogFragment {
//
//        public HelpDialogFragment() {
//            super();
//        }
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            builder.setTitle(R.string.settings_manual_title);
//            builder.setMessage(R.string.settings_manual);
//            builder.setPositiveButton(getString(R.string.ok), null);
//            builder.setCancelable(true);
//            return builder.create();
//        }
//    }

}

