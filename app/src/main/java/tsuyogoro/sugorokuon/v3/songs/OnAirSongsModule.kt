package tsuyogoro.sugorokuon.v3.songs

import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.v3.repository.FeedRepository
import tsuyogoro.sugorokuon.v3.rx.SchedulerProvider

@Module
class OnAirSongsModule(
        private val onAirSongsData: OnAirSongsData
) {

    @Provides
    fun provideOnAirSongsViewModelFactory(feedRepository: FeedRepository,
                                          schedulerProvider: SchedulerProvider) =
            OnAirSongsViewModel.Factory(onAirSongsData, feedRepository, schedulerProvider)

}