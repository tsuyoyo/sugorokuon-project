package tsuyogoro.sugorokuon.songs

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import tsuyogoro.sugorokuon.R
import tsuyogoro.sugorokuon.api.response.FeedResponse
import java.text.SimpleDateFormat
import java.util.*

class OnAirSongsAdapter(private val listener: OnAirSongsAdapter.OnAirSongsItemListener)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnAirSongsItemListener {
        fun onSearchSong(song: FeedResponse.Song)
    }

    private var onAirSongs: List<FeedResponse.Song> = emptyList()

    companion object {
        private const val TYPE_SONG = 1
        private const val TYPE_AD = 2
    }

    fun setOnAirSongsData(onAirSongs: List<FeedResponse.Song>) {
        this.onAirSongs = onAirSongs
    }

    override fun getItemViewType(position: Int): Int =
            if (position > onAirSongs.size - 1) {
                TYPE_AD
            } else {
                TYPE_SONG
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            when (viewType) {
                TYPE_AD -> OnAirSongsAdViewHolder(parent)
                else -> OnAirSongsViewHolder(parent, listener)
            }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position < onAirSongs.size) {
            (holder as? OnAirSongsViewHolder)?.setSong(onAirSongs[position])
        }
    }

    override fun getItemCount(): Int = onAirSongs.size + 1

    class OnAirSongsAdViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_on_air_song_ad, parent, false)
    ) {
        @BindView(R.id.ad_view)
        lateinit var adView: AdView

        init {
            ButterKnife.bind(this, itemView)
            adView.loadAd(AdRequest.Builder().build())
        }
    }

    class OnAirSongsViewHolder(
            parent: ViewGroup?,
            private val listener: OnAirSongsItemListener
    ) : RecyclerView.ViewHolder(
            LayoutInflater
                    .from(parent?.context)
                    .inflate(R.layout.item_on_air_song, parent, false)
    ) {

        @BindView(R.id.song_thumbnail)
        lateinit var thumbnail: ImageView

        @BindView(R.id.onair_date)
        lateinit var onAirDate: TextView

        @BindView(R.id.title)
        lateinit var title: TextView

        @BindView(R.id.artist)
        lateinit var artist: TextView

        @BindView(R.id.search_on_library)
        lateinit var searchOnLibraryBtn: TextView

        init {
            ButterKnife.bind(this, itemView)
        }

        fun setSong(song: FeedResponse.Song) {
            if (song.image != null && song.image.isNotBlank()) {
                Glide.with(itemView).load(song.image).into(thumbnail)
            } else {
                thumbnail.setImageDrawable(
                        itemView.resources
                                .getDrawable(R.drawable.ic_music_note_grey_600_48dp, null)
                )
            }
            onAirDate.text =
                    SimpleDateFormat(
                            itemView.resources.getString(R.string.date_hhmm),
                            Locale.JAPAN
                    ).format(Date(song.stamp.timeInMillis))
            title.text = song.title
            artist.text = song.artist

            searchOnLibraryBtn.setOnClickListener {
                listener.onSearchSong(song)
//                (itemView.context as? AppCompatActivity)?.let {
//                    it.startActivity(Intent().apply {
//                        action = MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH
//                        putExtra(MediaStore.EXTRA_MEDIA_FOCUS, "vnd.android.cursor.item/*")
//                        putExtra(MediaStore.EXTRA_MEDIA_TITLE, song.title)
//                        putExtra(MediaStore.EXTRA_MEDIA_ARTIST, song.artist)
//                        putExtra(SearchManager.QUERY, song.title)
//                    })
//                }
            }
        }
    }
}