package tsuyogoro.sugorokuon.songs

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import tsuyogoro.sugorokuon.service.FeedService
import tsuyogoro.sugorokuon.service.SettingsService
import tsuyogoro.sugorokuon.station.Station

class OnAirSongsRootViewModel(
        feedService: FeedService,
        settingsService: SettingsService,
        private val disposables: CompositeDisposable = CompositeDisposable()
) : ViewModel() {

    private val onAirSongsDataList = MutableLiveData<List<Station>>()

    private val isLoading = MutableLiveData<Boolean>()

    @Suppress("UNCHECKED_CAST")
    class Factory(
            private val feedService: FeedService,
            private val settingsService: SettingsService
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                OnAirSongsRootViewModel(feedService, settingsService) as T
    }

    init {
        disposables.add(
                Flowable.combineLatest(
                        feedService.observeFeedAvailableStations(),
                        settingsService.observeOrderedStations(),
                        BiFunction { availableStations: List<Station>,
                                     orderedStations: List<Station> ->
                            val orderedAvailableStations = mutableListOf<Station>()
                            orderedStations.forEach { s ->
                                availableStations.find { it.id == s.id }
                                        ?.let(orderedAvailableStations::add)
                            }
                            return@BiFunction orderedAvailableStations
                        })
                        .doOnNext { onAirSongsDataList.postValue(it) }
                        .subscribe()
        )
    }

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }

    fun observeFeedAvailableStations(): LiveData<List<Station>> = onAirSongsDataList

    fun observeIsLoading(): LiveData<Boolean> = isLoading

}