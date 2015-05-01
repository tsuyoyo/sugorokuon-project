/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.fragments.timetable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.models.apis.TimeTableApi;
import tsuyogoro.sugorokuon.models.entities.OnedayTimetable;
import tsuyogoro.sugorokuon.models.entities.Program;

public class TimeTableListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<Program>> {

    public static String PARAM_KEY_DATE_YEAR = "key_date_year";

    public static String PARAM_KEY_DATE_MONTH = "key_date_month";

    public static String PARAM_KEY_DATE_DATE = "key_date_date";

    public static String PARAM_KEY_STATION_ID = "key_station_id";

    private RecyclerView mRecyclerView;

    private RecyclerView.Adapter mAdapter;

    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.program_list, null);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.program_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // 区切り線
        mRecyclerView.addItemDecoration(new TimeTableListDivider(getActivity()));

        mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);

        return rootView;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLoaderManager().initLoader(0, getArguments(), this);
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
                // ParentFragmentは、PagerAdapterを生成しているTimeTableFragmentになる
                IProgramListItemTappedListener listener =
                        (IProgramListItemTappedListener) getParentFragment();
                listener.onProgramTapped(program);
            }
        }, getActivity());

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

        private static SimpleDateFormat sFormatStartTime;

        private static SimpleDateFormat sFormatEndTime;

        private ItemClickListener mListener;

        private interface ItemClickListener {

            public void onItemClicked(Program program);

        }

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public static class ViewHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener {

            class IconFetcherTask extends AsyncTask<Program, Void, Drawable> {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();

                    // 横画面の時はアイコン無し
                    if (null != mImageView) {
                        mImageView.setVisibility(View.GONE);
                        mImageView.setImageResource(android.R.color.transparent);
                    }
                }

                @Override
                protected Drawable doInBackground(Program... params) {
                    Program p = params[0];
                    Bitmap icon = p.getSymbolIcon(mContext);

                    if (null != icon) {
                        return new BitmapDrawable(mContext.getResources(), icon);
                    } else {
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Drawable iconData) {
                    super.onPostExecute(iconData);
                    if (null != iconData && null != mImageView) {
                        mImageView.setVisibility(View.VISIBLE);
                        mImageView.setImageDrawable(iconData);
                    }
                }
            }

            private final TextView mStartTime;
            private final TextView mEndTime;
            private final TextView mTitle;
            private final TextView mPersonalities;
            private final ImageView mImageView;

            private Program mProgram;

            private final Context mContext;

            private ItemClickListener mListener;

            private IconFetcherTask mIconFetcherTask;

            public ViewHolder(View view, Context context, ItemClickListener listener) {
                super(view);
                view.setOnClickListener(this);

                mStartTime = (TextView) view.findViewById(R.id.program_list_item_starttime);
                mEndTime = (TextView) view.findViewById(R.id.program_list_item_endtime);
                mTitle = (TextView) view.findViewById(R.id.program_list_item_title);
                mPersonalities = (TextView) view.findViewById(R.id.program_list_item_personality);
                mImageView = (ImageView) view.findViewById(R.id.program_list_item_image);

                mContext = context;
                mListener = listener;
            }

            void replace(Program program) {
                mProgram = program;

                mStartTime.setText(sFormatStartTime.format(new Date(program.startTime.getTimeInMillis())));
                mEndTime.setText(sFormatEndTime.format(new Date(program.endTime.getTimeInMillis())));
                mTitle.setText(program.title);
                mPersonalities.setText(program.personalities);

                if (null != mIconFetcherTask &&
                        mIconFetcherTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
                    mIconFetcherTask.cancel(true);
                }
                mIconFetcherTask = new IconFetcherTask();
                mIconFetcherTask.execute(mProgram);
            }

            @Override
            public void onClick(View v) {
                if (null != mProgram) {
                    mListener.onItemClicked(mProgram);
                }
            }
        }

        public TimeTableListAdapter(List<Program> programs, ItemClickListener listener, Context context) {
            mPrograms = programs;
            mListener = listener;

            if (null == sFormatStartTime || null == sFormatEndTime) {
                sFormatStartTime = new SimpleDateFormat(
                        context.getString(R.string.onair_start_time), Locale.US);
                sFormatEndTime = new SimpleDateFormat(
                        context.getString(R.string.onair_end_time), Locale.US);
            }
        }

        // Create new views (invoked by the layout manager)
        @Override
        public TimeTableListAdapter.ViewHolder onCreateViewHolder(
                ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.program_list_item_card, parent, false);

            ViewHolder vh = new ViewHolder(v, parent.getContext(), mListener);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.replace(mPrograms.get(position));
        }

        @Override
        public int getItemCount() {
            return mPrograms.size();
        }
    }

}