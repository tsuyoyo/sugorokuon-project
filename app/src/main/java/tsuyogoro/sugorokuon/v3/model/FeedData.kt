package tsuyogoro.sugorokuon.v3.model

import tsuyogoro.sugorokuon.v3.api.response.FeedResponse
import tsuyogoro.sugorokuon.v3.api.response.StationResponse

data class FeedData(val station: StationResponse.Station,
                    val feedResponse: FeedResponse)