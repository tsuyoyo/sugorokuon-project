package tsuyogoro.sugorokuon.v3.songs

import tsuyogoro.sugorokuon.v3.api.response.FeedResponse
import tsuyogoro.sugorokuon.v3.api.response.StationResponse

data class OnAirSongsData(
        val station: StationResponse.Station,
        val songs: List<FeedResponse.Song>
)
