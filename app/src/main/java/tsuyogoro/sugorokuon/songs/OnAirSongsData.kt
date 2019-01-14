package tsuyogoro.sugorokuon.songs

import tsuyogoro.sugorokuon.radiko.api.response.FeedResponse
import tsuyogoro.sugorokuon.radiko.api.response.StationResponse

data class OnAirSongsData(
    val station: StationResponse.Station,
    val songs: List<FeedResponse.Song>
)
