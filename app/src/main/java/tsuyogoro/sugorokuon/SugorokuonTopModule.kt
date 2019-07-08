package tsuyogoro.sugorokuon

import dagger.Module
import dagger.Provides
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
        schedulerProvider: SchedulerProvider) =
            SugorokuonTopViewModel.Factory(
                    settingsService,
                    timeTableService,
                    stationService,
                    feedService,
                    tutorialService,
                    schedulerProvider
            )
}