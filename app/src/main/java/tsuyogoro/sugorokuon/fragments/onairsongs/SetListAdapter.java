/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.fragments.onairsongs;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tsuyogoro.sugorokuon.models.entities.OnAirSong;

class SetListAdapter extends BaseAdapter {

    private final List<OneDaySetList> mData = new ArrayList<>();

    public SetListAdapter () {
    }

    public void addSong(OnAirSong song) {
        OneDaySetList setList = null;

        int year = song.date.get(Calendar.YEAR);
        int month = song.date.get(Calendar.MONTH) + 1;
        int date = song.date.get(Calendar.DATE);

        for (OneDaySetList s : mData) {
            if (s.year == year && s.month == month && s.date == date) {
                setList = s;
                break;
            }
        }

        if (null == setList) {
            setList = new OneDaySetList(year, month, date);
            mData.add(setList);
            Collections.sort(mData, new Comparator<OneDaySetList>() {
                @Override
                public int compare(OneDaySetList lhs, OneDaySetList rhs) {
                    Calendar l = Calendar.getInstance();
                    l.set(lhs.year, lhs.month - 1, lhs.date);

                    Calendar r = Calendar.getInstance();
                    r.set(rhs.year, rhs.month - 1, rhs.date);

                    // 降順に並べたい
                    return r.compareTo(l);
                }
            });
        }

        setList.addSong(song);
    }

    @Override
    public int getCount() {
        int count = 0;
        for (OneDaySetList setList : mData) {
            count += setList.getListSize();
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        Map<Integer, Integer> index = calculateIndexForView(position);
        return mData.get(index.get(0)).getSong(index.get(1));
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Map<Integer, Integer> index = calculateIndexForView(position);

        int indexDay = index.get(0);
        int indexInTheDay = index.get(1);
        return mData.get(indexDay).getView(indexInTheDay, convertView, parent, parent.getContext());
    }

    @Override
    public boolean isEnabled(int position) {
        Map<Integer, Integer> index = calculateIndexForView(position);
        return mData.get(index.get(0)).isEnabled(index.get(1));
    }

    // (key, value) = (0, dataのindex), (1, dataの中のposition)
    private Map<Integer, Integer> calculateIndexForView(int position) {
        Map<Integer,Integer> res = null;

        // data[]でサイズが {3, 4, 5...} として、
        //   positionが9だった場合、data[2]の[2] (3つめ) が欲しい
        //   positionが6だった場合、data[1]の[3] (4つめ) が欲しい
        int p = position;
        for (int i = 0; i < mData.size(); i++) {
            if (p > (mData.get(i).getListSize() - 1)) {
                p -= mData.get(i).getListSize();
            } else {
                res = new HashMap<Integer, Integer>();
                res.put(0, i);
                res.put(1, p);
                break;
            }
        }
        return res;
    }

}
