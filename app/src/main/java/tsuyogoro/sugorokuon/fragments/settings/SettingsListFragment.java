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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.SugorokuonApplication;

public class SettingsListFragment extends ListFragment {

    public SettingsListFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // GoogleAnalytics tracking
        Tracker t = ((SugorokuonApplication) getActivity().getApplication()).getTracker();
        t.setScreenName(getClass().getSimpleName());
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        final String[] titles = new String [] {
                getString(R.string.settings_header_area_title),
                getString(R.string.settings_auto_update),
                getString(R.string.settings_header_keyword_title),
                getString(R.string.settings_header_remindtiming_title)
//                ,
//                getString(R.string.settings_browser_cache_settings_title)
        };

        final String[] summaries = new String [] {
                getString(R.string.settings_header_area_summary),
                getString(R.string.settings_auto_update_summary),
                getString(R.string.settings_header_keyword_summary),
                getString(R.string.settings_header_remindtiming_summary)
//                ,
//                getString(R.string.settings_browser_cache_settings_summary)
        };

        super.onViewCreated(view, savedInstanceState);

        final List<Map<String, String>> options = new ArrayList<Map<String, String>>();

        final String keyTitle = "title";
        final String keySummary = "summary";

        for (int i=0; i < titles.length; i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put(keyTitle, titles[i]);
            map.put(keySummary, summaries[i]);
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
//            case 4:
//                f = new BrowserCacheSettingsPreferenceFragment();
//                fragmentTag = "BrowseCacheSettings";
//                break;
        }

        if (null != f) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.setting_fragment_container, f, fragmentTag);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}
