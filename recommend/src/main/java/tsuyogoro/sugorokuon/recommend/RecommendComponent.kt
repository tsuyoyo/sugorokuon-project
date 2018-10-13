package tsuyogoro.sugorokuon.recommend

import android.content.Context
import dagger.Component
import dagger.Provides
import tsuyogoro.sugorokuon.data.DataModule
import tsuyogoro.sugorokuon.radiko.RadikoApiModule
import tsuyogoro.sugorokuon.recommend.debug.RecommendDebugActivity
import tsuyogoro.sugorokuon.recommend.reminder.RecommendRemindNotifier
import tsuyogoro.sugorokuon.recommend.settings.RecommendSettingsRepository
import tsuyogoro.sugorokuon.station.StationRepository
import javax.inject.Singleton

@Singleton
@Component(modules = [
    RadikoApiModule::class,
    RecommendModule::class,
    DataModule::class,
    RecommendComponent.Module::class
])
internal interface RecommendComponent {

    fun inject(recommendReminderBroadCastReceiver: RecommendBroadCastReceiver)

    fun inject(recommendDebugActivity: RecommendDebugActivity)

    @dagger.Module
    class Module(c: Context) {
        private val context: Context = c.applicationContext

        @Singleton
        @Provides
        fun provideAppContext(): Context = context

        @Provides
        fun provideRecommendRemindTimerSubmitter(context: Context) =
            RecommendTimerSubmitter(context)

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
}