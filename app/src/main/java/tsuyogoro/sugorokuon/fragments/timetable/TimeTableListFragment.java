/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.fragments.timetable;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.databinding.ProgramListItemCardBinding;
import tsuyogoro.sugorokuon.models.apis.TimeTableApi;
import tsuyogoro.sugorokuon.models.entities.OnedayTimetable;
import tsuyogoro.sugorokuon.models.entities.Program;
import tsuyogoro.sugorokuon.utils.SugorokuonUtils;

public class TimeTableListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<Program>> {

    public static String PARAM_KEY_DATE_YEAR = "key_date_year";

    public static String PARAM_KEY_DATE_MONTH = "key_date_month";

    public static String PARAM_KEY_DATE_DATE = "key_date_date";

    public static String PARAM_KEY_STATION_ID = "key_station_id";

    public static String PARAM_KEY_FREQUENCY_LIST_AD = "key_frequency_list_ad";

    public static final int MIN_VALUE_FREQUENCY = 5;

    private RecyclerView mRecyclerView;

    private RecyclerView.Adapter mAdapter;

    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View rootView = inflater.inflate(R.layout.program_list, null);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.program_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);

        return rootView;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLoaderManager().initLoader(0, getArguments(), this);

        // TOOD : この辺でぐるぐる出したい
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Loader<List<Program>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<List<Program>>(getActivity()) {
            @Override
            public List<Program> loadInBackground() {
                int year = getArguments().getInt(PARAM_KEY_DATE_YEAR);
                int month = getArguments().getInt(PARAM_KEY_DATE_MONTH);
                int date = getArguments().getInt(PARAM_KEY_DATE_DATE);
                String stationId = getArguments().getString(PARAM_KEY_STATION_ID);

                if (isAbandoned()) {
                    return null;
                }
                TimeTableApi timeTableApi = new TimeTableApi(getContext());
                OnedayTimetable timeTable =
                        timeTableApi.fetchTimetable(year, month, date, stationId);

                return timeTable.programs;
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

        // specify an adapter (see also next example)
        mAdapter = new TimeTableListAdapter(programs, new TimeTableListAdapter.ItemClickListener() {
            @Override
            public void onItemClicked(Program program) {

                ProgramInfoBottomSheetMaker.show(program, getActivity());

                // TODO : 旧形式のレイアウトであればこれまで通りのフローに流した方が良いのかも
                // ParentFragmentは、PagerAdapterを生成しているTimeTableFragmentになる
//                IProgramListItemTappedListener listener =
//                        (IProgramListItemTappedListener) getParentFragment();
//                listener.onProgramTapped(program);
            }

            @Override
            public void onBrowserOpenClicked(Program program) {
                if (program.url != null && program.url.length() > 0) {
                    SugorokuonUtils.launchChromeTab(getActivity(), Uri.parse(program.url));
                }
            }
        }, getActivity(), getArguments().getInt(PARAM_KEY_FREQUENCY_LIST_AD));

        if (0 < mAdapter.getItemCount()) {
            mRecyclerView.setAdapter(mAdapter);

            // もし今日の番組表ならば、今の時間にfocusを当てる
            Calendar now = Calendar.getInstance(Locale.JAPAN);
            for (int i = 0; i < programs.size(); i++) {
                if (now.compareTo(programs.get(i).startTime) >= 0 &&
                        now.compareTo(programs.get(i).endTime) <= 0) {
                    mLayoutManager.scrollToPosition(i);
                    break;
                }
            }

            mAdapter.notifyDataSetChanged();
        } else {
            mRecyclerView.setVisibility(View.GONE);

            TextView noData = (TextView) getView().findViewById(R.id.program_list_no_data);
            noData.setText(getString(R.string.program_list_no_data));
            noData.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Program>> loader) {
    }

    public static class TimeTableListAdapter
            extends RecyclerView.Adapter<TimeTableListAdapter.ViewHolder> {

        private List<Program> mPrograms;

        private final int mFrequencyListAd;

        private static SimpleDateFormat sFormatStartTime;

        private static SimpleDateFormat sFormatEndTime;

        private ItemClickListener mListener;

        private Context mContext;

        private interface ItemClickListener {

            void onItemClicked(Program program);

            void onBrowserOpenClicked(Program program);

        }

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public static class ViewHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener {

            private final ProgramListItemCardBinding mBinding;

            private ItemClickListener mListener;

            private Program program;

            public ViewHolder(View view, ItemClickListener listener) {
                super(view);

                mBinding = DataBindingUtil.bind(view);
                mBinding.programListItemCardView.setOnClickListener(this);

                mBinding.programListItemOpenBrowser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (program.url != null && program.url.length() > 0) {
                            Log.d("TestTestTest", "Clicked open browser : " + program.url);
                        }
                    }
                });
                mListener = listener;

            }

            public ProgramListItemCardBinding getBinding() {
                return mBinding;
            }

            @Override
            public void onClick(View v) {
                if (null != program) {
                    mListener.onItemClicked(program);
                }
            }
        }

        public TimeTableListAdapter(List<Program> programs, ItemClickListener listener,
                                    Context context, int frequencyListAd) {
            mPrograms = programs;
            mListener = listener;
            mContext = context;

            if (frequencyListAd > mPrograms.size()) {
                mFrequencyListAd = 0;
            } else if (frequencyListAd > 0 && frequencyListAd < MIN_VALUE_FREQUENCY) {
                mFrequencyListAd = MIN_VALUE_FREQUENCY;
            } else {
                mFrequencyListAd = frequencyListAd;
            }

            if (null == sFormatStartTime || null == sFormatEndTime) {
                sFormatStartTime = new SimpleDateFormat(
                        context.getString(R.string.onair_start_time), Locale.US);
                sFormatEndTime = new SimpleDateFormat(
                        context.getString(R.string.onair_end_time), Locale.US);
            }
        }

        @Override
        public TimeTableListAdapter.ViewHolder onCreateViewHolder(
                ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.program_list_item_card, parent, false);

            return new ViewHolder(v, mListener);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            if (mFrequencyListAd > 0 && position % (mFrequencyListAd + 1) == mFrequencyListAd) {
                holder.getBinding().setIsAdEntry(true);
                return;
            }

            holder.getBinding().setIsAdEntry(false);

            int p = (mFrequencyListAd > 0) ? position - (position / (mFrequencyListAd + 1)) : position;
            final Program program = mPrograms.get(p);

            holder.program = program;
            holder.getBinding().setProgram(program);

            String iconPath = program.getSymbolIconPath(mContext);
            if (iconPath != null) {
                if (iconPath.startsWith("http")) {
                    Picasso.with(mContext).load(iconPath)
                            .transform(new CropCircleTransformation())
                            .into(holder.getBinding().programListItemImage);
                } else {
                    Picasso.with(mContext).load(new File(iconPath))
                            .into(holder.getBinding().programListItemImage);
                }
            }

            holder.getBinding().programListItemOpenBrowser.setOnClickListener(
                    v -> mListener.onBrowserOpenClicked(program));

            String start = sFormatStartTime.format(new Date(program.startTime.getTimeInMillis()));
            holder.getBinding().setStarttime(start);

            String end = sFormatEndTime.format(new Date(program.endTime.getTimeInMillis()));
            holder.getBinding().setEndtime(end);
        }

        @Override
        public int getItemCount() {
            if (mFrequencyListAd > 0) {
                return mPrograms.size() + (mPrograms.size() / mFrequencyListAd);
            } else {
                return mPrograms.size();
            }
        }
    }

}