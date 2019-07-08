package tsuyogoro.sugorokuon.recommend

import dagger.Component
import tsuyogoro.sugorokuon.data.DataModule
import tsuyogoro.sugorokuon.di.ServiceModule
import tsuyogoro.sugorokuon.radiko.RadikoApiModule
import tsuyogoro.sugorokuon.recommend.debug.RecommendDebugActivity
import tsuyogoro.sugorokuon.recommend.view.RecommendViewHolderImpl
import tsuyogoro.sugorokuon.setting.SettingsModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    RadikoApiModule::class,
    RecommendModule::class,
    RecommendInternalModule::class,
    RecommendDataModule::class,
    DataModule::class,
    ServiceModule::class
])
internal interface RecommendComponent {

    fun inject(recommendReminderBroadCastReceiver: RecommendBroadCastReceiver)

    fun inject(recommendDebugActivity: RecommendDebugActivity)

    fun inject(recommendViewHolderImpl: RecommendViewHolderImpl)

    fun inject(recommendUpdaterImpl: RecommendUpdaterImpl)

}