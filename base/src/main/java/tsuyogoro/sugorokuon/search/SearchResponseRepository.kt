package tsuyogoro.sugorokuon.search

import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import tsuyogoro.sugorokuon.radiko.api.response.SearchResponse

class SearchResponseRepository(
        private val searchResponses: BehaviorProcessor<List<SearchResponse>> =
                BehaviorProcessor.createDefault(emptyList())
) {
    fun add(searchResponse: SearchResponse) {
        val list = searchResponses.value?.toMutableList() ?: return
        list.add(searchResponse)
        searchResponses.onNext(list)
    }

    fun clear() {
        searchResponses.onNext(emptyList())
    }

    fun observeSearchResults(): Flowable<List<SearchResponse>> = searchResponses.hide()
}