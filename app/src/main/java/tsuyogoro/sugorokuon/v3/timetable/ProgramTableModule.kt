package tsuyogoro.sugorokuon.v3.timetable

import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.v3.service.SettingsService
import tsuyogoro.sugorokuon.v3.service.TimeTableService

@Module
class ProgramTableModule {

    @Provides
    fun provideProgramTableViewModelFactory(
            timeTableService: TimeTableService,
            settingsService: SettingsService) =
            ProgramTableViewModel.Factory(timeTableService, settingsService)

}