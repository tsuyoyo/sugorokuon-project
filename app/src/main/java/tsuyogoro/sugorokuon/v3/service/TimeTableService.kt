package tsuyogoro.sugorokuon.v3.service

import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.functions.BiFunction
import tsuyogoro.sugorokuon.v3.api.TimeTableApi
import tsuyogoro.sugorokuon.v3.api.response.StationResponse
import tsuyogoro.sugorokuon.v3.api.response.TimeTableResponse
import tsuyogoro.sugorokuon.v3.constant.Area
import tsuyogoro.sugorokuon.v3.repository.TimeTableRepository
import tsuyogoro.sugorokuon.v3.timetable.OneDayTimeTable
import java.util.*

class TimeTableService(
        private val timeTableApi: TimeTableApi,
        private val stationService: StationService,
        private val timeTableRepository: TimeTableRepository
) {

    fun fetchTimeTable(date: Calendar, areas: List<Area>): Completable =
            makeAreaFlowable(areas)
                    .flatMapCompletable { fetchTimeTable(date, it) }

    fun fetchTimeTable(date: Calendar, area: Area): Completable =
            timeTableApi.getTimeTable(buildApiParams(date), area.id)
                    .doOnSuccess { timeTableRepository.setTimeTableResponse(area, it) }
                    .ignoreElement()

    fun getProgram(programId: String) : Maybe<TimeTableResponse.Program> =
            observeTimeTable().firstElement()
                    .flatMap { timeTableStations ->
                        val program = findProgram(programId, timeTableStations.map { it.timeTable })
                        if (program != null) {
                            return@flatMap Maybe.just(program)
                        } else {
                            return@flatMap Maybe.error<TimeTableResponse.Program>(
                                    IllegalArgumentException("Unavailable program is searched"))
                        }
                    }

    fun observeOneDayTimeTables(): Flowable<List<OneDayTimeTable>> =
            Flowable.combineLatest(
                    observeTimeTable(),
                    stationService.observeStations(),
                    BiFunction { timeTables: List<TimeTableResponse.Station>,
                                 stations: List<StationResponse.Station> ->

                        return@BiFunction mutableListOf<OneDayTimeTable>().apply {
                            timeTables.forEach { timeTable ->
                                val station = stations.find { s -> s.id == timeTable.id }
                                if (station != null) {
                                    add(OneDayTimeTable(timeTable.timeTable.programs, station))
                                }
                            }
                        }
                    }
            )

    private fun observeTimeTable(): Flowable<List<TimeTableResponse.Station>> =
            timeTableRepository.observeAllResponses()
                    .map { allResponses ->
                        val results = mutableListOf<TimeTableResponse.Station>()
                        // allResponses includes response for some areas.
                        allResponses.values.forEach { response ->
                            // set response to results not to duplicate station.
                            response.stations.forEach { station ->
                                if (results.find { it.id == station.id } == null) {
                                    results.add(station)
                                }
                            }
                        }
                        return@map results
                    }

    private fun makeAreaFlowable(areas: List<Area>) = Flowable
            .create<Area>({ emitter ->
                areas.forEach(emitter::onNext)
                emitter.onComplete()
            }, BackpressureStrategy.LATEST)

    private fun buildApiParams(date: Calendar) =
            String.format("%04d%02d%02d",
                    date.get(Calendar.YEAR),
                    date.get(Calendar.MONTH) + 1,
                    date.get(Calendar.DAY_OF_MONTH))

    private fun findProgram(programId: String, timeTables: List<TimeTableResponse.TimeTable>)
            : TimeTableResponse.Program? {
        timeTables.forEach {
            val program = it.programs.find { p -> p.id == programId }
            if (program != null) {
                return program
            }
        }
        return null
    }
}