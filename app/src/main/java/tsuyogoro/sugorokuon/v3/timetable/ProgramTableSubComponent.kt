package tsuyogoro.sugorokuon.v3.timetable

import dagger.Subcomponent

@Subcomponent(modules = [
    ProgramTableModule::class
])
interface ProgramTableSubComponent {

    fun inject(fragment: ProgramTableFragment)

}