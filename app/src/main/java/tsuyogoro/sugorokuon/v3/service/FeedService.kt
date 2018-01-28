package tsuyogoro.sugorokuon.v3.service

import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
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

    fun fetchFeeds(stations: List<StationResponse.Station>): Completable = Flowable
            .create<StationResponse.Station>({ emitter ->
                stations.forEach(emitter::onNext)
                emitter.onComplete()
            }, BackpressureStrategy.LATEST)
            .doOnSubscribe {
                appState.setIsLoadingFeed(true)
                feedRepository.clear()
            }
            .flatMapCompletable {
                feedApi.getFeed(it.id)
                        .doOnSuccess { res -> feedRepository.set(it, res) }
                        .subscribeOn(Schedulers.io())
                        .ignoreElement()
                        .onErrorResumeNext { Completable.complete() }
            }
            .doOnComplete { appState.setIsLoadingFeed(false) }
            .doOnError { appState.setIsLoadingFeed(false) }

    fun fetchFeed(stationId: String): Completable =
            stationService.observeStations().firstElement()
                    .filter { it.find { s -> s.id == stationId } != null }
                    .map { it.find { s -> s.id == stationId } }
                    .flatMapCompletable {
                        feedApi.getFeed(it.id)
                                .doOnSuccess { res -> feedRepository.set(it, res) }
                                .subscribeOn(Schedulers.io())
                                .ignoreElement()
                                .onErrorResumeNext { Completable.complete() }
                    }
                    .doOnComplete { appState.setIsLoadingFeed(false) }
                    .doOnError { appState.setIsLoadingFeed(false) }

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