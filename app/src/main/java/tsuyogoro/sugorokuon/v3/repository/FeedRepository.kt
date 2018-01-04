package tsuyogoro.sugorokuon.v3.repository

import io.reactivex.Maybe
import tsuyogoro.sugorokuon.v3.api.FeedApi
import tsuyogoro.sugorokuon.v3.api.response.FeedResponse

class FeedRepository(private val feedApi: FeedApi) {

    fun fetchFeed(stationId: String): Maybe<FeedResponse> = feedApi.getFeed(stationId)
}