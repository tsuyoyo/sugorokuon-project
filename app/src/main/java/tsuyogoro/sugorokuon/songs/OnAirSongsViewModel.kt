package tsuyogoro.sugorokuon.songs

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import io.reactivex.disposables.CompositeDisposable
import tsuyogoro.sugorokuon.api.response.FeedResponse
import tsuyogoro.sugorokuon.rx.SchedulerProvider
import tsuyogoro.sugorokuon.service.FeedService
import tsuyogoro.sugorokuon.service.StationService

class OnAirSongsViewModel(
        private val stationId: String,
        private val stationService: StationService,
        private val feedService: FeedService,
        private val schedulerProvider: SchedulerProvider,
        private val disposables: CompositeDisposable = CompositeDisposable()
) : ViewModel() {

    @Suppress("UNCHECKED_CAST")
    class Factory(
            private val stationId: String,
            private val stationService: StationService,
            private val feedService: FeedService,
            private val schedulerProvider: SchedulerProvider
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            OnAirSongsViewModel(stationId, stationService, feedService, schedulerProvider) as T
    }

    private val onAirSongs = MutableLiveData<List<FeedResponse.Song>>()

    private val signalFetchOnAirSongError = MutableLiveData<Boolean>()

    init {
        disposables.add(
                feedService.observeFeeds()
                        .filter { it[stationId] != null }
                        .doOnNext { onAirSongs.postValue(it[stationId]!!.songs) }
                        .subscribe()
        )
    }

    fun fetchOnAirSongs(stationId: String) {
        disposables.add(
                feedService
                        .fetchFeed(stationId)
                        .subscribeOn(schedulerProvider.io())
                        .subscribe( { }, { _ -> signalFetchOnAirSongError.postValue(true) })
        )
    }

    fun observeOnAirSongs(): LiveData<List<FeedResponse.Song>> = onAirSongs

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}