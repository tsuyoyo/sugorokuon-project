package tsuyogoro.sugorokuon.data

import androidx.room.Room
import android.content.Context
import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.appstate.AppPrefRepository
import tsuyogoro.sugorokuon.appstate.AppPrefs
import tsuyogoro.sugorokuon.preference.AreaPrefs
import tsuyogoro.sugorokuon.preference.SearchMethodPrefs
import tsuyogoro.sugorokuon.preference.StationPrefs
import tsuyogoro.sugorokuon.recommend.RecommendProgramRepository
import tsuyogoro.sugorokuon.recommend.RecommendProgramsDao
import tsuyogoro.sugorokuon.recommend.RecommendProgramsDatabase
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

    @Singleton
    @Provides
    fun provideStationRepository(context: Context): StationRepository =
        StationRepository().apply {
            initialize(provideStationDao(provideStationDatabase(context)))
        }

    @Singleton
    @Provides
    fun provideRecommendProgramRepository(context: Context): RecommendProgramRepository =
        RecommendProgramRepository().apply {
            initialize(
                provideRecommendProgramsDao(provideRecommendProgramsDatabase(context))
            )
        }

    private fun provideStationDatabase(context: Context): StationDatabase = Room
        .databaseBuilder(
            context.applicationContext,
            StationDatabase::class.java,
            "stations"
        )
        .allowMainThreadQueries()
        .build()

    private fun provideStationDao(database: StationDatabase): StationDao =
        database.stationDao()


    private fun provideRecommendProgramsDatabase(context: Context): RecommendProgramsDatabase = Room
        .databaseBuilder(
            context.applicationContext,
            RecommendProgramsDatabase::class.java,
            "recommend_program_database"
        )
        .allowMainThreadQueries()
        .build()

    private fun provideRecommendProgramsDao(database: RecommendProgramsDatabase): RecommendProgramsDao =
        database.recommendProgramsDao()

}