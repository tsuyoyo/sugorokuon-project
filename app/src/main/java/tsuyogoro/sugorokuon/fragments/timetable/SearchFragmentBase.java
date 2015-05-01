/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.fragments.timetable;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.models.apis.StationApi;
import tsuyogoro.sugorokuon.models.entities.Program;

/**
 * program_list.xmlのlayoutを使ってlistを表示させるFragmentのベースクラス。
 * TimeTableApiのsearch系 (recommendとか) を使って番組情報を表示する画面を想定しているので
 * この名前 (SearchBase)。CardViewに必要な手続き、backgroundでのデータの読み込みロジック、
 * CardViewへのデータ設定ロジックが実装されている。
 * <p/>
 * 子クラスは、DBからのデータ取得の仕方と、画面特有の機能を実装すること。
 */
abstract class SearchFragmentBase extends ProgramViewerFragment implements
        LoaderManager.LoaderCallbacks<List<Program>> {

    abstract protected List<Program> doSearch(Bundle args);

    protected RecyclerView mRecyclerView;

    protected RecyclerView.Adapter mAdapter;

    protected RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.search_result_layout, null);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.program_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        return rootView;
    }

    @Override
    public Loader<List<Program>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<List<Program>>(getActivity()) {
            @Override
            public List<Program> loadInBackground() {
                return doSearch(args);
            }

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                forceLoad();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Program>> loader, List<Program> programs) {

        SearchResultListCardAdapter.ItemClickListener listener =
                new SearchResultListCardAdapter.ItemClickListener() {
                    @Override
                    public void onItemClicked(Program program) {
                        onProgramTapped(program);
                    }
                };

        mAdapter = new SearchResultListCardAdapter(programs, listener, getActivity());

        mRecyclerView.setAdapter(mAdapter);

        TextView noData = (TextView) getView().findViewById(R.id.program_list_no_data);
        noData.setText(getString(R.string.program_list_no_data));

        if (0 < mAdapter.getItemCount()) {
            mRecyclerView.setVisibility(View.VISIBLE);
            noData.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Program>> loader) {
    }

    @Override
    protected int listAreaViewId() {
        return R.id.search_result_list;
    }

    static private class SearchResultListCardAdapter
            extends RecyclerView.Adapter<SearchResultListCardAdapter.ViewHolder> {

        private static SimpleDateFormat sFormatStartTime;

        private static SimpleDateFormat sFormatEndTime;

        private static SimpleDateFormat sFormatOnAirDate;

        private Context mContext;

        private List<Program> mPrograms;

        private ItemClickListener mListener;

        interface ItemClickListener {

            public void onItemClicked(Program program);

        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            private View mView;

            private Program mProgram;

            public ViewHolder(View view, final ItemClickListener listener) {
                super(view);

                mView = view;

                mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != mProgram) {
                            listener.onItemClicked(mProgram);
                        }
                    }
                });
            }

            void replace(Program program, Context context) {
                mProgram = program;

                TextView startTime = (TextView) mView.findViewById(R.id.program_list_item_starttime);
                TextView endTime = (TextView) mView.findViewById(R.id.program_list_item_endtime);

                startTime.setText(sFormatStartTime.format(new Date(program.startTime.getTimeInMillis())));
                endTime.setText(sFormatEndTime.format(new Date(program.endTime.getTimeInMillis())));

                // Title
                TextView title = (TextView) mView.findViewById(R.id.program_list_item_title);
                title.setText(program.title);

                // パーソナリティ
                TextView per = (TextView) mView.findViewById(R.id.program_list_item_personality);
                per.setText(program.personalities);

                // onAirの日付
                TextView date = (TextView) mView.findViewById(R.id.program_list_item_date);
                date.setText(sFormatOnAirDate.format(new Date(program.startTime.getTimeInMillis())));

                // 局のロゴ
                StationApi stationApi = new StationApi(context);
                ImageView logo = (ImageView) mView.findViewById(R.id.program_list_item_station_logo);
                logo.setImageBitmap(stationApi.load(program.stationId).loadLogo(context));
            }
        }

        public SearchResultListCardAdapter(List<Program> programs,
                                           ItemClickListener listener, Context context) {
            mContext = context;
            mPrograms = programs;
            mListener = listener;

            if (null == sFormatStartTime || null == sFormatEndTime || null == sFormatOnAirDate) {
                sFormatStartTime = new SimpleDateFormat(
                        mContext.getString(R.string.onair_start_time), Locale.US);
                sFormatEndTime = new SimpleDateFormat(
                        mContext.getString(R.string.onair_end_time), Locale.US);
                sFormatOnAirDate = new SimpleDateFormat(
                        mContext.getString(R.string.onair_date), Locale.JAPAN);
            }
        }

        // Create new views (invoked by the layout manager)
        @Override
        public SearchResultListCardAdapter.ViewHolder onCreateViewHolder(
                ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.search_result_list_item_card, parent, false);

            ViewHolder vh = new ViewHolder(v, mListener);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.replace(mPrograms.get(position), mContext);
        }

        @Override
        public int getItemCount() {
            return (null != mPrograms) ? mPrograms.size() : 0;
        }
    }

}
