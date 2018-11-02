package tsuyogoro.sugorokuon.timetable

import tsuyogoro.sugorokuon.recommend.RecommendProgram
import tsuyogoro.sugorokuon.station.Station

data class RecommendProgramData(
    val program: RecommendProgram,
    val station: Station
)