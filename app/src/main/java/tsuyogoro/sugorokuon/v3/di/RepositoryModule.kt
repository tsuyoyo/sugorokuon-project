package tsuyogoro.sugorokuon.v3.di

import android.content.Context
import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.v3.api.FeedApi
import tsuyogoro.sugorokuon.v3.api.StationApi
import tsuyogoro.sugorokuon.v3.api.TimeTableApi
import tsuyogoro.sugorokuon.v3.preference.AreaPrefs
import tsuyogoro.sugorokuon.v3.repository.FeedRepository
import tsuyogoro.sugorokuon.v3.repository.SettingsRepository
import tsuyogoro.sugorokuon.v3.repository.StationRepository
import tsuyogoro.sugorokuon.v3.repository.TimeTableRepository
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun provideStationRepository(stationApi: StationApi) : StationRepository =
            StationRepository(stationApi)

    @Singleton
    @Provides
    fun provideTimeTableRepository(timeTableApi: TimeTableApi) : TimeTableRepository =
            TimeTableRepository(timeTableApi)

    @Singleton
    @Provides
    fun provideSettingsRepository(appContext: Context) : SettingsRepository =
            SettingsRepository(areaPrefs = AreaPrefs.get(appContext))

    @Singleton
    @Provides
    fun provideFeedRepository(feedApi: FeedApi) : FeedRepository =
            FeedRepository(feedApi)

}