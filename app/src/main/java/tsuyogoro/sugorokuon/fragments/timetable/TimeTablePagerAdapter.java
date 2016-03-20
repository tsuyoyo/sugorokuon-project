/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.fragments.timetable;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.sql.Time;
import java.util.List;

import tsuyogoro.sugorokuon.models.apis.StationApi;
import tsuyogoro.sugorokuon.models.entities.Station;

class TimeTablePagerAdapter extends FragmentPagerAdapter {

    private int mYear;

    private int mMonth;

    private int mDate;

    private List<Station> mStations;

    /**
     * コンストラクタ
     *
     * @param fm
     * @param fragment
     * @param year
     * @param month 5月なら5 (Calendarみたいに-1しない)
     * @param date
     */
    public TimeTablePagerAdapter(FragmentManager fm, Context context,
                                 int year, int month, int date) {
        super(fm);

        mYear = year;
        mMonth = month;
        mDate = date;

        StationApi stationApi = new StationApi(context);
        mStations = stationApi.load();
    }

    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
        bundle.putInt(TimeTableListFragment.PARAM_KEY_DATE_YEAR, mYear);
        bundle.putInt(TimeTableListFragment.PARAM_KEY_DATE_MONTH, mMonth);
        bundle.putInt(TimeTableListFragment.PARAM_KEY_DATE_DATE, mDate);
        bundle.putString(TimeTableListFragment.PARAM_KEY_STATION_ID, mStations.get(position).id);

        Fragment fragment = new TimeTableListFragment();
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

    public Station getStation(int position) {
        return mStations.get(position);
    }
}

