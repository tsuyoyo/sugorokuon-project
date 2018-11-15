package tsuyogoro.sugorokuon.constant

import android.content.res.Resources
import android.support.annotation.StringRes
import tsuyogoro.sugorokuon.data.R

enum class SearchSongMethod(@StringRes private val nameText: Int) {

    EVERY_TIME_SELECT(R.string.search_song_select_every_time),

    SEARCH_ON_PLAYER(R.string.search_song_by_player),

    SEARCH_ON_GOOGLE(R.string.search_song_by_google),

    COPY_TITLE_CLIP_BOARD(R.string.search_song_by_copy_clip_board),

    ;

    fun getDisplayName(resources: Resources) = resources.getString(nameText)

}