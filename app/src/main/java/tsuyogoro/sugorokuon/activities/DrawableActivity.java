/**
 * Copyright (c)
 * 2016 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import tsuyogoro.sugorokuon.R;

public class DrawableActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mDrawerToggle;

    protected static final int REQUESTCODE_SETTINGS = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.drawable_activity_layout);

        // For AdMob
        final AdView mAdView = (AdView) findViewById(R.id.adView);
        new Runnable() {
            @Override
            public void run() {
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            }
        }.run();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    /*
     * これを、子クラスのinflateの第2引数に使う
     */
    protected RelativeLayout getContentRoot() {
        return (RelativeLayout) findViewById(R.id.drawable_actiity_content_root);
    }

    /*
     * ActionBar (Toolbar) のinflateが終わった後に呼ぶこと
     */
    protected void setupDrawer(boolean isSetToggle) {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawable_activity_drawer_layout);

        if (isSetToggle) {
            mDrawerToggle = new ActionBarDrawerToggle(
                    this, mDrawerLayout, R.string.app_name, R.string.app_name);
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerLayout.addDrawerListener(mDrawerToggle);
        }

        NavigationView navView = (NavigationView) findViewById(R.id.drawable_activity_navigation_view);
        navView.setNavigationItemSelectedListener(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_drawer_menu_recommends:
                startRecommendActivity();
                mDrawerLayout.closeDrawers();
                break;
            case R.id.main_drawer_menu_onair_songs:
                startOnAirSongsActivity();
                mDrawerLayout.closeDrawers();
                break;
            case R.id.main_drawer_menu_settings:
                mDrawerLayout.closeDrawers();
                Intent intentForSettings = new Intent(this, SugorokuonSettingActivity.class);
                startActivityForResult(intentForSettings, REQUESTCODE_SETTINGS);
                if (!isMainActivity()) {
                    finish();
                }
                break;
            case R.id.main_drawer_menu_about:
                break;
            case R.id.main_drawer_menu_rating:
                mDrawerLayout.closeDrawers();
                Intent googlePlayIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.google_play_app_url)));
                googlePlayIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                callStartActivity(googlePlayIntent);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle != null) {
            return mDrawerToggle.onOptionsItemSelected(item)
                    || super.onOptionsItemSelected(item);
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    protected void startOnAirSongsActivity() {
        Intent onAirSongsIntent = new Intent(this, OnAirSongsActivity.class);
        callStartActivity(onAirSongsIntent);
    }

    protected void startRecommendActivity() {
        Intent recommendIntent = new Intent(this, RecommendActivity.class);
        callStartActivity(recommendIntent);
    }

    protected boolean isMainActivity() {
        return false;
    }

    private void callStartActivity(Intent intent)  {
        startActivity(intent);
        if (!isMainActivity()) {
            finish();
        }
    }

}
