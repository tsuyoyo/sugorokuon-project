package tsuyogoro.sugorokuon.repository

import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import tsuyogoro.sugorokuon.radiko.api.response.FeedResponse

class FeedRepository(
        private val responses: BehaviorProcessor<Map<String, FeedResponse>> =
                BehaviorProcessor.createDefault(emptyMap())
) {

    fun set(stationId: String, response: FeedResponse) {
        val values = responses.value.toMutableMap()
        values[stationId] = response
        responses.onNext(values)
    }

    fun clear() {
        responses.onNext(emptyMap())
    }

    fun observeFeeds() : Flowable<Map<String, FeedResponse>> = responses.hide()

}