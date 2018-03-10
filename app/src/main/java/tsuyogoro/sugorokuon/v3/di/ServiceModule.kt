package tsuyogoro.sugorokuon.v3.di

import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.v3.api.FeedApi
import tsuyogoro.sugorokuon.v3.api.StationApi
import tsuyogoro.sugorokuon.v3.api.TimeTableApi
import tsuyogoro.sugorokuon.v3.model.SugorokuonAppState
import tsuyogoro.sugorokuon.v3.repository.*
import tsuyogoro.sugorokuon.v3.service.*

@Module
class ServiceModule {

    @Provides
    fun provideStationService(stationApi: StationApi,
                              stationRepository: StationRepository) =
            StationService(stationApi, stationRepository)

    @Provides
    fun provideFeedService(stationService: StationService,
                           feedApi: FeedApi,
                           feedRepository: FeedRepository,
                           appState: SugorokuonAppState) =
            FeedService(stationService, feedApi, feedRepository, appState)

    @Provides
    fun provideSettingSErvice(settingsRepository: SettingsRepository) =
            SettingsService(settingsRepository)

    @Provides
    fun provideTimeTableService(timeTableApi: TimeTableApi,
                                stationService: StationService,
                                timeTableRepository: TimeTableRepository) =
            TimeTableService(timeTableApi, stationService, timeTableRepository)

    @Provides
    fun provideTutorialService(appPrefRepository: AppPrefRepository) =
            TutorialService(appPrefRepository)

}