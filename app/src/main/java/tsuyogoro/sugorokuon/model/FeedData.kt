package tsuyogoro.sugorokuon.model

import tsuyogoro.sugorokuon.radiko.api.response.FeedResponse
import tsuyogoro.sugorokuon.radiko.api.response.StationResponse

data class FeedData(val station: StationResponse.Station,
                    val feedResponse: FeedResponse)