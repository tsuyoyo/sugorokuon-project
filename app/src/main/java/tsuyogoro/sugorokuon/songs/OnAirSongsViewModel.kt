package tsuyogoro.sugorokuon.songs

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import tsuyogoro.sugorokuon.constant.SearchSongMethod
import tsuyogoro.sugorokuon.radiko.api.response.FeedResponse
import tsuyogoro.sugorokuon.rx.SchedulerProvider
import tsuyogoro.sugorokuon.service.FeedService
import tsuyogoro.sugorokuon.service.SettingsService
import tsuyogoro.sugorokuon.service.StationService

class OnAirSongsViewModel(
        private val stationId: String,
        private val stationService: StationService,
        private val settingsService: SettingsService,
        private val feedService: FeedService,
        private val disposables: CompositeDisposable = CompositeDisposable()
) : ViewModel() {

    @Suppress("UNCHECKED_CAST")
    class Factory(
            private val stationId: String,
            private val stationService: StationService,
            private val settingsService: SettingsService,
            private val feedService: FeedService
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                OnAirSongsViewModel(
                        stationId,
                        stationService,
                        settingsService,
                        feedService
                ) as T
    }

    private val onAirSongs = MutableLiveData<List<FeedResponse.Song>>()

    private val signalFetchOnAirSongError = MutableLiveData<Boolean>()

    private val signalSearchOnPlayer = PublishProcessor.create<FeedResponse.Song>()
    private val signalSearchOnGoogle = PublishProcessor.create<FeedResponse.Song>()
    private val signalSearchCopyText = PublishProcessor.create<FeedResponse.Song>()
    private val signalShowSearchDialog = PublishProcessor.create<FeedResponse.Song>()

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
                        .subscribeOn(Schedulers.io())
                        .subscribe({ }, { _ -> signalFetchOnAirSongError.postValue(true) })
        )
    }

    fun observeOnAirSongs(): LiveData<List<FeedResponse.Song>> = onAirSongs

    fun search(song: FeedResponse.Song) {
        disposables.add(
                settingsService.observeSelectedSongSearchMethod().firstElement()
                        .doOnSuccess {
                            when (it) {
                                SearchSongMethod.COPY_TITLE_CLIP_BOARD ->
                                    signalSearchCopyText.onNext(song)
                                SearchSongMethod.SEARCH_ON_PLAYER ->
                                    signalSearchOnPlayer.onNext(song)
                                SearchSongMethod.SEARCH_ON_GOOGLE ->
                                    signalSearchOnGoogle.onNext(song)
                                else -> signalShowSearchDialog.onNext(song)
                            }
                        }
                        .subscribe()
        )
    }

    fun saveSearchSettings(isSave: Boolean, searchSongMethod: SearchSongMethod) {
        if (isSave) {
            disposables.add(
                    settingsService
                            .setSongSearchMethod(searchSongMethod)
                            .subscribe()
            )
        }
    }

    fun observeSignalSearchOnPlayer(): Flowable<FeedResponse.Song> = signalSearchOnPlayer.hide()

    fun observeSignalSearchOnGoogle(): Flowable<FeedResponse.Song> = signalSearchOnGoogle.hide()

    fun observeSignalSearchCopyText(): Flowable<FeedResponse.Song> = signalSearchCopyText.hide()

    fun observeSignalShowSearchDialog(): Flowable<FeedResponse.Song> = signalShowSearchDialog.hide()

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}