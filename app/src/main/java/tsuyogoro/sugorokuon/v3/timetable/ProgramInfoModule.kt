package tsuyogoro.sugorokuon.v3.timetable

import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.v3.service.TimeTableService

@Module
class ProgramInfoModule(
        private val programId: String
) {
    @Provides
    fun provideProgramInfoViewModelFactory(timeTableService: TimeTableService)
            = ProgramInfoViewModel.Factory(timeTableService, programId)
}