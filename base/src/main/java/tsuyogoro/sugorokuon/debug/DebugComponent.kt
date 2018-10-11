package tsuyogoro.sugorokuon.debug

import dagger.Subcomponent
import tsuyogoro.sugorokuon.notification.TimerModule
import tsuyogoro.sugorokuon.recommend.RecommendModule

@Subcomponent(modules = [
    RecommendModule::class,
    TimerModule::class
])
interface DebugComponent {

    fun inject(recommendDebugActivity: RecommendDebugActivity)

}