package tsuyogoro.sugorokuon.songs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.RadioButton
import tsuyogoro.sugorokuon.base.R
import tsuyogoro.sugorokuon.constant.SearchSongMethod
import tsuyogoro.sugorokuon.radiko.api.response.FeedResponse

class OnAirSongsSearchDialog : androidx.fragment.app.DialogFragment() {

    interface OnAirSongsSearchDialogHost {
        fun onSaveSettings(isSave: Boolean, searchSongMethod: SearchSongMethod)

        fun onSearchByPlayers(song: FeedResponse.Song)

        fun onSearchByGoogle(song: FeedResponse.Song)

        fun onCopySongAndArtist(song: FeedResponse.Song)
    }

    companion object {
        private const val KEY_SONG = "song"

        fun createDialog(song: FeedResponse.Song) =
                OnAirSongsSearchDialog().apply {
                    arguments = Bundle().apply {
                        putSerializable(KEY_SONG, song)
                    }
                }
    }

    private lateinit var contentView: View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        context?.let {
            val inflater = LayoutInflater.from(context)
            contentView = inflater.inflate(R.layout.dialog_search_way_select, null)

            return AlertDialog.Builder(it)
                    .setTitle(R.string.select_search_way_title)
                    .setView(contentView)
                    .setPositiveButton(R.string.ok) { dialog, which ->
                        (arguments?.getSerializable(KEY_SONG) as? FeedResponse.Song)
                                ?.let {
                                    val isSave = contentView
                                            .findViewById<CheckBox>(R.id.check_save_default)
                                            .isChecked
                                    onSearch(it, isSave)
                                }
                    }
                    .setCancelable(true)
                    .create()
        }
        return super.onCreateDialog(savedInstanceState)
    }

    private fun onSearch(song: FeedResponse.Song, isSaveSettings: Boolean) {
        val host = (targetFragment as? OnAirSongsSearchDialogHost) ?: return

        if (contentView.findViewById<RadioButton>(R.id.search_on_players).isChecked) {
            host.onSearchByPlayers(song)
            host.onSaveSettings(isSaveSettings, SearchSongMethod.SEARCH_ON_PLAYER)
        } else if (contentView.findViewById<RadioButton>(R.id.search_on_google).isChecked) {
            host.onSearchByGoogle(song)
            host.onSaveSettings(isSaveSettings, SearchSongMethod.SEARCH_ON_GOOGLE)
        } else if (contentView.findViewById<RadioButton>(R.id.copy_clip_board).isChecked) {
            host.onCopySongAndArtist(song)
            host.onSaveSettings(isSaveSettings, SearchSongMethod.COPY_TITLE_CLIP_BOARD)
        }
    }
}