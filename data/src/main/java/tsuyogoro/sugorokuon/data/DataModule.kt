package tsuyogoro.sugorokuon.data

import android.arch.persistence.room.Room
import android.content.Context
import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.appstate.AppPrefRepository
import tsuyogoro.sugorokuon.appstate.AppPrefs
import tsuyogoro.sugorokuon.preference.AreaPrefs
import tsuyogoro.sugorokuon.preference.SearchMethodPrefs
import tsuyogoro.sugorokuon.preference.StationPrefs
import tsuyogoro.sugorokuon.settings.SettingsRepository
import tsuyogoro.sugorokuon.station.StationDao
import tsuyogoro.sugorokuon.station.StationDatabase
import tsuyogoro.sugorokuon.station.StationRepository
import javax.inject.Singleton

@Module
class DataModule {

    @Singleton
    @Provides
    fun provideSettingsRepository(appContext: Context): SettingsRepository =
        SettingsRepository(
            areaPrefs = AreaPrefs.get(appContext),
            stationsPrefs = StationPrefs.get(appContext),
            searchMethodPrefs = SearchMethodPrefs.get(appContext)
        )

    @Singleton
    @Provides
    fun provideAppPrefsRepository(appContext: Context): AppPrefRepository =
        AppPrefRepository(appPrefs = AppPrefs.get(appContext))

    @Provides
    fun provideStationDatabase(context: Context): StationDatabase = Room
        .databaseBuilder(
            context.applicationContext,
            StationDatabase::class.java,
            "stations"
        )
        .allowMainThreadQueries()
        .build()

    @Provides
    fun provideStationDao(database: StationDatabase): StationDao =
        database.stationDao()

    @Singleton
    @Provides
    fun provideStationRepository(stationDao: StationDao): StationRepository =
        StationRepository(stationDao = stationDao)
}