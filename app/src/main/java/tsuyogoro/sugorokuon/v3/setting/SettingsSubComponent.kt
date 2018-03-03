package tsuyogoro.sugorokuon.v3.setting

import dagger.Subcomponent

@Subcomponent(modules = [
    SettingsModule::class
])
interface SettingsSubComponent {

    fun inject(settingsFragment: SettingsTopFragment)

    fun inject(areaSettingsFragment: AreaSettingsFragment)

}