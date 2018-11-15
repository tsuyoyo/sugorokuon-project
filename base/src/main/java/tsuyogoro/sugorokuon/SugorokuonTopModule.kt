package tsuyogoro.sugorokuon

import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.recommend.RecommendSearchService
import tsuyogoro.sugorokuon.recommend.RecommendTimerService
import tsuyogoro.sugorokuon.recommend.settings.RecommendSettingsRepository
import tsuyogoro.sugorokuon.rx.SchedulerProvider
import tsuyogoro.sugorokuon.service.*

@Module
class SugorokuonTopModule {
    @Provides
    fun provideSugorokuonTopViewModelFactory(
        settingsService: SettingsService,
        timeTableService: TimeTableService,
        stationService: StationService,
        feedService: FeedService,
        tutorialService: TutorialService,
        recommendSearchService: RecommendSearchService,
        recommendTimerService: RecommendTimerService,
        recommendSettingsRepository: RecommendSettingsRepository,
        schedulerProvider: SchedulerProvider) =
            SugorokuonTopViewModel.Factory(
                    settingsService,
                    timeTableService,
                    stationService,
                    feedService,
                    tutorialService,
                    recommendSearchService,
                    recommendTimerService,
                    recommendSettingsRepository,
                    schedulerProvider
            )
}