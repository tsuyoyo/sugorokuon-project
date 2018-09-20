package tsuyogoro.sugorokuon.di

import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.radiko.api.FeedApi
import tsuyogoro.sugorokuon.radiko.api.StationApi
import tsuyogoro.sugorokuon.radiko.api.TimeTableApi
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