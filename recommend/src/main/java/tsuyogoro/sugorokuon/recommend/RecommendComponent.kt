package tsuyogoro.sugorokuon.recommend

import android.content.Context
import dagger.Component
import dagger.Provides
import tsuyogoro.sugorokuon.data.DataModule
import tsuyogoro.sugorokuon.radiko.RadikoApiModule
import tsuyogoro.sugorokuon.recommend.debug.RecommendConfigPrefs
import tsuyogoro.sugorokuon.recommend.debug.RecommendDebugActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [
    RadikoApiModule::class,
    RecommendModule::class,
    DataModule::class,
    RecommendInternalModule::class,
    RecommendComponent.Module::class
])
internal interface RecommendComponent {

    fun inject(recommendReminderBroadCastReceiver: RecommendBroadCastReceiver)

    fun inject(recommendDebugActivity: RecommendDebugActivity)

    @dagger.Module
    class Module {

        @Singleton
        @Provides
        fun provideRecommendConfig(context: Context): RecommendConfigs =
            RecommendConfigs(RecommendConfigPrefs.get(context))

    }

}