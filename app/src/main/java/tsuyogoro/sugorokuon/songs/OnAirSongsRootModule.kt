package tsuyogoro.sugorokuon.songs

import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.service.FeedService
import tsuyogoro.sugorokuon.service.SettingsService

@Module
class OnAirSongsRootModule {

    @Provides
    fun provideOnAirSongsRootViewModelFactory(feedService: FeedService,
                                              settingsService: SettingsService) =
            OnAirSongsRootViewModel.Factory(feedService, settingsService)

}