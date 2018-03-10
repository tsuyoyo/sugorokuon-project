package tsuyogoro.sugorokuon.v3

import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.v3.rx.SchedulerProvider
import tsuyogoro.sugorokuon.v3.service.*

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