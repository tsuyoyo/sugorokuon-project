package tsuyogoro.sugorokuon.v3.songs

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import tsuyogoro.sugorokuon.v3.constant.Area
import tsuyogoro.sugorokuon.v3.repository.FeedRepository
import tsuyogoro.sugorokuon.v3.repository.SettingsRepository
import tsuyogoro.sugorokuon.v3.repository.StationRepository
import tsuyogoro.sugorokuon.v3.rx.SchedulerProvider

class OnAirSongsRootViewModel(
        private val settingsRepository: SettingsRepository,
        private val stationRepository: StationRepository,
        private val feedRepository: FeedRepository,
        private val schedulerProvider: SchedulerProvider,
        private val disposables: CompositeDisposable = CompositeDisposable()
) : ViewModel() {

    private val onAirSongsDataList = MutableLiveData<List<OnAirSongsData>>()

    private val isLoading = MutableLiveData<Boolean>()

    @Suppress("UNCHECKED_CAST")
    class Factory(
            private val settingsRepository: SettingsRepository,
            private val stationRepository: StationRepository,
            private val feedRepository: FeedRepository,
            private val schedulerProvider: SchedulerProvider
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                OnAirSongsRootViewModel(
                        settingsRepository,
                        stationRepository,
                        feedRepository,
                        schedulerProvider
                ) as T
    }

    init {
        disposables.add(
                fetchFeedAvailableStations().subscribe()
        )
    }

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }

    fun observeFeedAvailableStations(): LiveData<List<OnAirSongsData>> = onAirSongsDataList

    fun observeIsLoading(): LiveData<Boolean> = isLoading

    private fun fetchFeedAvailableStations(): Completable {
        return settingsRepository
                .observeAreaSettings()
                .doOnSubscribe { onAirSongsDataList.postValue(emptyList()) }
                .firstElement()
                .doOnSuccess { isLoading.postValue(true) }
                .flatMapPublisher { areas ->
                    Flowable.create<Area>({ source ->
                        areas.forEach(source::onNext)
                        source.onComplete()
                    }, BackpressureStrategy.LATEST)
                }
                .flatMapMaybe { area -> stationRepository.fetchStations(area) }
                .flatMap { stationsForArea -> Flowable.fromIterable(stationsForArea) }
                .flatMapCompletable { station ->
                    feedRepository
                            .fetchFeed(station.id, update = true)
                            .filter {
                                onAirSongsDataList.value
                                        ?.find { it.station.id == station.id } == null
                            }
                            .doOnSuccess {
                                onAirSongsDataList.postValue(
                                        onAirSongsDataList.value?.toMutableList()?.apply {
                                            add(
                                                    OnAirSongsData(station, it.songs)
                                            )
                                        }
                                )
                            }
                            .ignoreElement()
                            // when the station has no feed, skip it.
                            .onErrorResumeNext { Completable.complete() }
                }
                .doOnComplete { isLoading.postValue(false) }
                .doOnError { isLoading.postValue(false) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.mainThread())
    }

}