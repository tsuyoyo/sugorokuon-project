package tsuyogoro.sugorokuon.v3.di

import dagger.Component
import tsuyogoro.sugorokuon.v3.SugorokuonTopModule
import tsuyogoro.sugorokuon.v3.SugorokuonTopSubComponent
import tsuyogoro.sugorokuon.v3.api.RadikoApiModule
import tsuyogoro.sugorokuon.v3.setting.SettingsModule
import tsuyogoro.sugorokuon.v3.setting.SettingsSubComonent
import tsuyogoro.sugorokuon.v3.songs.OnAirSongsModule
import tsuyogoro.sugorokuon.v3.songs.OnAirSongsRootModule
import tsuyogoro.sugorokuon.v3.songs.OnAirSongsRootSubModule
import tsuyogoro.sugorokuon.v3.songs.OnAirSongsSubModule
import tsuyogoro.sugorokuon.v3.timetable.ProgramInfoModule
import tsuyogoro.sugorokuon.v3.timetable.ProgramInfoSubComponent
import tsuyogoro.sugorokuon.v3.timetable.ProgramTableModule
import tsuyogoro.sugorokuon.v3.timetable.ProgramTableSubComponent
import javax.inject.Singleton

@Singleton
@Component(modules = [
    SugorokuonAppModule::class,
    RadikoApiModule::class,
    ServiceModule::class,
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

    fun sugorokuonTopSubComponent(sugorokuonTopModule: SugorokuonTopModule)
            : SugorokuonTopSubComponent
}