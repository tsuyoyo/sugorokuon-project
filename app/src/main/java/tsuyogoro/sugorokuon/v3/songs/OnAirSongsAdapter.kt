package tsuyogoro.sugorokuon.v3.songs

import android.app.SearchManager
import android.content.Intent
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import tsuyogoro.sugorokuon.R
import tsuyogoro.sugorokuon.v3.api.response.FeedResponse
import java.text.SimpleDateFormat
import java.util.*

class OnAirSongsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onAirSongs: List<FeedResponse.Song> = emptyList()

    fun setOnAirSongsData(onAirSongs: List<FeedResponse.Song>) {
        this.onAirSongs = onAirSongs
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? OnAirSongsViewHolder)?.setSong(onAirSongs[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            OnAirSongsViewHolder(parent)

    override fun getItemCount(): Int = onAirSongs.size

    class OnAirSongsViewHolder(parent: ViewGroup?) : RecyclerView.ViewHolder(
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
            }
            onAirDate.text =
                    SimpleDateFormat(
                            itemView.resources.getString(R.string.date_hhmm),
                            Locale.JAPAN
                    ).format(Date(song.stamp.timeInMillis))
            title.text = song.title
            artist.text = song.artist

            searchOnLibraryBtn.setOnClickListener {
                (itemView.context as? AppCompatActivity)?.let {
                    it.startActivity(Intent().apply {
                        action = MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH
                        putExtra(MediaStore.EXTRA_MEDIA_FOCUS, "vnd.android.cursor.item/*")
                        putExtra(MediaStore.EXTRA_MEDIA_TITLE, song.title)
                        putExtra(MediaStore.EXTRA_MEDIA_ARTIST, song.artist)
                        putExtra(SearchManager.QUERY, song.title)
                    })
                }
            }
        }
    }
}