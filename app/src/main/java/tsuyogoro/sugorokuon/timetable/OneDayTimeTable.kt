package tsuyogoro.sugorokuon.timetable

import tsuyogoro.sugorokuon.api.response.StationResponse
import tsuyogoro.sugorokuon.api.response.TimeTableResponse

data class OneDayTimeTable(
    val programs: List<TimeTableResponse.Program>,
    val station: StationResponse.Station
)