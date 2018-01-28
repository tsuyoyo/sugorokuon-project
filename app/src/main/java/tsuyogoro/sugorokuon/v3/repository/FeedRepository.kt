package tsuyogoro.sugorokuon.v3.repository

import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import tsuyogoro.sugorokuon.v3.api.response.FeedResponse
import tsuyogoro.sugorokuon.v3.api.response.StationResponse

class FeedRepository(
        private val responses: BehaviorProcessor<Map<String, FeedResponse>> =
                BehaviorProcessor.createDefault(emptyMap())
) {

    fun set(station: StationResponse.Station, response: FeedResponse) {
        val values = responses.value.toMutableMap()
        values[station.id] = response
        responses.onNext(values)
    }

    fun clear() {
        responses.onNext(emptyMap())
    }

    fun observeFeeds() : Flowable<Map<String, FeedResponse>> = responses.hide()

}