/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.fragments.timetable;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.databinding.SearchResultListItemCardBinding;
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
abstract class SearchFragmentBase extends Fragment implements
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

        ItemClickListener listener = new ItemClickListener() {
            @Override
            public void onItemClicked(Program program) {
                ProgramInfoBottomSheetMaker.show(program, getActivity());

                // TODO : 旧レイアウト用
                //onProgramTapped(program);
            }
        };

        mAdapter = new SearchResultAdapter(programs, listener, getActivity());

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

    public static class Formatters {

        private SimpleDateFormat mStartTime;

        private SimpleDateFormat mEndTime;

        private SimpleDateFormat mOnAirDate;

        public Formatters(Context context) {
            mStartTime = new SimpleDateFormat(
                    context.getString(R.string.onair_start_time), Locale.US);
            mEndTime = new SimpleDateFormat(
                    context.getString(R.string.onair_end_time), Locale.US);
            mOnAirDate = new SimpleDateFormat(
                    context.getString(R.string.onair_date), Locale.JAPAN);
        }

        public CharSequence formatOnAirDate(Calendar onAirDate) {
            return mOnAirDate.format(new Date(onAirDate.getTimeInMillis()));
        }

        public CharSequence formatStartTime(Calendar onAirDate) {
            return mStartTime.format(new Date(onAirDate.getTimeInMillis()));
        }

        public CharSequence formatEndTime(Calendar onAirDate) {
            return mEndTime.format(new Date(onAirDate.getTimeInMillis()));
        }

    }

    public interface ItemClickListener {

        void onItemClicked(Program program);

    }

    private static class SearchResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<Program> mSearchResult;

        private Formatters mFormatters;

        private ItemClickListener mListener;

        public SearchResultAdapter(List<Program> programs,
                                   ItemClickListener listener, Context context) {
            mSearchResult = new ArrayList<>(programs);
            mFormatters = new Formatters(context);
            mListener = listener;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            final SearchResultListItemCardBinding binding = DataBindingUtil.inflate(
                    inflater, R.layout.search_result_list_item_card, parent, false);

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClicked(binding.getProgram());
                }
            });

            return new RecyclerView.ViewHolder(binding.getRoot()) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            SearchResultListItemCardBinding binding = DataBindingUtil.getBinding(holder.itemView);
            Context context = binding.getRoot().getContext();
            Program program = mSearchResult.get(position);

            binding.setProgram(mSearchResult.get(position));

            StationApi stationApi = new StationApi(binding.getRoot().getContext());
            binding.programListItemStationLogo.setImageBitmap(
                    stationApi.load(program.stationId).loadLogo(context));

            binding.programListItemDate.setText(mFormatters.formatOnAirDate(program.startTime));
            binding.programListItemStarttime.setText(mFormatters.formatStartTime(program.startTime));
            binding.programListItemEndtime.setText(mFormatters.formatEndTime(program.endTime));
        }

        @Override
        public int getItemCount() {
            return mSearchResult.size();
        }
    }

}
