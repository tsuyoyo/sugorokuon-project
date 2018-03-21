package tsuyogoro.sugorokuon.di

import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.api.FeedApi
import tsuyogoro.sugorokuon.api.StationApi
import tsuyogoro.sugorokuon.api.TimeTableApi
import tsuyogoro.sugorokuon.model.SugorokuonAppState
import tsuyogoro.sugorokuon.repository.*
import tsuyogoro.sugorokuon.service.*

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
    fun provideSettingService(settingsRepository: SettingsRepository,
                              stationService: StationService) =
            SettingsService(settingsRepository, stationService)

    @Provides
    fun provideTimeTableService(timeTableApi: TimeTableApi,
                                stationService: StationService,
                                timeTableRepository: TimeTableRepository) =
            TimeTableService(timeTableApi, stationService, timeTableRepository)

    @Provides
    fun provideTutorialService(appPrefRepository: AppPrefRepository) =
            TutorialService(appPrefRepository)

}