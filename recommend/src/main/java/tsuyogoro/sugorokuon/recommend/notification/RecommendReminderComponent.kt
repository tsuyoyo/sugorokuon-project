package tsuyogoro.sugorokuon.recommend.notification

import android.content.Context
import dagger.Component
import dagger.Provides
import tsuyogoro.sugorokuon.data.DataModule
import tsuyogoro.sugorokuon.radiko.RadikoApiModule
import tsuyogoro.sugorokuon.recommend.RecommendModule
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
    RecommendReminderComponent.Module::class
])
internal interface RecommendReminderComponent {

    fun inject(recommendReminderBroadCastReceiver: RecommendRemindBroadCastReceiver)

    fun inject(recommendDebugActivity: RecommendDebugActivity)

    @dagger.Module
    class Module(c: Context) {
        private val context: Context = c.applicationContext

        @Singleton
        @Provides
        fun provideAppContext(): Context = context

        @Provides
        fun provideRecommendRemindTimerSubmitter(context: Context) =
            RecommendRemindTimerSubmitter(context)

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