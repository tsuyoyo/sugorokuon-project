package tsuyogoro.sugorokuon.songs

import android.app.SearchManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import tsuyogoro.sugorokuon.R
import tsuyogoro.sugorokuon.SugorokuonApplication
import tsuyogoro.sugorokuon.api.response.FeedResponse
import tsuyogoro.sugorokuon.constant.SearchSongMethod
import tsuyogoro.sugorokuon.utils.SugorokuonLog
import javax.inject.Inject

class OnAirSongsFragment : Fragment(), OnAirSongsSearchDialog.OnAirSongsSearchDialogHost {

    @Inject
    lateinit var viewModelFactory: OnAirSongsViewModel.Factory

    private val songsList: RecyclerView
        get() = view!!.findViewById(R.id.songs_list)

    private val swipeRefreshLayout: SwipeRefreshLayout
        get() = view!!.findViewById(R.id.swipe_refresh_layout)

    private val disposables = CompositeDisposable()

    private lateinit var onAirSongsAdapter: OnAirSongsAdapter

    private lateinit var viewModel: OnAirSongsViewModel

    companion object {
        private const val KEY_STATION_ID: String = "key_station_id"

        private const val TAG_SEARCH_DIALOG = "searchDialog"

        fun createInstance(stationId: String): OnAirSongsFragment {
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
        assert(stationId.isNotBlank(), { SugorokuonLog.e("Station id is null") })

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
        onAirSongsAdapter = OnAirSongsAdapter(object : OnAirSongsAdapter.OnAirSongsItemListener {
            override fun onSearchSong(song: FeedResponse.Song) {
                viewModel.search(song)
            }
        })

        swipeRefreshLayout.setOnRefreshListener {
            val stationId = arguments?.getString(KEY_STATION_ID) ?: ""
            assert(stationId.isNotBlank(), { SugorokuonLog.e("Station id is null") })
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

    override fun onResume() {
        super.onResume()
        disposables.addAll(
                viewModel.observeSignalSearchCopyText()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onCopySongAndArtist),

                viewModel.observeSignalSearchOnPlayer()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onSearchByPlayers),

                viewModel.observeSignalSearchOnGoogle()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onSearchByGoogle),

                viewModel.observeSignalShowSearchDialog()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::showSearchDialog)
        )
    }

    override fun onPause() {
        disposables.clear()
        super.onPause()
    }

    override fun onSaveSettings(isSave: Boolean, searchSongMethod: SearchSongMethod) {
        viewModel.saveSearchSettings(isSave, searchSongMethod)
    }

    override fun onSearchByPlayers(song: FeedResponse.Song) {
        context?.let {
            it.startActivity(Intent().apply {
                action = MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH
                putExtra(MediaStore.EXTRA_MEDIA_FOCUS, "vnd.android.cursor.item/*")
                putExtra(MediaStore.EXTRA_MEDIA_TITLE, song.title)
                putExtra(MediaStore.EXTRA_MEDIA_ARTIST, song.artist)
                putExtra(SearchManager.QUERY, song.title)
            })
        }
    }

    override fun onSearchByGoogle(song: FeedResponse.Song) {
        val intent = Intent(Intent.ACTION_WEB_SEARCH)
        intent.putExtra("query", song.artist + " " + song.title)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context?.startActivity(intent)
    }

    override fun onCopySongAndArtist(song: FeedResponse.Song) {
        context?.let {
            val clipboard = it.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            clipboard?.primaryClip = ClipData.newPlainText(
                    "Copied Text", "${song.title} ${song.artist}")

            Toast.makeText(context, R.string.copy_on_song_title, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showSearchDialog(song: FeedResponse.Song) {
        val dialog = OnAirSongsSearchDialog.createDialog(song)
        dialog.setTargetFragment(this, 0)
        dialog.show(fragmentManager, TAG_SEARCH_DIALOG)
    }

}