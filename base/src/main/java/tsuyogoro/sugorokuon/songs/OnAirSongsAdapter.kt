package tsuyogoro.sugorokuon.songs

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.common.wrappers.InstantApps
import tsuyogoro.sugorokuon.base.R
import tsuyogoro.sugorokuon.radiko.api.response.FeedResponse
import java.text.SimpleDateFormat
import java.util.*

class OnAirSongsAdapter(private val listener: OnAirSongsItemListener)
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
        private val adView: AdView
            get() = itemView.findViewById(R.id.ad_view)

        init {
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
        private val thumbnail: ImageView
            get() = itemView.findViewById(R.id.song_thumbnail)

        private val onAirDate: TextView
            get() = itemView.findViewById(R.id.onair_date)

        private val title: TextView
            get() = itemView.findViewById(R.id.title)

        private val artist: TextView
            get() = itemView.findViewById(R.id.artist)

        private val searchOnLibraryBtn: TextView
            get() = itemView.findViewById(R.id.search_on_library)

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

            if (InstantApps.isInstantApp(itemView.context)) {
                searchOnLibraryBtn.visibility = View.GONE
            }
            searchOnLibraryBtn.setOnClickListener {
                listener.onSearchSong(song)
            }
        }
    }
}