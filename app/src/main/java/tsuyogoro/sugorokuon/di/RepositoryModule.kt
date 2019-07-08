package tsuyogoro.sugorokuon.di

import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.repository.FeedRepository
import tsuyogoro.sugorokuon.repository.TimeTableRepository
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun provideTimeTableRepository(): TimeTableRepository = TimeTableRepository()

    @Singleton
    @Provides
    fun provideFeedRepository(): FeedRepository = FeedRepository()

}