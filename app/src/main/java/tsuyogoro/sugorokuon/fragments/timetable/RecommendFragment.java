/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.fragments.timetable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.SugorokuonApplication;
import tsuyogoro.sugorokuon.models.apis.TimeTableApi;
import tsuyogoro.sugorokuon.models.entities.Program;
import tsuyogoro.sugorokuon.services.TimeTableService;

public class RecommendFragment extends SearchFragmentBase {

    private BroadcastReceiver mUpdateReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(getString(R.string.title_recommends));

        // GoogleAnalytics tracking
        Tracker t = ((SugorokuonApplication) getActivity().getApplication()).getTracker();
        t.setScreenName(getClass().getSimpleName());
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getLoaderManager().initLoader(0, getArguments(), this);

        mUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getLoaderManager().initLoader(0, getArguments(), RecommendFragment.this);
            }
        };

        // オススメ更新があった時にAdapterを更新できるように
        IntentFilter filter = new IntentFilter(
                TimeTableService.NotifyAction.ACTION_NOTIFY_RECOMMEND_UPDATED);
        getActivity().registerReceiver(mUpdateReceiver, filter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mUpdateReceiver) {
            getActivity().unregisterReceiver(mUpdateReceiver);
        }
        SugorokuonApplication.getRefWatcher(getActivity()).watch(this);
    }

    @Override
    protected List<Program> doSearch(Bundle args) {
        TimeTableApi timeTableApi = new TimeTableApi(getActivity());
        return timeTableApi.fetchRecommends(Calendar.getInstance(Locale.JAPAN));
    }

}
