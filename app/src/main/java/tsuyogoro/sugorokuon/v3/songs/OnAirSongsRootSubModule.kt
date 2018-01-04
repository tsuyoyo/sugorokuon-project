package tsuyogoro.sugorokuon.v3.songs

import dagger.Subcomponent

@Subcomponent(modules = [
    OnAirSongsRootModule::class
])
interface OnAirSongsRootSubModule {

    fun inject(fragment: OnAirSongsRootFragment)

}