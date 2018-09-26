package tsuyogoro.sugorokuon.di

import dagger.Component
import tsuyogoro.sugorokuon.SugorokuonTopModule
import tsuyogoro.sugorokuon.SugorokuonTopSubComponent
import tsuyogoro.sugorokuon.data.DataModule
import tsuyogoro.sugorokuon.onboarding.OnboardingComponent
import tsuyogoro.sugorokuon.onboarding.OnboardingModule
import tsuyogoro.sugorokuon.radiko.RadikoApiModule
import tsuyogoro.sugorokuon.search.SearchModule
import tsuyogoro.sugorokuon.search.SearchSubComponent
import tsuyogoro.sugorokuon.setting.SettingsModule
import tsuyogoro.sugorokuon.setting.SettingsSubComponent
import tsuyogoro.sugorokuon.songs.OnAirSongsModule
import tsuyogoro.sugorokuon.songs.OnAirSongsRootModule
import tsuyogoro.sugorokuon.songs.OnAirSongsRootSubModule
import tsuyogoro.sugorokuon.songs.OnAirSongsSubModule
import tsuyogoro.sugorokuon.timetable.ProgramInfoModule
import tsuyogoro.sugorokuon.timetable.ProgramInfoSubComponent
import tsuyogoro.sugorokuon.timetable.ProgramTableModule
import tsuyogoro.sugorokuon.timetable.ProgramTableSubComponent
import javax.inject.Singleton

@Singleton
@Component(modules = [
    SugorokuonAppModule::class,
    RadikoApiModule::class,
    ServiceModule::class,
    RepositoryModule::class,
    DataModule::class
])
interface SugorokuonAppComponent {

    fun settingSubComponent(settingsModule: SettingsModule)
            : SettingsSubComponent

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

    fun searchSubComponent(searchModule: SearchModule)
            : SearchSubComponent

    fun onBoardingSubComponent(onboardingModule: OnboardingModule)
            : OnboardingComponent

}