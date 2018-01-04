package tsuyogoro.sugorokuon.v3.songs

import dagger.Subcomponent

@Subcomponent(modules = [
    OnAirSongsModule::class
])
interface OnAirSongsSubModule {

    fun inject(fragment: OnAirSongsFragment)

}