package tsuyogoro.sugorokuon.v3.songs

import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.v3.repository.FeedRepository
import tsuyogoro.sugorokuon.v3.repository.SettingsRepository
import tsuyogoro.sugorokuon.v3.repository.StationRepository
import tsuyogoro.sugorokuon.v3.rx.SchedulerProvider

@Module
class OnAirSongsRootModule {

    @Provides
    fun provideOnAirSongsRootViewModelFactory(
            settingsRepository: SettingsRepository,
            stationRepository: StationRepository,
            feedRepository: FeedRepository,
            schedulerProvider: SchedulerProvider
    ) = OnAirSongsRootViewModel.Factory(
            settingsRepository, stationRepository, feedRepository, schedulerProvider)

}