package tsuyogoro.sugorokuon.recommend

import dagger.Component
import tsuyogoro.sugorokuon.data.DataModule
import tsuyogoro.sugorokuon.radiko.RadikoApiModule
import tsuyogoro.sugorokuon.recommend.debug.RecommendDebugActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [
    RadikoApiModule::class,
    RecommendModule::class,
    RecommendDataModule::class,
    DataModule::class,
    RecommendInternalModule::class
])
internal interface RecommendComponent {

    fun inject(recommendReminderBroadCastReceiver: RecommendBroadCastReceiver)

    fun inject(recommendDebugActivity: RecommendDebugActivity)

}