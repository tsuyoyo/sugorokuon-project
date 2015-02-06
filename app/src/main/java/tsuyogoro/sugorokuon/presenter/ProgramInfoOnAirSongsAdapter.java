/**
 * Copyright (c) 
 * 2013 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.presenter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.datatype.Feed;
import tsuyogoro.sugorokuon.datatype.OnAirSong;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

class ProgramInfoOnAirSongsAdapter extends BaseAdapter {

    private Context mContext;

    private Feed mFeed;

    public ProgramInfoOnAirSongsAdapter(Context context) {
        super();
        mContext = context;
    }

    @Override
    public int getCount() {
        if(null == mFeed) {
            return 0;
        } else {
            return mFeed.onAirSongs.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if(null == mFeed) {
            return null;
        } else {
            return mFeed.onAirSongs.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        OnAirSong song = mFeed.onAirSongs.get(position);

        // Item‚ðinflate
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.program_info_viewer_onair_listitem, null);

        // OnAir‚ÌŽžŠÔ
        SimpleDateFormat formatStart = new SimpleDateFormat(
                mContext.getString(R.string.onair_start_time), Locale.US);
        TextView onAirText = (TextView) itemView.findViewById(R.id.onair_song_list_time);
        onAirText.setText(formatStart.format(new Date(song.date.getTimeInMillis())));

        // Title
        TextView titleText = (TextView) itemView.findViewById(R.id.onair_song_list_song_title);
        titleText.setText(song.title);

        // Artist
        TextView artistText = (TextView) itemView.findViewById(R.id.onair_song_list_song_artist);
        artistText.setText(song.artist);

        return itemView;
    }

    public void setFeed(Feed feed) {
        mFeed = feed;
    }

}