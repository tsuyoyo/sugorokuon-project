package tsuyogoro.sugorokuon.debug

import dagger.Subcomponent
import tsuyogoro.sugorokuon.recommend.RecommendModule

@Subcomponent(modules = [
    RecommendModule::class
])
interface DebugComponent {

    fun inject(recommendDebugActivity: RecommendDebugActivity)

}