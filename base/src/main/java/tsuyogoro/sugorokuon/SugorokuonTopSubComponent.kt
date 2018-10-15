package tsuyogoro.sugorokuon

import dagger.Subcomponent
import tsuyogoro.sugorokuon.recommend.RecommendModule
import javax.inject.Singleton

@Subcomponent(modules = [
    SugorokuonTopModule::class,
    RecommendModule::class
])
interface SugorokuonTopSubComponent {

    fun inject(activity: SugorokuonTopActivity)

}