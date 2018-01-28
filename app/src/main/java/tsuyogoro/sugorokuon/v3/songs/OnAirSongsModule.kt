package tsuyogoro.sugorokuon.v3.songs

import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.v3.service.FeedService
import tsuyogoro.sugorokuon.v3.service.StationService

@Module
class OnAirSongsModule(
        private val stationId: String
) {
    @Provides
    fun provideOnAirSongsViewModelFactory(feedService: FeedService, stationService: StationService) =
            OnAirSongsViewModel.Factory(stationId, stationService, feedService)
}