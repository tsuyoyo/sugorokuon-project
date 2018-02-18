package tsuyogoro.sugorokuon.v3.timetable

import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.v3.rx.SchedulerProvider
import tsuyogoro.sugorokuon.v3.service.TimeTableService

@Module
class ProgramInfoModule(
        private val programId: String
) {
    @Provides
    fun provideProgramInfoViewModelFactory(
            timeTableService: TimeTableService,
            schedulerProvider: SchedulerProvider
    ) = ProgramInfoViewModel.Factory(timeTableService, programId, schedulerProvider)
}