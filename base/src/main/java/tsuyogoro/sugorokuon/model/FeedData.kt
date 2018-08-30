package tsuyogoro.sugorokuon.model

import tsuyogoro.sugorokuon.api.response.FeedResponse
import tsuyogoro.sugorokuon.api.response.StationResponse

data class FeedData(val station: StationResponse.Station,
                    val feedResponse: FeedResponse)