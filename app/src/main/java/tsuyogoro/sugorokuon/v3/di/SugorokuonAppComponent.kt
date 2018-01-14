package tsuyogoro.sugorokuon.v3.di

import dagger.Component
import tsuyogoro.sugorokuon.v3.api.RadikoApiModule
import tsuyogoro.sugorokuon.v3.setting.SettingsModule
import tsuyogoro.sugorokuon.v3.setting.SettingsSubComonent
import tsuyogoro.sugorokuon.v3.songs.OnAirSongsModule
import tsuyogoro.sugorokuon.v3.songs.OnAirSongsRootModule
import tsuyogoro.sugorokuon.v3.songs.OnAirSongsRootSubModule
import tsuyogoro.sugorokuon.v3.songs.OnAirSongsSubModule
import tsuyogoro.sugorokuon.v3.timetable.*
import javax.inject.Singleton

@Singleton
@Component(modules = [
    SugorokuonAppModule::class,
    RadikoApiModule::class,
    RepositoryModule::class
])
interface SugorokuonAppComponent {

    fun settingSubComponent(settingsModule: SettingsModule)
            : SettingsSubComonent

    fun programTableSubComponent(programTableModule: ProgramTableModule)
            : ProgramTableSubComponent

    fun programInfoSubComponent(programInfoFragment: ProgramInfoModule)
            : ProgramInfoSubComponent

    fun onAirSongsSubComponent(onAirSongsModule: OnAirSongsModule)
            : OnAirSongsSubModule

    fun onAirSongsRootSubComponent(onAirSongsRootModule: OnAirSongsRootModule)
            : OnAirSongsRootSubModule
}