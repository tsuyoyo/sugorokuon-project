/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.fragments;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Formatter;
import java.util.Locale;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.SugorokuonApplication;
import tsuyogoro.sugorokuon.utils.SugorokuonLog;

public class AboutAppFragment extends ListFragment {

    private static final int INDEX_COPY_RIGHT = 0;
    private static final int INDEX_APP_VERSION = 1;
    private static final int INDEX_APP_HISTORY = 2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // GoogleAnalytics tracking
        Tracker t = ((SugorokuonApplication) getActivity().getApplication()).getTracker();
        t.setScreenName(getClass().getSimpleName());
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // For version info
        StringBuilder versionInfoBuilder = new StringBuilder();
        Formatter formatter = new Formatter(versionInfoBuilder, Locale.US);
        formatter.format(getString(R.string.version_info), getVersionName());
        formatter.close();

        String[] data = {
                getString(R.string.copyright).toString(),
                versionInfoBuilder.toString(),
                getString(R.string.notice_license)
        };

        setListAdapter(new ArrayAdapter<String>(
                getActivity(), R.layout.aboutapp_listitem_layout, data));
    }

    private String getVersionName() {
        String versionName = "";
        try {
            PackageInfo packageInfo = getActivity().getPackageManager().getPackageInfo(
                    getActivity().getPackageName(), PackageManager.GET_ACTIVITIES);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            SugorokuonLog.e(e.getMessage());
        }
        return versionName;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        switch(position) {
            case INDEX_APP_VERSION:
                break;
            case INDEX_COPY_RIGHT:
                break;
            case INDEX_APP_HISTORY:
                break;
        }
    }

//    private void sendMail() {
//        Uri uri = Uri.parse(getString(R.string.developer_mail_addr));
//        Intent mailIntent = new Intent(Intent.ACTION_SENDTO);
//        mailIntent.setPackage(getActivity().getPackageName());
//        mailIntent.setData(uri);
//        mailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
//        getActivity().startActivity(mailIntent);
//    }
}
