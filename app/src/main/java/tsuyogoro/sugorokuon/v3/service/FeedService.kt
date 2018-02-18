package tsuyogoro.sugorokuon.v3.service

import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.functions.BiFunction
import tsuyogoro.sugorokuon.v3.api.FeedApi
import tsuyogoro.sugorokuon.v3.api.response.FeedResponse
import tsuyogoro.sugorokuon.v3.api.response.StationResponse
import tsuyogoro.sugorokuon.v3.model.SugorokuonAppState
import tsuyogoro.sugorokuon.v3.repository.FeedRepository

class FeedService(
        private val stationService: StationService,
        private val feedApi: FeedApi,
        private val feedRepository: FeedRepository,
        private val appState: SugorokuonAppState
) {
    fun fetchFeeds(stationIds: List<String>): Completable = Flowable
            .create<String>({ emitter ->
                stationIds.forEach(emitter::onNext)
                emitter.onComplete()
            }, BackpressureStrategy.LATEST)
            .doOnSubscribe {
                appState.setIsLoadingFeed(true)
                feedRepository.clear()
            }
            .flatMapCompletable(this::callFeedApi)
            .doOnComplete { appState.setIsLoadingFeed(false) }
            .doOnError { appState.setIsLoadingFeed(false) }

    fun fetchFeed(stationId: String): Completable =
            Maybe.just(stationId)
                    .flatMapCompletable(this::callFeedApi)
                    .doOnSubscribe { appState.setIsLoadingFeed(true) }
                    .doOnComplete { appState.setIsLoadingFeed(false) }


    private fun callFeedApi(stationId: String): Completable =
            feedApi.getFeed(stationId)
                    .doOnSuccess { res -> feedRepository.set(stationId, res) }
                    .ignoreElement()
                    .onErrorResumeNext { Completable.complete() }

    fun observeFeedAvailableStations(): Flowable<List<StationResponse.Station>> =
            Flowable.combineLatest(
                    stationService.observeStations(),
                    feedRepository.observeFeeds(),
                    BiFunction {
                        stations: List<StationResponse.Station>,
                        feeds : Map<String, FeedResponse> ->
                        return@BiFunction feeds.mapNotNull {
                            stations.find { s -> s.id == it.key } // key has stationID
                        }
                    }
            )

    fun observeFeeds(): Flowable<Map<String, FeedResponse>> = feedRepository.observeFeeds()

}