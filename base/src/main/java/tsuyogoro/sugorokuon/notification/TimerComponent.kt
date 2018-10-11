package tsuyogoro.sugorokuon.notification

import dagger.Subcomponent
import tsuyogoro.sugorokuon.recommend.RecommendModule

@Subcomponent(modules = [
    RecommendModule::class,
    TimerModule::class
])
interface TimerComponent {

    fun inject(recommendRemindBroadCastReceiver: RecommendRemindBroadCastReceiver)

}