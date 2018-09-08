package tsuyogoro.sugorokuon.songs

import dagger.Subcomponent

@Subcomponent(modules = [
    OnAirSongsRootModule::class
])
interface OnAirSongsRootSubModule {

    fun inject(fragment: OnAirSongsRootFragment)

}