package tsuyogoro.sugorokuon.di

import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.radiko.api.*

@Module
class RadikoApiModule {

    @Provides
    fun provideStationApi(): StationApi = RadikoApiProvider.provideStationApi()

    @Provides
    fun provideSearchApi(): SearchApi = RadikoApiProvider.provideSearchApi()

    @Provides
    fun provideTimeTableApi() : TimeTableApi = RadikoApiProvider.provideTimeTableApi()

    @Provides
    fun provideFeedApi() : FeedApi = RadikoApiProvider.provideFeedApi()

}