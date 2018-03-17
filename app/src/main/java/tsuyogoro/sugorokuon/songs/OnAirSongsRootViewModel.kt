package tsuyogoro.sugorokuon.songs

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import io.reactivex.disposables.CompositeDisposable
import tsuyogoro.sugorokuon.api.response.StationResponse
import tsuyogoro.sugorokuon.service.FeedService

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

}