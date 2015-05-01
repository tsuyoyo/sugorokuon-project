/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.fragments.timetable;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.List;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.SugorokuonApplication;
import tsuyogoro.sugorokuon.models.apis.ProgramSearchKeywordFilter;
import tsuyogoro.sugorokuon.models.apis.TimeTableApi;
import tsuyogoro.sugorokuon.models.entities.Program;

public class SearchFragment extends SearchFragmentBase
        implements SearchView.OnQueryTextListener {

    /**
     * 起動時にこのkeyで検索ワードを渡すと、検索まで走る (ようにする←まだ出来てない)
     *
     */
    public static String PARAM_KEY_SEARCH_WORD = "key_search_word";

    private static final String KEY_TASK_PARAM_SEARCH_WORD = "key_task_param_search_word";

    private SearchView mSearchView;

    private MenuItem mMenuItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle("");

        // GoogleAnalytics tracking
        Tracker t = ((SugorokuonApplication) getActivity().getApplication()).getTracker();
        t.setScreenName(getClass().getSimpleName());
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // Menuの設定
        inflater.inflate(R.menu.search, menu);

        mMenuItem = menu.findItem(R.id.menu_search_view);

        mSearchView = (SearchView) mMenuItem.getActionView();
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setSubmitButtonEnabled(false);

        String keyword = "";
        if (null != getArguments()) {
            keyword = getArguments().getString(PARAM_KEY_SEARCH_WORD, "");
        }

//        mSearchView.setQuery(keyword, false);

//        if (!this.searchWord.equals("")) {
//            // TextView.setTextみたいなもの
//            this.searchView.setQuery(this.searchWord, false);
//        } else {
//            String queryHint = self.getResources().getString(R.string.search_menu_query_hint_text);
//            // placeholderみたいなもの
//            this.searchView.setQueryHint(queryHint);
//        }
        mSearchView.setOnQueryTextListener(this);

        mSearchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mSearchView.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus){
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getActivity().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        mSearchView.setFocusable(true);
        mSearchView.setIconified(false);
        mSearchView.requestFocusFromTouch();
    }

    @Override
    protected List<Program> doSearch(Bundle args) {
        String keyword = args.getString(KEY_TASK_PARAM_SEARCH_WORD);

        TimeTableApi timeTableApi = new TimeTableApi(getActivity());
        return timeTableApi.search(
                new ProgramSearchKeywordFilter(new String[] { keyword } ));
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        Bundle params = new Bundle();
        params.putString(KEY_TASK_PARAM_SEARCH_WORD, s);
        getLoaderManager().restartLoader(0, params, this);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

}
