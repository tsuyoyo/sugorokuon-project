package tsuyogoro.sugorokuon.v3.songs

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import tsuyogoro.sugorokuon.R
import tsuyogoro.sugorokuon.SugorokuonApplication
import tsuyogoro.sugorokuon.utils.SugorokuonLog
import javax.inject.Inject

class OnAirSongsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: OnAirSongsViewModel.Factory

    @BindView(R.id.songs_list)
    lateinit var songsList: RecyclerView

    @BindView(R.id.swipe_refresh_layout)
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var onAirSongsAdapter: OnAirSongsAdapter

    private lateinit var viewModel: OnAirSongsViewModel

    companion object {
        val KEY_STATION_ID: String = "key_station_id"

        fun createInstance(stationId: String) : OnAirSongsFragment {
            return OnAirSongsFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_STATION_ID, stationId)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val stationId = arguments?.getString(KEY_STATION_ID) ?: ""
        assert(stationId.isNotBlank(), { SugorokuonLog.e("Station id is null")})

        SugorokuonApplication.application(context)
                .appComponent()
                .onAirSongsSubComponent(OnAirSongsModule(stationId))
                .inject(this)

        viewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(OnAirSongsViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_on_air_songs, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)
        onAirSongsAdapter = OnAirSongsAdapter()

        swipeRefreshLayout.setOnRefreshListener {
            val stationId = arguments?.getString(KEY_STATION_ID) ?: ""
            assert(stationId.isNotBlank(), { SugorokuonLog.e("Station id is null")})
            viewModel.fetchOnAirSongs(stationId)
        }

        songsList.apply {
            adapter = onAirSongsAdapter
            layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.VERTICAL,
                    false)
        }

        viewModel.observeOnAirSongs()
                .observe(this, Observer {
                    if (it != null) {
                        onAirSongsAdapter.setOnAirSongsData(it)
                        onAirSongsAdapter.notifyDataSetChanged()
                    }
                    swipeRefreshLayout.isRefreshing = false
                })
    }

}