package tsuyogoro.sugorokuon.v3.timetable

import dagger.Subcomponent

@Subcomponent(modules = [
    ProgramInfoModule::class
])
interface ProgramInfoSubComponent {

    fun inject(fragment: ProgramInfoFragment)
    
}