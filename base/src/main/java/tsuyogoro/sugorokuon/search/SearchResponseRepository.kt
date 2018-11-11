package tsuyogoro.sugorokuon.search

import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import tsuyogoro.sugorokuon.radiko.api.response.SearchResponse

class SearchResponseRepository(
        private val searchResponses: BehaviorProcessor<List<SearchResponse>> =
                BehaviorProcessor.createDefault(emptyList())
) {
    fun add(searchResponse: SearchResponse) {
        searchResponses.onNext(
                searchResponses.value?.toMutableList().apply {
                    add(searchResponse)
                } ?: return
        )
    }

    fun clear() {
        searchResponses.onNext(emptyList())
    }

    fun observeSearchResults(): Flowable<List<SearchResponse>> = searchResponses.hide()
}