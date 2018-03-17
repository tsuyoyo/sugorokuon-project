package tsuyogoro.sugorokuon.search

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.res.Resources
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import tsuyogoro.sugorokuon.R
import tsuyogoro.sugorokuon.utils.SugorokuonLog
import tsuyogoro.sugorokuon.api.response.SearchResponse
import tsuyogoro.sugorokuon.api.response.StationResponse
import tsuyogoro.sugorokuon.rx.SchedulerProvider
import tsuyogoro.sugorokuon.service.SettingsService
import tsuyogoro.sugorokuon.service.StationService

class SearchViewModel(
        private val searchService: SearchService,
        private val settingsService: SettingsService,
        private val stationService: StationService,
        private val schedulerProvider: SchedulerProvider,
        private val resources: Resources,
        private val disposables: CompositeDisposable = CompositeDisposable()
) : ViewModel() {

    @Suppress("UNCHECKED_CAST")
    class Factory(
            private val searchService: SearchService,
            private val settingsService: SettingsService,
            private val stationService: StationService,
            private val schedulerProvider: SchedulerProvider,
            private val resources: Resources
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SearchViewModel(
                    searchService,
                    settingsService,
                    stationService,
                    schedulerProvider,
                    resources
            ) as T
        }
    }

    data class SearchResultData(
            val program: SearchResponse.Program,
            val station: StationResponse.Station
    )

    private val searchResults: MutableLiveData<List<SearchResultData>> = MutableLiveData()
    private val searchCondition: MutableLiveData<String> = MutableLiveData()
    private val searchError: MutableLiveData<String> = MutableLiveData()
    private val isSearching: MutableLiveData<Boolean> = MutableLiveData()

    init {
        Flowable.combineLatest(
                searchService.observeSearchResults(),
                stationService.observeStations(),
                BiFunction { foundPrograms: List<SearchResponse.Program>,
                             stations: List<StationResponse.Station> ->

                    val results = mutableListOf<SearchResultData>()
                    foundPrograms.forEach {

                        val isAlreadyAdded = results.find { r ->
                            r.program.title == it.title && r.program.start == it.start } != null
                        if (!isAlreadyAdded) {
                            stations
                                    .find { s -> s.id == it.stationId }
                                    ?.let { s ->
                                        results.add(SearchResultData(it, s))
                                    }
                        }
                    }
                    results.sortBy { it.program.start }

                    return@BiFunction results
                })
                .doOnNext(searchResults::postValue)
                .subscribeOn(schedulerProvider.io())
                .subscribe(
                        {},
                        { e -> SugorokuonLog.e(e.message) }
                )

        searchCondition.postValue("")
    }

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }

    fun observeSearchResults(): LiveData<List<SearchResultData>> = searchResults

    fun observeSearchCondition(): LiveData<String> = searchCondition

    fun observeSearchError(): LiveData<String> = searchError

    fun observeIsSearching(): LiveData<Boolean> = isSearching

    fun search(keyword: String) {
        disposables.add(
                settingsService
                        .observeAreas()
                        .doOnSubscribe { isSearching.postValue(true) }
                        .flatMapCompletable {
                            searchService
                                    .search(keyword, it)
                                    .doOnComplete {
                                        isSearching.postValue(false)
                                        searchCondition.postValue(
                                                String.format(
                                                        resources.getString(R.string.search_condition),
                                                        keyword
                                                )
                                        )
                                    }
                                    .doOnError { isSearching.postValue(false) }
                        }
                        .subscribeOn(schedulerProvider.io())
                        .subscribe(
                                {

                                },
                                { e ->
                                    val msg = String.format(
                                            resources.getString(R.string.error_search),
                                            e.localizedMessage
                                    )
                                    searchError.postValue(msg)
                                }
                        )
        )
    }
}