/**
 * Copyright (c)
 * 2018 tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.recommend

import android.content.Context
import android.support.v7.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.radiko.api.SearchApi
import tsuyogoro.sugorokuon.recommend.reminder.RecommendRemindNotifier
import tsuyogoro.sugorokuon.recommend.settings.RecommendSettingsRepository
import tsuyogoro.sugorokuon.settings.SettingsRepository
import tsuyogoro.sugorokuon.station.StationRepository

@Module
class RecommendModule {

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

    // TODO : これも外に出す必要は無いのでは?
    @Provides
    fun provideRecommendRemindNotifier(
        context: Context,
        recommendSettingsRepository: RecommendSettingsRepository,
        stationRepository: StationRepository
    ): RecommendRemindNotifier = RecommendRemindNotifier(
        context, recommendSettingsRepository, stationRepository
    )
}