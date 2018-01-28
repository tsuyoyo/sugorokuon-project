package tsuyogoro.sugorokuon.v3.songs

import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.v3.service.FeedService

@Module
class OnAirSongsRootModule {

    @Provides
    fun provideOnAirSongsRootViewModelFactory(feedService: FeedService) =
            OnAirSongsRootViewModel.Factory(feedService)

}