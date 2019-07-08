package tsuyogoro.sugorokuon.timetable

import tsuyogoro.sugorokuon.radiko.api.response.TimeTableResponse
import tsuyogoro.sugorokuon.station.Station

data class OneDayTimeTable(
    val programs: List<TimeTableResponse.Program>,
    val station: Station
)