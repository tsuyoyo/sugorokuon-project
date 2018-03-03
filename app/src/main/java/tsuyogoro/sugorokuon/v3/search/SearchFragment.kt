package tsuyogoro.sugorokuon.v3.search

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import tsuyogoro.sugorokuon.R
import tsuyogoro.sugorokuon.SugorokuonApplication
import tsuyogoro.sugorokuon.v3.SugorokuonTopActivity
import tsuyogoro.sugorokuon.v3.timetable.ProgramInfoFragment
import javax.inject.Inject


class SearchFragment : Fragment(),
        SearchResultListAdapter.SearchResultListAdapterListener {

    @Inject
    lateinit var viewModelFactory: SearchViewModel.Factory

    @BindView(R.id.search_condition)
    lateinit var searchCondition: TextView

    @BindView(R.id.search_results)
    lateinit var searchResults: RecyclerView

    @BindView(R.id.swipe_refresh_layout)
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    lateinit var searchResultListAdapter: SearchResultListAdapter

    lateinit var searchView: SearchView

    private lateinit var viewModel: SearchViewModel

    private val disposables : CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SugorokuonApplication
                .application(context)
                .appComponent()
                .searchSubComponent(SearchModule())
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_search_result, null)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)

        // Not to allow swipe to refresh on search result list
        swipeRefreshLayout.isEnabled = false

        searchResultListAdapter = SearchResultListAdapter(this)
        searchResults.adapter = searchResultListAdapter
        searchResults.layoutManager = LinearLayoutManager(
                context, LinearLayoutManager.VERTICAL, false)

        viewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(SearchViewModel::class.java)

        viewModel.observeSearchCondition()
                .observe(this, Observer {
                    if (it == null || it.isEmpty()) {
                        searchCondition.visibility = View.GONE
                    } else {
                        searchCondition.visibility = View.VISIBLE
                        searchCondition.text = it
                    }
                })

        viewModel.observeIsSearching()
                .observe(this, Observer {
                    if (it != null) {
                        swipeRefreshLayout.isRefreshing = it
                    }
                })

        viewModel.observeSearchError()
                .observe(this, Observer {
                    // TODO : error handling
                })

        viewModel.observeSearchResults()
                .observe(this, Observer {
                    if (it == null) {
                        searchResultListAdapter.setSearchResults(emptyList())
                    } else {
                        searchResultListAdapter.setSearchResults(it)
                    }
                    searchResultListAdapter.notifyDataSetChanged()
                })

        setupHandleBackKey(view)

        // To make sure to be ready for search input.
        if (savedInstanceState == null) {
            (activity as? SugorokuonTopActivity)?.setFocusOnSearchForm()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        val disposable = (context as? SugorokuonTopActivity)
                ?.observeSearchKeyword()
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe {
                    viewModel.search(it)
                    closeKeyboard()
                }
        if (disposable != null) {
            disposables.add(disposable)
        }
    }

    private fun setupHandleBackKey(view: View?) {
        view?.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_DOWN) {
                fragmentManager?.popBackStack()
                true
            } else {
                false
            }
        }
        view?.isFocusableInTouchMode = true
        view?.requestFocus()
    }

    override fun onSearchResultClicked(searchResult: SearchViewModel.SearchResultData) {
        (activity as? SugorokuonTopActivity)?.pushFragment(
                ProgramInfoFragment.createInstance(searchResult.program),
                ProgramInfoFragment.FRAGMENT_TAG
        )
    }

    private fun closeKeyboard() {
        (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
                ?.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}