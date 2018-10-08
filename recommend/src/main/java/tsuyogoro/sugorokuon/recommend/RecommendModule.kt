/**
 * Copyright (c)
 * 2018 tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.recommend

import android.arch.persistence.room.Room
import android.content.Context
import android.support.v7.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.data.SettingsRepository
import tsuyogoro.sugorokuon.radiko.api.SearchApi
import tsuyogoro.sugorokuon.recommend.database.RecommendProgramsDao
import tsuyogoro.sugorokuon.recommend.database.RecommendProgramsDatabase
import tsuyogoro.sugorokuon.recommend.notification.RecommendRemindNotifier
import tsuyogoro.sugorokuon.recommend.settings.RecommendSettingsRepository

@Module
class RecommendModule {

    @Provides
    fun provideRecommendProgramsDatabase(context: Context): RecommendProgramsDatabase = Room
        .databaseBuilder(
            context.applicationContext,
            RecommendProgramsDatabase::class.java,
            "recommend_program_database"
        )
        .allowMainThreadQueries()
        .build()

    @Provides
    fun provideRecommendProgramsDao(database: RecommendProgramsDatabase): RecommendProgramsDao =
        database.recommendProgramsDao()

    @Provides
    fun provideRecommendSettingsRepository(context: Context): RecommendSettingsRepository =
        RecommendSettingsRepository(context, PreferenceManager.getDefaultSharedPreferences(context))

    @Provides
    fun provideRecommendSearchService(
        searchApi: SearchApi,
        recommendProgramsDao: RecommendProgramsDao,
        recommendSettingsRepository: RecommendSettingsRepository,
        settingsRepository: SettingsRepository
    ): RecommendSearchService = RecommendSearchService(
        searchApi = searchApi,
        recommendProgramsDao = recommendProgramsDao,
        recommendSettingsRepository = recommendSettingsRepository,
        settingsRepository = settingsRepository
    )

    @Provides
    fun provideRecommendRemindNotifier(
        context: Context,
        recommendSettingsRepository: RecommendSettingsRepository
    ): RecommendRemindNotifier = RecommendRemindNotifier(
        context, recommendSettingsRepository
    )

}