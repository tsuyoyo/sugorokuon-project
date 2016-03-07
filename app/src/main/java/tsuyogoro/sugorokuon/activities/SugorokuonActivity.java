/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import net.app_c.cloud.sdk.AppCCloud;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import tsuyogoro.sugorokuon.BuildConfig;
import tsuyogoro.sugorokuon.BuildTypeVariables;
import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.fragments.AboutAppFragment;
import tsuyogoro.sugorokuon.fragments.dialogs.HelloV2DialogFragment;
import tsuyogoro.sugorokuon.fragments.dialogs.MessageDialogFragment;
import tsuyogoro.sugorokuon.fragments.dialogs.OhenDialog;
import tsuyogoro.sugorokuon.fragments.onairsongs.WeeklyOnAirSongsFragment;
import tsuyogoro.sugorokuon.fragments.timetable.RecommendFragment;
import tsuyogoro.sugorokuon.fragments.timetable.SearchFragment;
import tsuyogoro.sugorokuon.fragments.timetable.SettingsChangedAlertDialog;
import tsuyogoro.sugorokuon.fragments.timetable.SettingsLauncherDialogFragment;
import tsuyogoro.sugorokuon.fragments.timetable.TimeTableFetchAlertDialog;
import tsuyogoro.sugorokuon.fragments.timetable.TimeTableFetchProgressDialog;
import tsuyogoro.sugorokuon.fragments.timetable.TimeTableFragment;
import tsuyogoro.sugorokuon.models.apis.StationApi;
import tsuyogoro.sugorokuon.models.apis.TimeTableApi;
import tsuyogoro.sugorokuon.models.entities.OnedayTimetable;
import tsuyogoro.sugorokuon.models.entities.Station;
import tsuyogoro.sugorokuon.models.prefs.AreaSettingPreference;
import tsuyogoro.sugorokuon.models.prefs.LaunchedCheckPreference;
import tsuyogoro.sugorokuon.models.prefs.UpdatedDateManager;
import tsuyogoro.sugorokuon.services.OnAirSongsService;
import tsuyogoro.sugorokuon.services.TimeTableService;
import tsuyogoro.sugorokuon.utils.RadikoLauncher;
import tsuyogoro.sugorokuon.utils.SugorokuonLog;
import tsuyogoro.sugorokuon.utils.SugorokuonUtils;

public class SugorokuonActivity extends AppCompatActivity
        implements SettingsChangedAlertDialog.IListener, TimeTableFetchProgressDialog.IListener
        , HelloV2DialogFragment.IHelloV2DialogListener, TimeTableFetchAlertDialog.OnOptionSelectedListener {

    /**
     * TimeTableを明示的に開きたいときはこのActionを送る
     * EXTRA_STATION_IDを使って局を指定することができる
     */
    public static final String ACTION_OPEN_TIMETABLE = "tsuyogoro.sugorokuon.action_open_timetable";

    public static final String EXTRA_STATION_ID = "extra_station_id";

    // DialogFragmentのtag
    private static final String TAG_PROGRESS_DIALOG = "progress_dialog";
    private static final String TAG_SHOULD_LOAD_DIALOG = "should_load_dialog";
    private static final String TAG_WELCOME_DIALOG = "welcome_dialog";
    private static final String TAG_NO_AREA_DIALOG = "no_area_dialog";
    private static final String TAG_HELLO_V2_DIALOG = "hello_v2_dialog";

    private static final String TAG_TIME_TABLE_FRAGMENT = "time_table_fragment";
    private static final String TAG_WEEKLY_ON_AIR_SONGS_FRAGMENT = "weekly_onair_songs_fragment";

    private static final int REQUESTCODE_SETTINGS = 100;

    private DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mDrawerToggle;

    private AppCCloud mAppCCloud;

    private StatusCheckerTask mStatusCheckerTask;

    private static class DataCheckResult {
        private static final int NO_NEED_TO_FETCH = 0;
        private static final int SHOULD_FETCH_STATION = 1;
        private static final int SHOULD_FETCH_WEEKLY_PROGRAM = 2;
        private static final int SHOULD_LAUNCH_SETTINGS = 3;
        private static final int SHOULD_SHOW_WELCOME = 4;
        private static final int SHOULD_SHOW_PROGRESS = 5;
        private static final int SHOULD_SHOW_HELLO_V2 = 6;
    }

    // アプリの状態 (番組表は落とし済みか...etc) をチェックして振る舞いを決めるtask
    private class StatusCheckerTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {

            if (isCancelled()) {
                return DataCheckResult.NO_NEED_TO_FETCH;
            }

            if (null != mTimeTableService && mTimeTableService.runningWeeklyUpdate()
                    && null == getSupportFragmentManager().findFragmentByTag(TAG_PROGRESS_DIALOG)) {
                SugorokuonLog.d("DataCheckResut.SHOULD_SHOW_PROGRESS");
                return DataCheckResult.SHOULD_SHOW_PROGRESS;
            }

            if (!LaunchedCheckPreference.hasLaunched(SugorokuonActivity.this)) {
                return DataCheckResult.SHOULD_SHOW_WELCOME;
            }

            if (!LaunchedCheckPreference.hasV2Launched(SugorokuonActivity.this)) {
                return DataCheckResult.SHOULD_SHOW_HELLO_V2;
            }

            if (0 == AreaSettingPreference.getTargetAreas(SugorokuonActivity.this).length) {
                return DataCheckResult.SHOULD_LAUNCH_SETTINGS;
            }

            StationApi stationApi = new StationApi(SugorokuonActivity.this);
            List<Station> stations = stationApi.load();
            if (0 == stations.size()) {
                return DataCheckResult.SHOULD_FETCH_STATION;
            }

            TimeTableApi timeTableApi = new TimeTableApi(SugorokuonActivity.this);
            Calendar now = Calendar.getInstance();
            int year = now.get(Calendar.YEAR);
            int month = now.get(Calendar.MONTH) + 1;
            int date = now.get(Calendar.DATE);
            if (now.get(Calendar.HOUR_OF_DAY) < 5) {
                date -= 1;
            }

            OnedayTimetable todaysTimeTable = timeTableApi.fetchTimetable(
                    year, month, date, stations.get(0).id);
            if (0 == todaysTimeTable.programs.size()) {
                return DataCheckResult.SHOULD_FETCH_WEEKLY_PROGRAM;
            }

            return DataCheckResult.NO_NEED_TO_FETCH;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            switch (result) {
                case DataCheckResult.SHOULD_SHOW_PROGRESS:
                    showTimeTableFetchProgress();
                    break;
                case DataCheckResult.SHOULD_SHOW_WELCOME:
                    showWelcomDialog();
                    break;
                case DataCheckResult.SHOULD_LAUNCH_SETTINGS:
                    showNoAreaDialog();
                    break;
                case DataCheckResult.SHOULD_FETCH_STATION:
                    showTimeTableFetchAlert(true, false);
                    break;
                case DataCheckResult.SHOULD_FETCH_WEEKLY_PROGRAM:
                    showTimeTableFetchAlert(false, false);
                    break;
                case DataCheckResult.SHOULD_SHOW_HELLO_V2:
                    showHelloV2Dialog();
                    break;
            }

            mStatusCheckerTask = null;
        }
    }

    private TimeTableService.TimeTableServiceBinder mTimeTableService;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mTimeTableService = (TimeTableService.TimeTableServiceBinder) service;
            if (mTimeTableService.runningWeeklyUpdate()) {
                showTimeTableFetchProgress();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity_layout);

        setupDrawer();

        // TODO : ActionBarじゃなくてToolBarにしたい

        // TimeTableServiceとbindして、fetchタスクの状態を見るのに使う
        Intent intent = new Intent(this, TimeTableService.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);

        // appC cloud生成
        mAppCCloud = new AppCCloud(this).start();

        // 初期位置へフォーカスを
        if (null == savedInstanceState || getIntent().getAction().equals(ACTION_OPEN_TIMETABLE)) {
            openTodaysTimeTableFragment();
        }

        // アプリ生存期間内に必要なDangerous permissionをリクエスト
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Set<String> requirePermissionsSet = new HashSet<>();
            for (String p : BuildTypeVariables.PERMISSIONS_GET_AT_LAUNCH_APP) {
                if (checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
                    // 必要なpermissionに対して「今後は表示をしない」が選択されている場合
                    if (shouldShowRequestPermissionRationale(p)) {
                        finish();
                        Toast.makeText(this,
                                getString(R.string.permission_mandatory_never_asked_again),
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        requirePermissionsSet.add(p);
                    }
                }
            }
            if (requirePermissionsSet.size() > 0) {
                String[] requirePermissions = new String[requirePermissionsSet.size()];
                requirePermissionsSet.toArray(requirePermissions);
                requestPermissions(requirePermissions, 100);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean enoughPermissionAllowed = true;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                SugorokuonLog.d("Permission denied : " + permissions[i]);
                enoughPermissionAllowed = false;
            }
        }
        if (!enoughPermissionAllowed) {
            finish();
            Toast.makeText(this, getString(R.string.permission_not_enough_to_launch_app),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void openTodaysTimeTableFragment() {
        Calendar now = Calendar.getInstance();

        int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
        if (5 > now.get(Calendar.HOUR_OF_DAY)) {
            if (Calendar.SUNDAY == dayOfWeek) {
                dayOfWeek = Calendar.SATURDAY;
            } else {
                dayOfWeek -= 1;
            }
        }

        openTimeTableFragment(dayOfWeek, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }

    @Override
    public void finish() {
        super.finish();
        // appC cloud終了処理
        mAppCCloud.finish();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void setupDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_activity_drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, R.string.app_name, R.string.app_name);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // 今週のオススメリストを開く
        findViewById(R.id.drawer_item_recommends).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragments(new RecommendFragment(), true);
                mDrawerLayout.closeDrawers();
            }
        });

        // 番組表を開く
        setupTimeTableList();

        // 設定を起動
        findViewById(R.id.drawer_item_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(SugorokuonActivity.this,
                        SugorokuonSettingActivity.class), REQUESTCODE_SETTINGS);
                mDrawerLayout.closeDrawers();
            }
        });

        // 今週よくかかった曲
        findViewById(R.id.drawer_item_onair_songs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == getSupportFragmentManager().findFragmentByTag(
                        TAG_WEEKLY_ON_AIR_SONGS_FRAGMENT)) {
                    switchFragments(new WeeklyOnAirSongsFragment(), true,
                            TAG_WEEKLY_ON_AIR_SONGS_FRAGMENT);
                }
                mDrawerLayout.closeDrawers();
            }
        });

        // このアプリについて
        findViewById(R.id.drawer_item_about_app).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragments(new AboutAppFragment(), true);
                mDrawerLayout.closeDrawers();
            }
        });

        // このアプリを評価
        findViewById(R.id.drawer_item_rating).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent googlePlayIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.google_play_app_url)));
                googlePlayIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(googlePlayIntent);
                mDrawerLayout.closeDrawers();
            }
        });

        // 作者を応援
        findViewById(R.id.drawer_item_launch_appc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OhenDialog dialog = new OhenDialog();
                dialog.show(getSupportFragmentManager(), "OhenDialog");
                mDrawerLayout.closeDrawers();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void switchFragments(Fragment f, boolean enableReturnByBack, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (null != tag) {
            transaction.replace(R.id.main_activity_drawer_content_layout, f, tag);
        } else {
            transaction.replace(R.id.main_activity_drawer_content_layout, f);
        }

        if (enableReturnByBack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    private void switchFragments(Fragment f, boolean enableReturnByBack) {
        switchFragments(f, enableReturnByBack, null);
    }

    private void setupTimeTableList() {
        setupDrawerTimeTableDay((TextView) findViewById(R.id.drawer_item_time_table_monday),
                Calendar.MONDAY);
        setupDrawerTimeTableDay((TextView) findViewById(R.id.drawer_item_time_table_tuesday),
                Calendar.TUESDAY);
        setupDrawerTimeTableDay((TextView) findViewById(R.id.drawer_item_time_table_wednesday),
                Calendar.WEDNESDAY);
        setupDrawerTimeTableDay((TextView) findViewById(R.id.drawer_item_time_table_thirsday),
                Calendar.THURSDAY);
        setupDrawerTimeTableDay((TextView) findViewById(R.id.drawer_item_time_table_friday),
                Calendar.FRIDAY);
        setupDrawerTimeTableDay((TextView) findViewById(R.id.drawer_item_time_table_saturday),
                Calendar.SATURDAY);
        setupDrawerTimeTableDay((TextView) findViewById(R.id.drawer_item_time_table_sunday),
                Calendar.SUNDAY);
    }

    private void setupDrawerTimeTableDay(TextView textView, final int dayOfWeek) {
        Calendar d = SugorokuonUtils.dayOfThisWeek(dayOfWeek);
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                getString(R.string.date_mmddeee), Locale.JAPANESE);
        textView.setText(dateFormat.format(new Date(d.getTimeInMillis())));

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimeTableFragment(dayOfWeek, true);
                mDrawerLayout.closeDrawers();
            }
        });
    }

    // dayOfWeekは、Calendar#SUNDAY ~ Calendar#SATURDAY
    private void openTimeTableFragment(int dayOfWeek, boolean enableReturnByBackKey) {
        Fragment f = new TimeTableFragment();

        Calendar d = SugorokuonUtils.dayOfThisWeek(dayOfWeek);

        Bundle bundle = new Bundle();
        bundle.putInt(TimeTableFragment.PARAMS_KEY_YEAR, d.get(Calendar.YEAR));
        bundle.putInt(TimeTableFragment.PARAMS_KEY_MONTH, d.get(Calendar.MONTH) + 1);
        bundle.putInt(TimeTableFragment.PARAMS_KEY_DATE, d.get(Calendar.DATE));

        String stationId = getIntent().getStringExtra(EXTRA_STATION_ID);
        if (null != stationId) {
            bundle.putString(TimeTableFragment.PARAMS_KEY_STATION_ID, stationId);
        }

        f.setArguments(bundle);

        switchFragments(f, enableReturnByBackKey);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // メモ : FragmentのonOprionsItemSelectedから呼ばれるようにした
        boolean consumed = false;
        switch (item.getItemId()) {
            case R.id.menu_launch_radiko: {
                RadikoLauncher.launch(this);
                consumed = true;
            }
            break;
            case R.id.menu_launch_search: {
                switchFragments(new SearchFragment(), true);
                consumed = true;
            }
            break;
            case R.id.menu_fetch_latest_program: {
                if (UpdatedDateManager.shouldUpdate(this)) {
                    showTimeTableFetchAlert(false, true);
                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            getString(R.string.date_mm_sl_dd_eehhmm), Locale.JAPANESE);

                    String lastUpdate = dateFormat.format(
                            new Date(UpdatedDateManager.getLastUpdateTime(this)));

                    Bundle params = new Bundle();
                    params.putString(MessageDialogFragment.KEY_MESSAGE,
                            getString(R.string.no_update_message, lastUpdate));
                    params.putString(MessageDialogFragment.KEY_TITLE,
                            getString(R.string.no_update_title));

                    MessageDialogFragment messageDialog = new MessageDialogFragment();
                    messageDialog.setArguments(params);
                    messageDialog.show(getSupportFragmentManager(), "AlreadyLatestTimeTable");
                }
                consumed = true;
            }
            break;
            default:
                break;
        }

        return mDrawerToggle.onOptionsItemSelected(item)
                || super.onOptionsItemSelected(item) || consumed;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 設定値が変わったら自動的にupdateを開始。
        if (REQUESTCODE_SETTINGS == requestCode) {
            switch (resultCode) {
                // Areaが変わったら再度番組表更新。
                case SugorokuonSettingActivity.RESULT_AREA_SETTINGS_UPDATED:
                    SettingsChangedAlertDialog dialog = new SettingsChangedAlertDialog();
                    dialog.show(getSupportFragmentManager(), TAG_SHOULD_LOAD_DIALOG);

                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (null == mStatusCheckerTask) {
            mStatusCheckerTask = new StatusCheckerTask();
            mStatusCheckerTask.execute();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mStatusCheckerTask) {
            mStatusCheckerTask.cancel(true);
            mStatusCheckerTask = null;
        }
    }

    @Override
    public void onSettingsChangeDialogOptionSelected(boolean positive) {
        if (positive) {
            startFetchTimeTable(TimeTableService.ACTION_UPDATE_STATION_AND_TIME_TABLE);
        }
    }

    @Override
    public void onDismissFetchProgress(boolean completed) {
        if (completed) {
            openTodaysTimeTableFragment();
        } else {
            Bundle params = new Bundle();
            params.putString(MessageDialogFragment.KEY_MESSAGE, getString(R.string.date_loading_error_msg));
            params.putString(MessageDialogFragment.KEY_TITLE, getString(R.string.date_loading_error_title));

            MessageDialogFragment errorDialog = new MessageDialogFragment();
            errorDialog.setArguments(params);
            errorDialog.show(getSupportFragmentManager(), "FetchErrorDialog");
        }
    }

    // ProgressDialogがTimeTableServiceからのbroadcastを受け取って、
    // 完了などを通知してくれる仕組みになってる
    private void showTimeTableFetchProgress() {
        if (null == getSupportFragmentManager().findFragmentByTag(TAG_PROGRESS_DIALOG)) {
            TimeTableFetchProgressDialog progressDlg = new TimeTableFetchProgressDialog();
            progressDlg.show(getSupportFragmentManager(), TAG_PROGRESS_DIALOG);
        }
    }

    private void showTimeTableFetchAlert(boolean updateStation, boolean updateToday) {
        Bundle params = new Bundle();
        params.putBoolean(TimeTableFetchAlertDialog.KEY_UPDATE_STATION, updateStation);
        params.putBoolean(TimeTableFetchAlertDialog.KEY_UPDATE_TODAY, updateToday);

        TimeTableFetchAlertDialog dialog = new TimeTableFetchAlertDialog();
        dialog.setArguments(params);
        dialog.show(getSupportFragmentManager(), TAG_SHOULD_LOAD_DIALOG);
    }

    @Override
    public void onTimeTableFetchSelected(
            boolean startUpdate, boolean updateStation, boolean updateToday) {
        if (startUpdate) {
            String action;

            if (updateStation) {
                action = TimeTableService.ACTION_UPDATE_STATION_AND_TIME_TABLE;
            } else if (updateToday) {
                action = TimeTableService.ACTION_UPDATE_TODAYS_TIME_TABLE;
            } else {
                action = TimeTableService.ACTION_UPDATE_WEEKLY_TIME_TABLE;
            }

            if (updateStation) {
                TimeTableFragment.resetCurrentPageIndex(SugorokuonActivity.this);
            }

            startFetchTimeTable(action);
        }
    }

    private void startFetchTimeTable(String action) {
        Intent intent = new Intent(action);
        intent.setPackage(getPackageName());
        startService(intent);

        showTimeTableFetchProgress();
    }

    private void showWelcomDialog() {
        if (null == getSupportFragmentManager().findFragmentByTag(TAG_WELCOME_DIALOG)) {
            SettingsLauncherDialogFragment dialog =
                    SettingsLauncherDialogFragment.getInstance(true);
            dialog.show(getSupportFragmentManager(), TAG_WELCOME_DIALOG);
        }
    }

    private void showHelloV2Dialog() {
        if (null == getSupportFragmentManager().findFragmentByTag(TAG_HELLO_V2_DIALOG)) {
            HelloV2DialogFragment dialog = new HelloV2DialogFragment();
            dialog.show(getSupportFragmentManager(), TAG_HELLO_V2_DIALOG);
        }
    }

    @Override
    public void onStartV2app(boolean positive) {
        if (positive) {
            // 週間番組表の取得と、
            startFetchTimeTable(TimeTableService.ACTION_UPDATE_STATION_AND_TIME_TABLE);

            // OnAir曲の取得
            Intent intent = new Intent(OnAirSongsService.ACTION_FETCH_ON_AIR_SONGS);
            intent.setPackage(getPackageName());
            startService(intent);

            LaunchedCheckPreference.setLaunchedV2(this);
        } else {
            finish();
        }
    }

    private void showNoAreaDialog() {
        if (null == getSupportFragmentManager().findFragmentByTag(TAG_NO_AREA_DIALOG)) {
            SettingsLauncherDialogFragment dialog =
                    SettingsLauncherDialogFragment.getInstance(false);
            dialog.show(getSupportFragmentManager(), TAG_NO_AREA_DIALOG);
        }
    }

    public void onOhenAccepted() {
        mAppCCloud.Ad.callWebActivity();
    }

}