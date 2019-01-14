package tsuyogoro.sugorokuon.timetable

import dagger.Subcomponent

@Subcomponent(modules = [
    ProgramTableModule::class
])
interface ProgramTableSubComponent {

    fun inject(fragment: ProgramTableFragment)

}