package tsuyogoro.sugorokuon.v3.timetable

import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.v3.repository.SettingsRepository
import tsuyogoro.sugorokuon.v3.repository.StationRepository
import tsuyogoro.sugorokuon.v3.repository.TimeTableRepository
import tsuyogoro.sugorokuon.v3.rx.SchedulerProviderForApp

@Module
class ProgramTableModule {

    @Provides
    fun provideProgramTableViewModelFactory(
            settingsRepository: SettingsRepository,
            timeTableRepository: TimeTableRepository,
            stationRepository: StationRepository
    ) = ProgramTableViewModel.Factory(
            settingsRepository, timeTableRepository, stationRepository, SchedulerProviderForApp())

}