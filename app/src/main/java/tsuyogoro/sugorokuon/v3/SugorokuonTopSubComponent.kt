package tsuyogoro.sugorokuon.v3

import dagger.Subcomponent

@Subcomponent(modules = [
    SugorokuonTopModule::class
])
interface SugorokuonTopSubComponent {

    fun inject(activity: SugorokuonTopActivity)

}