/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.fragments.onairsongs;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.databinding.OnairSongListItemSongBinding;
import tsuyogoro.sugorokuon.models.entities.OnAirSong;

class OneDaySetList {

    private final List<OnAirSong> mSongs = new ArrayList<OnAirSong>();

    public final int year;

    public final int month;

    public final int date;

    public OneDaySetList(int year, int month, int date) {
        this.year = year;
        this.month = month;
        this.date = date;
    }

    /**
     * この日のデータを表示するリストのサイズ
     *
     * @return
     */
    public int getListSize() {
        // 1 は日付のラベル
        int size = 1;

        // データが無かったら "No data" を表示するので +1
        size += (0 < mSongs.size()) ? mSongs.size() : 1;

        return size;
    }

    public View getView(int position, View convertView, ViewGroup parent, Context context) {
        View view;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        // 日付ラベル
        if (0 == position) {
            view = inflater.inflate(R.layout.onair_song_list_item_datelabel, null);

            TextView label = (TextView) view.findViewById(R.id.onair_song_list_date_label);
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    context.getString(R.string.date_mmddeee), Locale.JAPAN);
            Calendar c = Calendar.getInstance();
            c.set(year, month - 1, date);
            label.setText(dateFormat.format(new Date(c.getTimeInMillis())));
        }
        // No dataのラベル
        else if (0 == mSongs.size() && 1 == position) {
            view = inflater.inflate(R.layout.onair_song_list_item_nodata, null);
        }
        // 1曲分の情報
        else {
            OnairSongListItemSongBinding binding;

            if (convertView == null || DataBindingUtil.getBinding(convertView) == null) {
                binding = DataBindingUtil.inflate(
                        inflater, R.layout.onair_song_list_item_song, parent, false);
            } else {
                binding = DataBindingUtil.getBinding(convertView);
            }

            SimpleDateFormat formatter = new SimpleDateFormat(
                    context.getString(R.string.date_hhmm), Locale.JAPAN);

            OnAirSong song = mSongs.get(position - 1);
            binding.setSong(song);
            binding.setDate(formatter.format(new Date(song.date.getTimeInMillis())));

            if (song.imageUrl != null && song.imageUrl.length() > 0) {
                Picasso.with(context).load(song.imageUrl).transform(new CropCircleTransformation())
                        .into(binding.onairSongListImg);
            }

            view = binding.getRoot();
        }

        return view;
    }

    public void addSong(OnAirSong song) {
        mSongs.add(song);

        Collections.sort(mSongs, new Comparator<OnAirSong>() {
            @Override
            public int compare(OnAirSong lhs, OnAirSong rhs) {
                // 降順に並べたいのでこの計算式
                return rhs.date.compareTo(lhs.date);
            }
        });
    }

    /**
     * AdapterのisEnabledに返させる値。
     * falseの場合、そのlist itemはtapできない。
     *
     * @param position
     * @return
     */
    public boolean isEnabled(int position) {
        if (0 == position || (0 == mSongs.size() && 1 == position)) {
            return false;
        } else {
            return true;
        }
    }

    public OnAirSong getSong(int position) {
        if (0 == position || (0 == mSongs.size() && 1 == position)) {
            return null;
        } else {
            return mSongs.get(position - 1);
        }
    }
}
