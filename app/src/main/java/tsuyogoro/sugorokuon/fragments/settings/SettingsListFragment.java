package tsuyogoro.sugorokuon.fragments.settings;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.tagmanager.Container;
import com.google.android.gms.tagmanager.ContainerHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.SugorokuonApplication;
import tsuyogoro.sugorokuon.network.gtm.ContainerHolderSingleton;
import tsuyogoro.sugorokuon.network.gtm.SugorokuonTagManagerWrapper;

public class SettingsListFragment extends ListFragment {

    public SettingsListFragment() {
        super();
    }

    private boolean mIsDistributionServerAvailable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // GoogleAnalytics tracking
        Tracker t = ((SugorokuonApplication) getActivity().getApplication()).getTracker();
        t.setScreenName(getClass().getSimpleName());
        t.send(new HitBuilders.AppViewBuilder().build());

        mIsDistributionServerAvailable =
                SugorokuonTagManagerWrapper.getDistributionServerAvailable();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        List<String> titles = new ArrayList<>();
        String[] titlesStr = new String[] {
                getString(R.string.settings_header_area_title),
                getString(R.string.settings_auto_update),
                getString(R.string.settings_header_keyword_title),
                getString(R.string.settings_header_remindtiming_title)
        };
        titles.addAll(Arrays.asList(titlesStr));

        List<String> summaries = new ArrayList<>();
        String[] summariesStr = new String [] {
                getString(R.string.settings_header_area_summary),
                getString(R.string.settings_auto_update_summary),
                getString(R.string.settings_header_keyword_summary),
                getString(R.string.settings_header_remindtiming_summary)
        };
        summaries.addAll(Arrays.asList(summariesStr));

        if (mIsDistributionServerAvailable) {
            titles.add("NHK設定");
            summaries.add("NHKラジオ局の地域設定を行います");
        }

        super.onViewCreated(view, savedInstanceState);

        final List<Map<String, String>> options = new ArrayList<Map<String, String>>();

        final String keyTitle = "title";
        final String keySummary = "summary";

        for (int i=0; i < titles.size(); i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put(keyTitle, titles.get(i));
            map.put(keySummary, summaries.get(i));
            options.add(map);
        }

        setListAdapter(new SimpleAdapter(
                getActivity(),
                options,
                android.R.layout.simple_list_item_2,
                new String[] {keyTitle, keySummary},
                new int[] { android.R.id.text1, android.R.id.text2 }
        ));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Fragment f = null;
        String fragmentTag = "";

        switch (position) {
            case 0:
                f = new AreaSettingPreferenceFragment();
                fragmentTag = "AreaSettings";
                break;
            case 1:
                f = new AutoUpdatePreferenceFragment();
                fragmentTag = "AutoUpdateSettings";
                break;
            case 2:
                f = new RecommendWordPreferenceFragment();
                fragmentTag = "RecommendWordsSettings";
                break;
            case 3:
                f = new ReminderSettingFragment();
                fragmentTag = "ReminderSettings";
                break;
            // NHKの設定がenableの時のみlistに表示されて選択可能
            case 4:
                f = new NhkAreaSettingsFragment();
                fragmentTag = "NHKAreaSettings";
        }

        if (null != f) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.setting_fragment_container, f, fragmentTag);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}
