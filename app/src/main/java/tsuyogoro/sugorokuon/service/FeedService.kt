package tsuyogoro.sugorokuon.service

import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.functions.BiFunction
import tsuyogoro.sugorokuon.model.SugorokuonAppState
import tsuyogoro.sugorokuon.radiko.api.FeedApi
import tsuyogoro.sugorokuon.radiko.api.response.FeedResponse
import tsuyogoro.sugorokuon.repository.FeedRepository
import tsuyogoro.sugorokuon.station.Station

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

    fun observeFeedAvailableStations(): Flowable<List<Station>> =
            Flowable.combineLatest(
                    stationService.observeStations(),
                    feedRepository.observeFeeds(),
                    BiFunction {
                        stations: List<Station>,
                        feeds : Map<String, FeedResponse> ->
                        return@BiFunction feeds.mapNotNull {
                            stations.find { s -> s.id == it.key } // key has stationID
                        }
                    }
            )

    fun observeFeeds(): Flowable<Map<String, FeedResponse>> = feedRepository.observeFeeds()

}