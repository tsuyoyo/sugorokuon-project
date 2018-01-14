package tsuyogoro.sugorokuon.v3.songs

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.util.Log
import io.reactivex.disposables.CompositeDisposable
import tsuyogoro.sugorokuon.v3.api.response.FeedResponse
import tsuyogoro.sugorokuon.v3.api.response.StationResponse
import tsuyogoro.sugorokuon.v3.repository.FeedRepository
import tsuyogoro.sugorokuon.v3.rx.SchedulerProvider

class OnAirSongsViewModel(
        private val stationId: String,
        private val feedRepository: FeedRepository,
        private val schedulerProvider: SchedulerProvider,
        private val disposables: CompositeDisposable = CompositeDisposable()
) : ViewModel() {

    @Suppress("UNCHECKED_CAST")
    class Factory(
            private val stationId: String,
            private val feedRepository: FeedRepository,
            private val schedulerProvider: SchedulerProvider
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            OnAirSongsViewModel(
                    stationId,
                    feedRepository,
                    schedulerProvider
            ) as T
    }

    private val onAirSongs = MutableLiveData<List<FeedResponse.Song>>()

    init {
        disposables.add(
                feedRepository.fetchFeed(stationId)
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.mainThread())
                        .doOnSuccess { onAirSongs.value = it.songs }
                        .subscribe()
        )
    }

    fun observeOnAirSongs(): LiveData<List<FeedResponse.Song>> = onAirSongs

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}