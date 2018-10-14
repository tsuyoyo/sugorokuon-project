package tsuyogoro.sugorokuon.recommend

import android.content.Context
import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.recommend.reminder.RecommendRemindNotifier
import tsuyogoro.sugorokuon.recommend.settings.RecommendSettingsRepository
import tsuyogoro.sugorokuon.station.StationRepository
import javax.inject.Singleton

@Module
internal class RecommendInternalModule(c: Context) {
    private val context: Context = c.applicationContext

    @Singleton
    @Provides
    fun provideAppContext(): Context = context

    @Provides
    fun provideRecommendRemindTimerService(
        context: Context,
        recommendProgramRepository: RecommendProgramRepository,
        recommendConfigs: RecommendConfigs
    ) = RecommendTimerService(context, recommendProgramRepository, recommendConfigs)

    @Provides
    fun provideRecommendRemindNotifier(
        context: Context,
        recommendSettingsRepository: RecommendSettingsRepository,
        stationRepository: StationRepository
    ): RecommendRemindNotifier = RecommendRemindNotifier(
        context, recommendSettingsRepository, stationRepository
    )

}