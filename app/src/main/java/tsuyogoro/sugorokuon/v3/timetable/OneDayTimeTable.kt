package tsuyogoro.sugorokuon.v3.timetable

import tsuyogoro.sugorokuon.v3.api.response.StationResponse
import tsuyogoro.sugorokuon.v3.api.response.TimeTableResponse

data class OneDayTimeTable(
    val programs: List<TimeTableResponse.Program>,
    val station: StationResponse.Station
)