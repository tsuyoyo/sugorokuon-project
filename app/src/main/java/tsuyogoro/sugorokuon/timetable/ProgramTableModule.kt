package tsuyogoro.sugorokuon.timetable

import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.service.SettingsService
import tsuyogoro.sugorokuon.service.TimeTableService

@Module
class ProgramTableModule {

    @Provides
    fun provideProgramTableViewModelFactory(
            timeTableService: TimeTableService,
            settingsService: SettingsService) =
            ProgramTableViewModel.Factory(timeTableService, settingsService)

}