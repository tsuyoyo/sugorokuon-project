package tsuyogoro.sugorokuon.v3.songs

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import tsuyogoro.sugorokuon.R
import tsuyogoro.sugorokuon.SugorokuonApplication
import javax.inject.Inject

class OnAirSongsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: OnAirSongsViewModel.Factory

    @BindView(R.id.songs_list)
    lateinit var songsList: RecyclerView

    private lateinit var onAirSongsAdapter: OnAirSongsAdapter

    private lateinit var viewModel: OnAirSongsViewModel

    private lateinit var onAirSongsData: OnAirSongsData


    // TODO : コレダメ。回転した時にonAirSongDataが入ってないからクラッシュする
    fun setOnAirSongsData(onAirSongsData: OnAirSongsData) {
        this.onAirSongsData = onAirSongsData
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SugorokuonApplication.application(context)
                .appComponent()
                .onAirSongsSubComponent(OnAirSongsModule(onAirSongsData))
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_on_air_songs, container, false)


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view!!)

        viewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(OnAirSongsViewModel::class.java)

        onAirSongsAdapter = OnAirSongsAdapter().apply {
            setOnAirSongsData(onAirSongsData)
        }

        songsList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = onAirSongsAdapter
            adapter.notifyDataSetChanged()
        }
    }

}