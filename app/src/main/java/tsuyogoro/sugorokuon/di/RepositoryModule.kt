package tsuyogoro.sugorokuon.di

import android.content.Context
import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.preference.AppPrefs
import tsuyogoro.sugorokuon.preference.AreaPrefs
import tsuyogoro.sugorokuon.preference.StationPrefs
import tsuyogoro.sugorokuon.repository.*
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun provideStationRepository(): StationRepository = StationRepository()

    @Singleton
    @Provides
    fun provideTimeTableRepository(): TimeTableRepository = TimeTableRepository()

    @Singleton
    @Provides
    fun provideSettingsRepository(appContext: Context): SettingsRepository =
            SettingsRepository(
                    areaPrefs = AreaPrefs.get(appContext),
                    stationsPrefs = StationPrefs.get(appContext)
            )

    @Singleton
    @Provides
    fun provideFeedRepository(): FeedRepository = FeedRepository()

    @Singleton
    @Provides
    fun provideAppPrefsRepository(appContext: Context): AppPrefRepository =
            AppPrefRepository(appPrefs = AppPrefs.get(appContext))

}