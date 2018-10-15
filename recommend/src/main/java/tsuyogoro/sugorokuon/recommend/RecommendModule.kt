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
import tsuyogoro.sugorokuon.recommend.debug.RecommendConfigPrefs
import tsuyogoro.sugorokuon.recommend.settings.RecommendSettingsRepository
import tsuyogoro.sugorokuon.settings.SettingsRepository

@Module
class RecommendModule {

    @Provides
    fun provideRecommendSearchService(
        searchApi: SearchApi,
        recommendProgramRepository: RecommendProgramRepository,
        recommendSettingsRepository: RecommendSettingsRepository,
        settingsRepository: SettingsRepository
    ): RecommendSearchService = RecommendSearchService(
        searchApi = searchApi,
        recommendProgramRepository = recommendProgramRepository,
        recommendSettingsRepository = recommendSettingsRepository,
        settingsRepository = settingsRepository
    )

    @Provides
    fun provideRecommendRemindTimerService(
        context: Context,
        recommendProgramRepository: RecommendProgramRepository,
        recommendConfigs: RecommendConfigs
    ) = RecommendTimerService(context, recommendProgramRepository, recommendConfigs)

    @Provides
    fun provideRecommendConfig(context: Context): RecommendConfigs =
        RecommendConfigs(RecommendConfigPrefs.get(context))
}