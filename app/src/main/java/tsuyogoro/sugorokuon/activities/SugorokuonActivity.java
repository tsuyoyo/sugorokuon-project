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
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import tsuyogoro.sugorokuon.BuildTypeVariables;
import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.fragments.dialogs.HelloV2DialogFragment;
import tsuyogoro.sugorokuon.fragments.dialogs.MessageDialogFragment;
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
        , HelloV2DialogFragment.IHelloV2DialogListener, TimeTableFetchAlertDialog.OnOptionSelectedListener
        , NavigationView.OnNavigationItemSelectedListener {

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        if (toolbar != null) {
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
        }

        setupStationListBottomSheet();

        setupFloatingActionButton();

        setupDrawer();

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

        // For AdMob
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void setupFloatingActionButton() {
        final FloatingActionButton fab = (FloatingActionButton) findViewById(
                R.id.main_activity_open_stations_btn);

        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // FABを押した時にsub menuのボタンは大きくなるアニメーションをする
                    final FloatingActionButton songListBtn = (FloatingActionButton) findViewById(
                            R.id.main_activity_fab_open_song_list);
                    final FloatingActionButton favoriteListBtn = (FloatingActionButton) findViewById(
                            R.id.main_activity_fab_open_favorite_list);
                    final TextView songListBtnLabel = (TextView) findViewById(
                            R.id.main_activity_fab_open_song_list_label);
                    final TextView favoriteListBtnLabel = (TextView) findViewById(
                            R.id.main_activity_fab_open_favorite_list_label);

                    songListBtn.setVisibility(View.INVISIBLE);
                    favoriteListBtn.setVisibility(View.INVISIBLE);
                    songListBtnLabel.setVisibility(View.INVISIBLE);
                    favoriteListBtnLabel.setVisibility(View.INVISIBLE);


                    // FABを押した際、白い背景をフェードインさせる
                    final View fabMenuArea = findViewById(R.id.main_activity_fab_menu_area);
                    Animation bgAnimation = new AlphaAnimation(0f, 1f);
                    bgAnimation.setDuration(200);

                    bgAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            Animation subFabAnimation = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, 50, 50);
                            subFabAnimation.setDuration(200);

                            songListBtnLabel.setVisibility(View.VISIBLE);
                            songListBtnLabel.startAnimation(subFabAnimation);
                            favoriteListBtnLabel.setVisibility(View.VISIBLE);
                            favoriteListBtnLabel.startAnimation(subFabAnimation);

                            songListBtn.show();
                            favoriteListBtn.show();

                            fabMenuArea.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    fabMenuArea.startAnimation(bgAnimation);

                    FloatingActionButton closeFab = (FloatingActionButton) findViewById(
                            R.id.main_activity_fab_close_btn_list);
                    closeFab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            fabMenuArea.setVisibility(View.GONE);
                        }
                    });

                    fabMenuArea.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            fabMenuArea.setVisibility(View.GONE);
                            return true;
                        }
                    });
                }
            });
        }
    }

    private ViewPager mViewPager;

    private StationListAdapter.IStationListListener mStationListListener =
            new StationListAdapter.IStationListListener() {
                @Override
                public void onStationSelected(Station station) {
                    int currentFocus = mViewPager.getCurrentItem();

                    TimeTableFragment timeTableFragment =
                            (TimeTableFragment) mDateTabAdapter.getItem(currentFocus);

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
            titleBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View bottomSheet = findViewById(R.id.main_activity_stationlist_bottom_sheet);
                    BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);

                    TextView titleText = (TextView) findViewById(
                            R.id.main_actiity_stationlist_title_text);

                    final FloatingActionButton fab = (FloatingActionButton) findViewById(
                            R.id.main_activity_open_stations_btn);

                    if (titleText != null && fab != null) {
                        // 初期状態の設定で表示されている (behavior_peekHeightで設定した高さ)
                        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            titleText.setText(getString(R.string.station_list_dialog_description));
                            fab.hide();
                        }
                        // 一番大きくなっている状態 (layout_heightの高さで表示されている)
                        else if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            titleText.setText(getString(R.string.station_list_dialog_title));
                            fab.show();
                        }
                    }
                }
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

    private FragmentPagerAdapter mDateTabAdapter;

    private void setupSwitchDateTabs() {
        mDateTabAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            TimeTableFragment[] mFragments = new TimeTableFragment[mOrderedDateInWeek.length];

            @Override
            public Fragment getItem(int position) {
                if (mFragments[position] == null) {
                    mFragments[position] = getFragmentByDayOfWeek(mOrderedDateInWeek[position]);
                }
                return mFragments[position];
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
        };

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
                    Calendar d = SugorokuonUtils.dayOfThisWeek(mOrderedDateInWeek[position]);

                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            getString(R.string.date_mmddeee), Locale.JAPANESE);
                    String date = dateFormat.format(new Date(d.getTimeInMillis()));

                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(date);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
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

        ViewPager viewPager = (ViewPager) findViewById(R.id.main_activity_tab_viewpager);

        int dateToday = Calendar.getInstance(Locale.JAPAN).get(Calendar.DAY_OF_WEEK);
        for (int index = 0; index < mOrderedDateInWeek.length; index++) {
            if (dateToday == mOrderedDateInWeek[index]) {
                viewPager.setCurrentItem(index);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
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

        NavigationView navView = (NavigationView) findViewById(R.id.main_activity_navigation_view);
        navView.setNavigationItemSelectedListener(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
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
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_drawer_menu_recommends:
                break;
            case R.id.main_drawer_menu_onair_songs:

                Intent onAirSongsIntent = new Intent(this, OnAirSongsActivity.class);
                startActivity(onAirSongsIntent);
//                        if (getSupportFragmentManager().findFragmentByTag(
//                                TAG_WEEKLY_ON_AIR_SONGS_FRAGMENT) == null) {
//                            switchFragments(new WeeklyOnAirSongsFragment(), true,
//                                    TAG_WEEKLY_ON_AIR_SONGS_FRAGMENT);
//                        }
                // TODO : Activityを起動するようにする (今Activityが無いので作る)
                mDrawerLayout.closeDrawers();
                break;
            case R.id.main_drawer_menu_settings:
                mDrawerLayout.closeDrawers();
                Intent intentForSettings = new Intent(
                        SugorokuonActivity.this, SugorokuonSettingActivity.class);
                startActivityForResult(intentForSettings, REQUESTCODE_SETTINGS);
                break;
            case R.id.main_drawer_menu_about:
                break;
            case R.id.main_drawer_menu_rating:
                mDrawerLayout.closeDrawers();
                Intent googlePlayIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.google_play_app_url)));
                googlePlayIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(googlePlayIntent);
                break;
        }
        return true;
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

}