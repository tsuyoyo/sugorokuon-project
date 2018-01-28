package tsuyogoro.sugorokuon.v3.songs

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import io.reactivex.disposables.CompositeDisposable
import tsuyogoro.sugorokuon.v3.api.response.StationResponse
import tsuyogoro.sugorokuon.v3.service.FeedService

class OnAirSongsRootViewModel(
        private val feedService: FeedService,
        private val disposables: CompositeDisposable = CompositeDisposable()
) : ViewModel() {

    private val onAirSongsDataList = MutableLiveData<List<StationResponse.Station>>()

    private val isLoading = MutableLiveData<Boolean>()

    @Suppress("UNCHECKED_CAST")
    class Factory(
            private val feedService: FeedService
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                OnAirSongsRootViewModel(feedService) as T
    }

    init {
        disposables.add(
            feedService.observeFeedAvailableStations()
                    .doOnNext { onAirSongsDataList.postValue(it) }
                    .subscribe()
        )
    }

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }

    fun observeFeedAvailableStations(): LiveData<List<StationResponse.Station>> = onAirSongsDataList

    fun observeIsLoading(): LiveData<Boolean> = isLoading

//    private fun fetchFeedAvailableStations(): Completable {
//        return settingsRepository
//                .observeAreaSettings()
//                .doOnSubscribe { onAirSongsDataList.postValue(emptyList()) }
//                .firstElement()
//                .doOnSuccess { isLoading.postValue(true) }
//                .flatMapPublisher { areas ->
//                    Flowable.create<Area>({ source ->
//                        areas.forEach(source::onNext)
//                        source.onComplete()
//                    }, BackpressureStrategy.LATEST)
//                }
//                .flatMapMaybe { area -> stationRepository.fetchStations(area) }
//                .flatMap { stationsForArea -> Flowable.fromIterable(stationsForArea) }
//                .flatMapCompletable { station ->
//                    feedRepository
//                            .fetchFeed(station.id, update = true)
//                            .filter {
//                                onAirSongsDataList.value
//                                        ?.find { it.station.id == station.id } == null
//                            }
//                            .doOnSuccess {
//                                onAirSongsDataList.postValue(
//                                        onAirSongsDataList.value?.toMutableList()?.apply {
//                                            set(
//                                                    OnAirSongsData(station, it.songs)
//                                            )
//                                        }
//                                )
//                            }
//                            .ignoreElement()
//                            // when the station has no feed, skip it.
//                            .onErrorResumeNext { Completable.complete() }
//                }
//                .doOnComplete { isLoading.postValue(false) }
//                .doOnError { isLoading.postValue(false) }
//                .subscribeOn(schedulerProvider.io())
//                .observeOn(schedulerProvider.mainThread())
//    }

}