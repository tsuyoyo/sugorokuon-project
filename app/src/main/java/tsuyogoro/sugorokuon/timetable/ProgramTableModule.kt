package tsuyogoro.sugorokuon.timetable

import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.recommend.RecommendProgramRepository
import tsuyogoro.sugorokuon.service.SettingsService
import tsuyogoro.sugorokuon.service.TimeTableService
import tsuyogoro.sugorokuon.station.StationRepository

@Module
class ProgramTableModule {

    @Provides
    fun provideProgramTableViewModelFactory(
        timeTableService: TimeTableService,
        recommendProgramRepository: RecommendProgramRepository,
        stationRepository: StationRepository,
        settingsService: SettingsService) =
        ProgramTableViewModel.Factory(
            timeTableService,
            recommendProgramRepository,
            stationRepository,
            settingsService
        )

}