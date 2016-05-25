/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;
import tsuyogoro.sugorokuon.BuildTypeVariables;
import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.SugorokuonApplication;
import tsuyogoro.sugorokuon.fragments.dialogs.HelloV2DialogFragment;
import tsuyogoro.sugorokuon.fragments.dialogs.MessageDialogFragment;
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
import tsuyogoro.sugorokuon.services.TimeTableService;
import tsuyogoro.sugorokuon.utils.RadikoLauncher;
import tsuyogoro.sugorokuon.utils.SugorokuonLog;
import tsuyogoro.sugorokuon.utils.SugorokuonUtils;

public class SugorokuonActivity extends DrawableActivity
        implements SettingsChangedAlertDialog.IListener, TimeTableFetchProgressDialog.IListener,
        HelloV2DialogFragment.IHelloV2DialogListener, TimeTableFetchAlertDialog.OnOptionSelectedListener {
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
    private static final String TAG_HELLO_V2_2_DIALOG = "hello_v2_2_dialog";

    private static final String TAG_TIME_TABLE_FRAGMENT = "time_table_fragment";
    private static final String TAG_WEEKLY_ON_AIR_SONGS_FRAGMENT = "weekly_onair_songs_fragment";

    private static final int REQUESTCODE_SETTINGS = 100;

    private StatusCheckerTask mStatusCheckerTask;

    private static class DataCheckResult {
        private static final int NO_NEED_TO_FETCH = 0;
        private static final int SHOULD_FETCH_STATION = 1;
        private static final int SHOULD_FETCH_WEEKLY_PROGRAM = 2;
        private static final int SHOULD_LAUNCH_SETTINGS = 3;
        private static final int SHOULD_SHOW_WELCOME = 4;
        private static final int SHOULD_SHOW_PROGRESS = 5;
//        private static final int SHOULD_SHOW_HELLO_V2 = 6;
//        private static final int SHOULD_SHOW_HELLO_V2_2 = 7;
        private static final int SHOULD_SHOW_HELLO_V2_3 = 8;
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

            if (!LaunchedCheckPreference.hasV23Launched(SugorokuonActivity.this)) {
                return DataCheckResult.SHOULD_SHOW_HELLO_V2_3;
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
                case DataCheckResult.SHOULD_SHOW_HELLO_V2_3:
                    showHelloV2_3Dialog();
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

        // Main Activityのコンテンツ
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(R.layout.main_activity_content_layout, getContentRoot(), true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        if (toolbar != null) {
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
        }
        setupStationListBottomSheet();
        setupFloatingActionButton();
        setupDrawer(true);
        setupSwitchDateTabs();

        // TimeTableServiceとbindして、fetchタスクの状態を見るのに使う
        Intent intent = new Intent(this, TimeTableService.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);

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
                    } else {
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

    private void setupFloatingActionButton() {

        FabSpeedDial fabSpeedDial = (FabSpeedDial) findViewById(R.id.main_activity_fab_speed_dial);

        if (fabSpeedDial == null) {
            return;
        }

        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                boolean consumed;
                switch (menuItem.getItemId()) {
                    case R.id.fab_speed_dial_menu_onair_songs:
                        startOnAirSongsActivity();
                        consumed = true;
                        break;
                    case R.id.fab_speed_dial_menu_recommend:
                        startRecommendActivity();
                        consumed = true;
                        break;
                    default:
                        consumed = super.onMenuItemSelected(menuItem);
                        break;
                }
                return consumed;
            }
        });

    }

    private ViewPager mViewPager;

    private StationListAdapter.IStationListListener mStationListListener =
            new StationListAdapter.IStationListListener() {
                @Override
                public void onStationSelected(Station station) {
                    int currentFocus = mViewPager.getCurrentItem();

                    TimeTableFragment timeTableFragment =
                            mDateTabAdapter.getRegisteredFragment(currentFocus);

                    timeTableFragment.setFocus(station);
                }

                @Override
                public void onStationLongTapped(Station station) {
                    if (station.siteUrl != null) {
                        SugorokuonUtils.launchChromeTab(
                                SugorokuonActivity.this, Uri.parse(station.siteUrl));
                    }
                }
            };

    private void setupStationListBottomSheet() {
        View titleBar = findViewById(R.id.main_actiity_stationlist_title);
        if (titleBar != null) {
            titleBar.setOnClickListener(v -> {
                View bottomSheet = findViewById(R.id.main_activity_stationlist_bottom_sheet);
                BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);

                TextView titleText = (TextView) findViewById(
                        R.id.main_actiity_stationlist_title_text);

                if (titleText != null) {
                    // 初期状態の設定で表示されている (behavior_peekHeightで設定した高さ)
                    if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        titleText.setText(getString(R.string.station_list_dialog_description));
                    }
                    // 一番大きくなっている状態 (layout_heightの高さで表示されている)
                    else if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        titleText.setText(getString(R.string.station_list_dialog_title));
                    }
                }

                SugorokuonApplication.firebaseAnalytics.logEvent("OpenStationList", null);
            });
            AsyncTask<Void, Void, List<Station>> stationLoaderTask =
                    new AsyncTask<Void, Void, List<Station>>() {
                        @Override
                        protected List<Station> doInBackground(Void... params) {
                            return (new StationApi(SugorokuonActivity.this)).load();
                        }

                        @Override
                        protected void onPostExecute(List<Station> stations) {
                            super.onPostExecute(stations);
                            RecyclerView stationList =
                                    (RecyclerView) findViewById(R.id.main_activity_station_list);
                            if (stationList != null) {
                                stationList.setLayoutManager(new LinearLayoutManager(
                                        SugorokuonActivity.this, LinearLayoutManager.HORIZONTAL, false));

                                StationListAdapter adapter = new StationListAdapter(
                                        stations, mStationListListener);

                                stationList.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                            }
                        }
                    };
            stationLoaderTask.execute();
        }
    }

    private final int[] mOrderedDateInWeek = new int[]{
            Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
            Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY
    };

    private DatePagerAdapter mDateTabAdapter;

    private class DatePagerAdapter extends FragmentPagerAdapter {

        public DatePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = getFragmentByDayOfWeek(mOrderedDateInWeek[position]);
            return fragment;
        }

        SparseArray<TimeTableFragment> registeredFragments = new SparseArray<>();

        public TimeTableFragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }

        // Configuration changeでFragmentが作り変えられても、Adapter経由でPagerの中のFragmentに
        // アクセスするには、作ったFragmentを手の届く所に対比させないといけない (-> registeredFragments)
        // http://stackoverflow.com/questions/8785221/retrieve-a-fragment-from-a-viewpager
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TimeTableFragment fragment = (TimeTableFragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public int getCount() {
            return mOrderedDateInWeek.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.date_tab_switcher_monday);
                case 1:
                    return getString(R.string.date_tab_switcher_tuesday);
                case 2:
                    return getString(R.string.date_tab_switcher_wednesday);
                case 3:
                    return getString(R.string.date_tab_switcher_thursday);
                case 4:
                    return getString(R.string.date_tab_switcher_friday);
                case 5:
                    return getString(R.string.date_tab_switcher_saturday);
                case 6:
                    return getString(R.string.date_tab_switcher_sunday);
                default:
                    return "";
            }
        }
    }

    ;

    private void setupSwitchDateTabs() {
        mDateTabAdapter = new DatePagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.main_activity_tab_viewpager);
        if (mViewPager != null) {
            mViewPager.setOffscreenPageLimit(mOrderedDateInWeek.length);
            mViewPager.setAdapter(mDateTabAdapter);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_activity_date_tab);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(mViewPager);
        }

        if (mViewPager != null) {
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    setActionBarTitle(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });

        }
    }

    private void setActionBarTitle(int positionInDatePager) {
        Calendar d = SugorokuonUtils.dayOfThisWeek(mOrderedDateInWeek[positionInDatePager]);

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                getString(R.string.date_mmddeee), Locale.JAPANESE);
        String date = dateFormat.format(new Date(d.getTimeInMillis()));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(date);
        }
    }

    private TimeTableFragment getFragmentByDayOfWeek(int dayOfWeek) {
        TimeTableFragment timeTableFragment = new TimeTableFragment();
        Calendar d = SugorokuonUtils.dayOfThisWeek(dayOfWeek);

        Bundle bundle = new Bundle();
        bundle.putInt(TimeTableFragment.PARAMS_KEY_YEAR, d.get(Calendar.YEAR));
        bundle.putInt(TimeTableFragment.PARAMS_KEY_MONTH, d.get(Calendar.MONTH) + 1);
        bundle.putInt(TimeTableFragment.PARAMS_KEY_DATE, d.get(Calendar.DATE));

        String stationId = getIntent().getStringExtra(EXTRA_STATION_ID);
        if (null != stationId) {
            bundle.putString(TimeTableFragment.PARAMS_KEY_STATION_ID, stationId);
        }

        timeTableFragment.setArguments(bundle);

        return timeTableFragment;
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

        int dayInWeek = SugorokuonUtils.getDayInRadioTimeTable();

        if (dayInWeek == Calendar.SUNDAY) {
            mViewPager.setCurrentItem(6);
        } else {
            mViewPager.setCurrentItem(dayInWeek - Calendar.MONDAY);
        }

        // 初期フォーカスが月曜日の場合、DateTabPagerの初期フォーカスが元々月曜なので、
        // ViewPagerのonPageSelectedが走らない。よって手動でタイトルを仕掛ける。
        if (dayInWeek == Calendar.MONDAY) {
            setActionBarTitle(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }

//    private void switchFragments(Fragment f, boolean enableReturnByBack, String tag) {
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//
//        if (null != tag) {
//            transaction.replace(R.id.main_activity_drawer_content_layout, f, tag);
//        } else {
//            transaction.replace(R.id.main_activity_drawer_content_layout, f);
//        }
//
//        if (enableReturnByBack) {
//            transaction.addToBackStack(null);
//        }
//
//        transaction.commit();
//    }
//
//    private void switchFragments(Fragment f, boolean enableReturnByBack) {
//        switchFragments(f, enableReturnByBack, null);
//    }

    private void showTimeTableFetchAlert(boolean updateStation, boolean updateToday) {
        Bundle params = new Bundle();
        params.putBoolean(TimeTableFetchAlertDialog.KEY_UPDATE_STATION, updateStation);
        params.putBoolean(TimeTableFetchAlertDialog.KEY_UPDATE_TODAY, updateToday);

        TimeTableFetchAlertDialog dialog = new TimeTableFetchAlertDialog();
        dialog.setArguments(params);
        dialog.show(getSupportFragmentManager(), TAG_SHOULD_LOAD_DIALOG);
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
                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivity(searchIntent);
                consumed = true;
            }
            break;
            case R.id.menu_fetch_latest_program: {
                showTimeTableFetchAlert(false, true);
                consumed = true;
            }
            break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item) || consumed;
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
            setupSwitchDateTabs();
            openTodaysTimeTableFragment();

            AsyncTask<Void, Void, List<Station>> stationLoaderTask =
                    new AsyncTask<Void, Void, List<Station>>() {
                        @Override
                        protected List<Station> doInBackground(Void... params) {
                            return (new StationApi(SugorokuonActivity.this)).load();
                        }

                        @Override
                        protected void onPostExecute(List<Station> stations) {
                            super.onPostExecute(stations);
                            RecyclerView stationList =
                                    (RecyclerView) findViewById(R.id.main_activity_station_list);
                            if (stationList != null) {
                                ((StationListAdapter) stationList.getAdapter()).update(stations);
                            }
                        }
                    };
            stationLoaderTask.execute();

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

    private void showHelloV2_3Dialog() {
        if (null == getSupportFragmentManager().findFragmentByTag(TAG_HELLO_V2_DIALOG)) {
            HelloV2DialogFragment dialog = new HelloV2DialogFragment();
            dialog.show(getSupportFragmentManager(), TAG_HELLO_V2_DIALOG);

            LaunchedCheckPreference.setLaunchedV23(this);
            LaunchedCheckPreference.setLaunchedV22(this);
            LaunchedCheckPreference.setLaunchedV2(this);
            LaunchedCheckPreference.setLaunched(this);
        }
    }

    @Override
    public void onStartV2app(boolean positive) {

        if (positive) {
            LaunchedCheckPreference.setLaunchedV2(this);
            startFetchTimeTable(TimeTableService.ACTION_UPDATE_STATION_AND_TIME_TABLE);
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

    @Override
    protected boolean isMainActivity() {
        return true;
    }
}