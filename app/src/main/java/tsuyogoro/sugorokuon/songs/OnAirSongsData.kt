package tsuyogoro.sugorokuon.songs

import tsuyogoro.sugorokuon.api.response.FeedResponse
import tsuyogoro.sugorokuon.api.response.StationResponse

data class OnAirSongsData(
        val station: StationResponse.Station,
        val songs: List<FeedResponse.Song>
)
