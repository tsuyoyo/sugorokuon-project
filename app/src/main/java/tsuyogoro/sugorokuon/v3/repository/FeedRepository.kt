package tsuyogoro.sugorokuon.v3.repository

import io.reactivex.Maybe
import retrofit2.HttpException
import tsuyogoro.sugorokuon.v3.api.FeedApi
import tsuyogoro.sugorokuon.v3.api.response.FeedResponse

class FeedRepository(
        private val feedApi: FeedApi,
        private val feedUnavailableStations: MutableList<String> = mutableListOf()
) {
    fun fetchFeed(stationId: String, update: Boolean = false): Maybe<FeedResponse> =
        if (update || feedUnavailableStations.find { it == stationId } == null) {
            feedApi.getFeed(stationId)
                    .doOnError {
                        if (it is HttpException && it.code() == 404) {
                            feedUnavailableStations.add(stationId)
                        }
                    }
        } else {
            Maybe.error(IllegalAccessException("Feed of ${stationId} is unavailable"))
        }
}