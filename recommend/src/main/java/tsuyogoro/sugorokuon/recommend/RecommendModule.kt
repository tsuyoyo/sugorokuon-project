/**
 * Copyright (c)
 * 2018 tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.recommend

import android.content.Context
import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.radiko.api.SearchApi
import tsuyogoro.sugorokuon.recommend.debug.RecommendConfigPrefs
import tsuyogoro.sugorokuon.recommend.settings.RecommendSettingsRepository
import tsuyogoro.sugorokuon.recommend.view.RecommendViewHolderViewModel
import tsuyogoro.sugorokuon.settings.SettingsRepository
import tsuyogoro.sugorokuon.station.StationRepository

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
        recommendConfigs: RecommendConfigs,
        recommendSettingsRepository: RecommendSettingsRepository) =
        RecommendTimerService(
            context,
            recommendProgramRepository,
            recommendConfigs,
            recommendSettingsRepository
        )

    @Provides
    fun provideRecommendConfig(context: Context): RecommendConfigs =
        RecommendConfigs(RecommendConfigPrefs.get(context))

    @Provides
    fun provideRecommendViewHolderViewModel(
        recommendProgramRepository: RecommendProgramRepository,
        stationRepository: StationRepository,
        recommendSettingsRepository: RecommendSettingsRepository
    ): RecommendViewHolderViewModel = RecommendViewHolderViewModel(
        recommendProgramRepository,
        stationRepository,
        recommendSettingsRepository
    )
}
