package tsuyogoro.sugorokuon

import dagger.Subcomponent

@Subcomponent(modules = [
    SugorokuonTopModule::class
])
interface SugorokuonTopSubComponent {

    fun inject(activity: SugorokuonTopActivity)

}