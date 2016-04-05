/**
 * Copyright (c)
 * 2016 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.databinding.OnairSongListActivityBinding;
import tsuyogoro.sugorokuon.fragments.onairsongs.WeeklyOnAirSongsFragment;

public class OnAirSongsActivity extends AppCompatActivity {

    private OnairSongListActivityBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.onair_song_list_activity);

        mBinding.onairSongListActivityToolBar.setTitle(R.string.weekly_onair_song_title);

        setSupportActionBar(mBinding.onairSongListActivityToolBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        WeeklyOnAirSongsFragment fragment = new WeeklyOnAirSongsFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.onair_song_list_activity_fragment_area, fragment);

        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
