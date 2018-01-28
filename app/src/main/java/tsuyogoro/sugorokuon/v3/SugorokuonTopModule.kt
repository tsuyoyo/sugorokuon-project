package tsuyogoro.sugorokuon.v3

import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.v3.rx.SchedulerProvider
import tsuyogoro.sugorokuon.v3.service.FeedService
import tsuyogoro.sugorokuon.v3.service.SettingsService
import tsuyogoro.sugorokuon.v3.service.StationService
import tsuyogoro.sugorokuon.v3.service.TimeTableService

@Module
class SugorokuonTopModule {
    @Provides
    fun provideSugorokuonTopViewModelFactory(
            settingsService: SettingsService,
            timeTableService: TimeTableService,
            stationService: StationService,
            feedService: FeedService,
            schedulerProvider: SchedulerProvider) = SugorokuonTopViewModel.Factory(
            settingsService, timeTableService, stationService, feedService, schedulerProvider)
}