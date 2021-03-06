/**
 * Copyright (c)
 * 2016 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.activities;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import net.nend.android.NendAdIconLayout;
import net.nend.android.NendAdIconLoader;

import java.util.List;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.databinding.MainActivityStationListItemBinding;
import tsuyogoro.sugorokuon.models.entities.Station;

public class StationListAdapter extends RecyclerView.Adapter<StationListAdapter.StationListViewHolder> {

    private final List<Station> mStation;

    private IStationListListener mListener;

    public interface IStationListListener {
        void onStationSelected(Station station);

        void onStationLongTapped(Station station);
    }

    public StationListAdapter(List<Station> stations, IStationListListener listener) {
        mStation = stations;
        mListener = listener;
    }

    /**
     * データを置き換えて (古いデータをclearして) notifyする
     *
     * @param stations
     */
    public void update(List<Station> stations) {
        mStation.clear();;
        mStation.addAll(stations);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mStation.size() + 1;
    }

    @Override
    public StationListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.main_activity_station_list_item, parent, false);
        return new StationListViewHolder(v, mListener);
    }

    private NendAdIconLoader mIconLoader;

    @Override
    public void onBindViewHolder(StationListViewHolder holder, int position) {

        // 一番左に広告を表示する
        if (position == 0) {
            holder.getBinding().setIsAdEntry(true);

            if (mIconLoader == null) {
                Context context = holder.getBinding().getRoot().getContext();
                mIconLoader = new NendAdIconLoader(context.getApplicationContext(),
                        context.getResources().getInteger(R.integer.nend_spot_id),
                        context.getResources().getString(R.string.nend_api_key));
            }

            holder.getBinding().nendAdicon.setTitleVisible(false);
            holder.getBinding().nendAdicon.setIconSpaceEnabled(false);

            mIconLoader.addIconView(holder.getBinding().nendAdicon);
            mIconLoader.loadAd();

            holder.station = null;
            holder.getBinding().setStation(null);
            holder.getBinding().mainActivityStationListIcon.setImageDrawable(null);

        } else {

            holder.getBinding().setIsAdEntry(false);

            Station station = mStation.get(position - 1);
            holder.station = station;
            holder.getBinding().setStation(station);
            holder.getBinding().mainActivityStationListIcon.setImageBitmap(
                    BitmapFactory.decodeFile(station.getLogoCachePath()));
        }

    }

    public static class StationListViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private Station station;

        private IStationListListener listener;

        private final MainActivityStationListItemBinding mBinding;

        public StationListViewHolder(View itemView, IStationListListener stationListListener) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setLongClickable(true);
            itemView.setOnLongClickListener(this);
            mBinding = DataBindingUtil.bind(itemView);
            listener = stationListListener;
        }

        public MainActivityStationListItemBinding getBinding() {
            return mBinding;
        }

        @Override
        public void onClick(View v) {
            if (station != null) {
                listener.onStationSelected(station);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (station != null) {
                listener.onStationLongTapped(station);
            }
            return true;
        }
    }
}
