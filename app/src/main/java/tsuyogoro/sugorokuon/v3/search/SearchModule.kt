package tsuyogoro.sugorokuon.v3.search

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.v3.api.SearchApi
import tsuyogoro.sugorokuon.v3.api.SearchUuidGenerator
import tsuyogoro.sugorokuon.v3.rx.SchedulerProvider
import tsuyogoro.sugorokuon.v3.service.SettingsService
import tsuyogoro.sugorokuon.v3.service.StationService

@Module
class SearchModule {

    @Provides
    fun provideSearchResponseRepository(): SearchResponseRepository =
            SearchResponseRepository()

    @Provides
    fun provideSearchService(
            searchApi: SearchApi,
            searchUuidGenerator: SearchUuidGenerator,
            searchResponseRepository: SearchResponseRepository): SearchService =
            SearchService(
                    searchApi,
                    searchUuidGenerator,
                    searchResponseRepository
            )

    @Provides
    fun provideSearchViewModelFactory(
            searchService: SearchService,
            settingsService: SettingsService,
            stationService: StationService,
            schedulerProvider: SchedulerProvider,
            resources: Resources
    ) = SearchViewModel.Factory(
            searchService,
            settingsService,
            stationService,
            schedulerProvider,
            resources
    )
}