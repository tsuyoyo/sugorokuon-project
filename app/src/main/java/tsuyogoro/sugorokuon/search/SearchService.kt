package tsuyogoro.sugorokuon.search

import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import tsuyogoro.sugorokuon.constant.Area
import tsuyogoro.sugorokuon.radiko.SearchUuidGenerator
import tsuyogoro.sugorokuon.radiko.api.SearchApi
import tsuyogoro.sugorokuon.radiko.api.response.SearchResponse
import java.net.URLEncoder

class SearchService(
    private val searchApi: SearchApi,
    private val searchUuidGenerator: SearchUuidGenerator,
    private val searchResponseRepository: SearchResponseRepository
) {

    /**
     * Search programs for each areas.
     * Results are updated & emitted every time when it would get search results.
     * Searching all areas is done, the stream will be completed (onComplete should be called).
     */
    fun search(keyword: String, areas: Collection<Area>): Completable =
            makeAreasStream(areas.toList())
                    .doOnSubscribe { searchResponseRepository.clear() }
                    .flatMapCompletable {
                        searchApi
                                .search(
                                        encodedWord = URLEncoder.encode(keyword, "UTF-8"),
                                        areaId = it.id,
                                        culAreaId = it.id,
                                        uid = searchUuidGenerator.generateSearchUuid()
                                )
                                .doOnSuccess(searchResponseRepository::add)
                                .ignoreElement()
                    }

    fun observeSearchResults(): Flowable<List<SearchResponse.Program>> =
            searchResponseRepository
                    .observeSearchResults()
                    .map {
                        mutableListOf<SearchResponse.Program>().apply {
                            it.forEach { r -> addAll(r.programs) }
                        }
                    }

    private fun makeAreasStream(areas: List<Area>): Flowable<Area> =
            Flowable.create<Area>(
                    { emitter ->
                        areas.forEach(emitter::onNext)
                        emitter.onComplete()
                    }, BackpressureStrategy.LATEST)
}