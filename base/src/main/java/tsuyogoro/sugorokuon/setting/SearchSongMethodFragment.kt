package tsuyogoro.sugorokuon.setting

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tsuyogoro.sugorokuon.base.R
import tsuyogoro.sugorokuon.SugorokuonApplication
import tsuyogoro.sugorokuon.constant.SearchSongMethod
import javax.inject.Inject

class SearchSongMethodFragment: androidx.fragment.app.Fragment(),
        SearchSongMethodListAdapter.SearchSongMethodListListener {

    private val methodList: androidx.recyclerview.widget.RecyclerView
        get() = view!!.findViewById(R.id.method_list)

    @Inject
    lateinit var viewModelFactory: SearchSongMethodViewModel.Factory

    private lateinit var viewModel: SearchSongMethodViewModel
    private lateinit var methodListAdapter: SearchSongMethodListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SugorokuonApplication.application(context)
                .appComponent()
                .settingSubComponent(SettingsModule())
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_settings_search_song_method, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        methodListAdapter = SearchSongMethodListAdapter(listener = this)

        methodList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        methodList.adapter = methodListAdapter

        viewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(SearchSongMethodViewModel::class.java)

        methodListAdapter.setSearchSongMethods(viewModel.getOptions())
        methodListAdapter.notifyDataSetChanged()

        viewModel.observeSelectedMethod()
                .observe(this, Observer {
                    if (it != null) {
                        methodListAdapter.setSelectedMethod(it)
                        methodListAdapter.notifyDataSetChanged()
                    }
                })
    }

    override fun onSearchSongMethodSelected(selectedMethod: SearchSongMethod) {
        viewModel.selectSearchSongMethod(selectedMethod)
    }

}