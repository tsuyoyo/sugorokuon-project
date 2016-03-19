/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.fragments.timetable;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.SugorokuonApplication;
import tsuyogoro.sugorokuon.models.entities.Station;
import tsuyogoro.sugorokuon.models.prefs.LastStationFocusPreference;
import tsuyogoro.sugorokuon.utils.SugorokuonLog;

public class TimeTableFragment extends ProgramViewerFragment {

    @Override
    protected int listAreaViewId() {
        return R.id.timetable_pager;
    }

    public static final String PARAMS_KEY_YEAR = "timetable_fragment_params_key_year";

    public static final String PARAMS_KEY_MONTH = "timetable_fragment_params_key_month";

    public static final String PARAMS_KEY_DATE = "timetable_fragment_params_key_date";

    public static final String PARAMS_KEY_STATION_ID = "timetable_fragment_params_key_station_id";

    private ViewPager mViewPager;

    private TimeTablePagerAdapter mPagerAdapter;

    private View mStationListFrame;

    private ImageButton mStationListOpenBtn;

    private AsyncTask<Void, Void, TimeTablePagerAdapter> mSetupTask;

    /**
     * 局の情報の更新などを入れる際、これを呼ぶこと
     * current pageのindexがはみ出ちゃうかもしれない
     *
     */
    public static void resetCurrentPageIndex(Context context) {
        LastStationFocusPreference.saveLastFocusedIndex(context, 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mSetupTask && mSetupTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
            mSetupTask.cancel(true);
        }
        SugorokuonApplication.getRefWatcher(getActivity()).watch(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // GoogleAnalytics tracking
        Tracker t = ((SugorokuonApplication) getActivity().getApplication()).getTracker();
        t.setScreenName(getClass().getSimpleName());
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.time_table, menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != getView()) {
            getView().setFocusableInTouchMode(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return getActivity().onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.timetable_layout, null);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final int year = getArguments().getInt(PARAMS_KEY_YEAR);
        final int month = getArguments().getInt(PARAMS_KEY_MONTH);
        final int date = getArguments().getInt(PARAMS_KEY_DATE);

        mViewPager = (ViewPager) view.findViewById(R.id.timetable_pager);

        // TimeTablePagerAdapterを作る時にDBアクセスがあるので、
        // 一度backgroundに逃す
        mSetupTask = new AsyncTask<Void, Void, TimeTablePagerAdapter>() {
            @Override
            protected TimeTablePagerAdapter doInBackground(Void... params) {
                if (isCancelled()) {
                    return null;
                }

                TimeTablePagerAdapter timeTablePagerAdapter = new TimeTablePagerAdapter(
                        getChildFragmentManager(), TimeTableFragment.this, year, month, date);
                return timeTablePagerAdapter;
            }

            @Override
            protected void onPostExecute(final TimeTablePagerAdapter timeTablePagerAdapter) {
                super.onPostExecute(timeTablePagerAdapter);

                if (isCancelled()) {
                    return;
                }

                mPagerAdapter = timeTablePagerAdapter;

                mViewPager.setAdapter(mPagerAdapter);
                mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        LastStationFocusPreference.saveLastFocusedIndex(getActivity(), position);
                        SugorokuonLog.d("onPageSelected : " + position);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

                // 初期フォーカスの位置
                String stationId = getArguments().getString(PARAMS_KEY_STATION_ID);
                int initialIndex = -1;
                if (null != stationId) {
                    initialIndex = getStationIndex(stationId);
                }
                if (0 > initialIndex) {
                    // stationIdが存在していなければ、getStationIndexは-1を返す
                    initialIndex = LastStationFocusPreference.lastFocusedIndex(getActivity());
                }
                mViewPager.setCurrentItem(initialIndex);

                // PagerTabのindicatorはjavaからしか設定できないみたい
                PagerTabStrip tabStrip =
                        (PagerTabStrip) view.findViewById(R.id.timetable_pager_tab_strip);
                tabStrip.setTabIndicatorColorResource(R.color.app_primary);

                // StationList
                mStationListOpenBtn = (ImageButton) view.findViewById(
                        R.id.timetable_station_list_open_btn);
                mStationListFrame = view.findViewById(R.id.timetable_station_list_frame);

                mStationListOpenBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setStationListVisibility(true);
                        closeInfoViewer();
                    }
                });
                ListView stationList = (ListView) view.findViewById(R.id.timetable_station_list);
                stationList.setAdapter(new StationListAdapter());
                stationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        setStationListVisibility(false);
                        mViewPager.setCurrentItem(position);
                    }
                });

                // 下のフレームへタッチを通さない
                mStationListFrame.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
                view.findViewById(R.id.timetable_station_list_other_area)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setStationListVisibility(false);
                            }
                        });

                // Backキーのハンドリング
                view.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        SugorokuonLog.d("onKeyListener is called");

                        boolean consumed = false;

                        if (KeyEvent.ACTION_DOWN == event.getAction()
                                && KeyEvent.KEYCODE_BACK == keyCode) {
                            if (View.VISIBLE == mStationListFrame.getVisibility()) {
                                setStationListVisibility(false);
                                consumed = true;

                                SugorokuonLog.d("station is closed");
                            }
                            else if (isInfoViewerVisible()) {
                                consumed = backWebView();
                                if (!consumed) {
                                    consumed = closeInfoViewer();
                                }

                                SugorokuonLog.d("WebView handled it");
                            }
                        }
                        SugorokuonLog.d("onKeyListener is done : " + consumed);
                        return consumed;
                    }
                });
                SugorokuonLog.d("setOnKeyListener was done");
            }
        };
        mSetupTask.execute();
    }

    private void setStationListVisibility(boolean visible) {
        if (visible) {
            mStationListFrame.setVisibility(View.VISIBLE);
            mStationListOpenBtn.setVisibility(View.GONE);
        } else {
            mStationListFrame.setVisibility(View.GONE);
            mStationListOpenBtn.setVisibility(View.VISIBLE);
        }
    }

    private int getStationIndex(String stationId) {
        int index = -1;
        for (int i=0; i < mPagerAdapter.getCount(); i++) {
            if (mPagerAdapter.getStation(i).id.equals(stationId)) {
                index = i;
                break;
            }
        }
        return index;
    }

    class StationListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mPagerAdapter.getCount();
        }

        @Override
        public Object getItem(int position) {
            return mPagerAdapter.getStation(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater(
                    getArguments()).inflate(R.layout.stationlist_item, null);

            ImageView logo = (ImageView) view.findViewById(R.id.station_list_logo);
            TextView name = (TextView) view.findViewById(R.id.station_list_name);

            Station station = mPagerAdapter.getStation(position);

            logo.setImageDrawable(new BitmapDrawable(getResources(),
                    station.loadLogo(getActivity())));
            name.setText(station.name);

            return view;
        }
    }

}
