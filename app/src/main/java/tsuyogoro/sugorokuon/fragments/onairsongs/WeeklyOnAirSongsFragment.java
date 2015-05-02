/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.fragments.onairsongs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.List;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.SugorokuonApplication;
import tsuyogoro.sugorokuon.models.apis.StationApi;
import tsuyogoro.sugorokuon.models.entities.Station;
import tsuyogoro.sugorokuon.models.prefs.LastStationFocusPreference;
import tsuyogoro.sugorokuon.services.OnAirSongsService;
import tsuyogoro.sugorokuon.utils.SugorokuonLog;


public class WeeklyOnAirSongsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<WeeklyOnAirSongsFragment.OnAirSongPagerAdapter> {

    private static int sCurrentIndex = 0;

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
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.onair_song_view_layout, null);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.weekly_onair_song_title));
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.onair_song_list_view, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        boolean res = super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.menu_fetch_latest_onair_songs:
                Intent intent = new Intent(OnAirSongsService.ACTION_FETCH_ON_AIR_SONGS);
                intent.setPackage(getActivity().getPackageName());
                getActivity().startService(intent);

                FetchProgressDialog dialog = new FetchProgressDialog();
                dialog.show(getFragmentManager(), "ProgressDialog");
                break;
        }

        return res;
    }

    @Override
    public Loader<OnAirSongPagerAdapter> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<OnAirSongPagerAdapter>(getActivity()) {
            @Override
            public OnAirSongPagerAdapter loadInBackground() {

                StationApi stationApi = new StationApi(getContext());
                List<Station> stations = stationApi.load(true);

                return new OnAirSongPagerAdapter(getChildFragmentManager(), stations);
            }

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                forceLoad();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<OnAirSongPagerAdapter> loader, OnAirSongPagerAdapter adapter) {
        ViewPager viewPager = (ViewPager) getView().findViewById(R.id.onair_song_list_pager);
        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(sCurrentIndex);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                sCurrentIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // PagerTabのindicatorはjavaからしか設定できないみたい
        PagerTabStrip tabStrip = (PagerTabStrip) getView().findViewById(
                R.id.onair_song_list_pager_tabstrip);
        tabStrip.setTabIndicatorColorResource(R.color.onair_song_primary);
    }

    @Override
    public void onLoaderReset(Loader<OnAirSongPagerAdapter> loader) {
    }

    static class OnAirSongPagerAdapter extends FragmentPagerAdapter {

        private final List<Station> mStations;

        public OnAirSongPagerAdapter(FragmentManager fm, List<Station> stations) {
            super(fm);
            mStations = stations;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putString(WeeklyOnAirSongsListFragment.KEY_ON_AIR_SONG_STATION_ID,
                    mStations.get(position).id);

            Fragment fragment = new WeeklyOnAirSongsListFragment();
            fragment.setArguments(bundle);

            return fragment;
        }

        @Override
        public int getCount() {
            return mStations.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mStations.get(position).name;
        }
    }

    public static class FetchProgressDialog extends DialogFragment {

        private BroadcastReceiver mBroadCastReceiver;

        public FetchProgressDialog(){
            super();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setCancelable(false);

            mBroadCastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    dismiss();
                }
            };
            getActivity().registerReceiver(mBroadCastReceiver,
                    new IntentFilter(OnAirSongsService.NOTIFY_ON_FETCH_LATEST_SETLIST));
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            getActivity().unregisterReceiver(mBroadCastReceiver);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreateDialog(savedInstanceState);

            ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage(getActivity().getString(R.string.feteching_onair_songs));

            return dialog;
        }
    }

}
