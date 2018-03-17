package tsuyogoro.sugorokuon.songs

import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.service.FeedService

@Module
class OnAirSongsRootModule {

    @Provides
    fun provideOnAirSongsRootViewModelFactory(feedService: FeedService) =
            OnAirSongsRootViewModel.Factory(feedService)

}