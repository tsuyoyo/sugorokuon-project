package tsuyogoro.sugorokuon.timetable

import dagger.Subcomponent

@Subcomponent(modules = [
    ProgramInfoModule::class
])
interface ProgramInfoSubComponent {

    fun inject(fragment: ProgramInfoFragment)
    
}