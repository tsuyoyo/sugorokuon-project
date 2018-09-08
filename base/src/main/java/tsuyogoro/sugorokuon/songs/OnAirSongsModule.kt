package tsuyogoro.sugorokuon.songs

import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.rx.SchedulerProvider
import tsuyogoro.sugorokuon.service.FeedService
import tsuyogoro.sugorokuon.service.SettingsService
import tsuyogoro.sugorokuon.service.StationService

@Module
class OnAirSongsModule(
        private val stationId: String
) {
    @Provides
    fun provideOnAirSongsViewModelFactory(
            feedService: FeedService,
            stationService: StationService,
            settingsService: SettingsService,
            schedulerProvider: SchedulerProvider) =
            OnAirSongsViewModel.Factory(
                    stationId,
                    stationService,
                    settingsService,
                    feedService,
                    schedulerProvider
            )
}