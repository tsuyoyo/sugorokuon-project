package tsuyogoro.sugorokuon.preference

import com.rejasupotaro.android.kvs.annotations.Key
import com.rejasupotaro.android.kvs.annotations.Table
import com.rejasupotaro.android.kvs.serializers.PrefsSerializer
import tsuyogoro.sugorokuon.constant.SearchSongMethod

@Table(name = "searchMethod")
internal class SearchMethodPrefsSchema {

    @Key(name = "searchSongWay", serializer = SearchSongWaySerializer::class)
    var searchSongWay: String = ""

    class SearchSongWaySerializer : PrefsSerializer<SearchSongMethod, String> {
        override fun serialize(src: SearchSongMethod?): String = src?.name ?: ""

        override fun deserialize(src: String?): SearchSongMethod =
            src?.let{
                try {
                    SearchSongMethod.valueOf(it)
                } catch (e: IllegalArgumentException) {
                    null
                }
            } ?: SearchSongMethod.EVERY_TIME_SELECT
    }
}