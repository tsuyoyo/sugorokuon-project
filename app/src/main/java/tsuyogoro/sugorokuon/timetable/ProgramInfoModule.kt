package tsuyogoro.sugorokuon.timetable

import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.rx.SchedulerProvider
import tsuyogoro.sugorokuon.service.TimeTableService

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