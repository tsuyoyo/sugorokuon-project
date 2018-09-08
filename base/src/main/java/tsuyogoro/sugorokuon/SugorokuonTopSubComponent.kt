package tsuyogoro.sugorokuon

import dagger.Subcomponent
import tsuyogoro.sugorokuon.SugorokuonTopActivity
import tsuyogoro.sugorokuon.SugorokuonTopModule

@Subcomponent(modules = [
    SugorokuonTopModule::class
])
interface SugorokuonTopSubComponent {

    fun inject(activity: SugorokuonTopActivity)

}