package tsuyogoro.sugorokuon.v3.timetable

import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.v3.constant.Area
import tsuyogoro.sugorokuon.v3.repository.TimeTableRepository
import tsuyogoro.sugorokuon.v3.rx.SchedulerProvider
import java.util.*

@Module
class ProgramInfoModule(
        private val date: Calendar,
        private val area: Area,
        private val programId: String
) {

    @Provides
    fun provideProgramInfoViewModelFactory(
            timeTableRepository: TimeTableRepository,
            schedulerProvider: SchedulerProvider
    ) = ProgramInfoViewModel.Factory(
            date, area, programId, timeTableRepository, schedulerProvider)
}