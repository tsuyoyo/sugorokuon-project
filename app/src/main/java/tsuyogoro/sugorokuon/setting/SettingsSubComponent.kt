package tsuyogoro.sugorokuon.setting

import dagger.Subcomponent

@Subcomponent(modules = [
    SettingsModule::class
])
interface SettingsSubComponent {

    fun inject(settingsFragment: SettingsTopFragment)

    fun inject(areaSettingsFragment: AreaSettingsFragment)

    fun inject(stationOrderFragment: StationOrderFragment)

    fun inject(searchSongMethodFragment: SearchSongMethodFragment)

}