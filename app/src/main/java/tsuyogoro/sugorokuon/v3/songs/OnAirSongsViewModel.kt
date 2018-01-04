package tsuyogoro.sugorokuon.v3.songs

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import tsuyogoro.sugorokuon.v3.api.response.FeedResponse
import tsuyogoro.sugorokuon.v3.api.response.StationResponse
import tsuyogoro.sugorokuon.v3.repository.FeedRepository
import tsuyogoro.sugorokuon.v3.rx.SchedulerProvider

class OnAirSongsViewModel(
        private val station: StationResponse.Station,
        private val songs: List<FeedResponse.Song>,
        private val feedRepository: FeedRepository,
        private val schedulerProvider: SchedulerProvider
) : ViewModel() {

    @Suppress("UNCHECKED_CAST")
    class Factory(
            private val onAirSongsData: OnAirSongsData,
            private val feedRepository: FeedRepository,
            private val schedulerProvider: SchedulerProvider
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            OnAirSongsViewModel(
                    onAirSongsData.station,
                    onAirSongsData.songs,
                    feedRepository,
                    schedulerProvider
            ) as T
    }

    private val onAirSongs = MutableLiveData<List<FeedResponse.Song>>()

    init {
        onAirSongs.value = songs
    }

    fun observeOnAirSongs(): LiveData<List<FeedResponse.Song>> = onAirSongs

    override fun onCleared() {
        super.onCleared()
    }
}