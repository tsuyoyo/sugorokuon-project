package tsuyogoro.sugorokuon.debug

import dagger.Subcomponent
import tsuyogoro.sugorokuon.recommend.RecommendModule
import tsuyogoro.sugorokuon.recommend.debug.RecommendDebugActivity

@Subcomponent(modules = [
    RecommendModule::class
])
interface DebugComponent {

    fun inject(recommendDebugActivity: RecommendDebugActivity)

}