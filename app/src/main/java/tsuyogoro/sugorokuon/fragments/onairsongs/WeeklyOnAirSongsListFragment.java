/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.fragments.onairsongs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.models.apis.OnAirSongsApi;
import tsuyogoro.sugorokuon.models.entities.OnAirSong;
import tsuyogoro.sugorokuon.services.OnAirSongsService;
import tsuyogoro.sugorokuon.utils.SugorokuonUtils;

public class WeeklyOnAirSongsListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<SetListAdapter> {

    public static final String KEY_ON_AIR_SONG_STATION_ID = "key_onair_song_stationid";

    private String mStationId;

    private BroadcastReceiver mReceiver;

    private SetListAdapter mAdapter;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mReceiver) {
            getActivity().unregisterReceiver(mReceiver);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.onair_song_list, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mStationId = getArguments().getString(KEY_ON_AIR_SONG_STATION_ID);

        // 曲情報を取得
        getLoaderManager().restartLoader(0, null, this);

        // OnAirSongServiceから最新のデータ取得の完了通知を受けて、曲情報をDBに取りに行く
        IntentFilter filter = new IntentFilter(OnAirSongsService.NOTIFY_ON_FETCH_LATEST_SETLIST);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getLoaderManager().restartLoader(0, null, WeeklyOnAirSongsListFragment.this);
            }
        };
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public Loader<SetListAdapter> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<SetListAdapter>(getActivity()) {
            @Override
            public SetListAdapter loadInBackground() {

                Calendar from = SugorokuonUtils.beginningOfThisWeek();
                Calendar to = Calendar.getInstance();

                OnAirSongsApi onAirSongsApi = new OnAirSongsApi(getContext());

                List<OnAirSong> songs = onAirSongsApi.search(from, to, mStationId);

                SetListAdapter adapter = new SetListAdapter(getContext());
                for (OnAirSong s : songs) {
                    adapter.addSong(s);
                }

                return adapter;
            }

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                forceLoad();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<SetListAdapter> loader, SetListAdapter adapter) {
        mAdapter = adapter;

        ListView list = (ListView) getView().findViewById(R.id.onair_song_list);
        list.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        // Itemをclickすると、曲名とアーティスト名でGoogle検索を行う
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OnAirSong song = (OnAirSong) mAdapter.getItem(position);

                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra("query", song.artist + " " + song.title);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<SetListAdapter> loader) {

    }

}
