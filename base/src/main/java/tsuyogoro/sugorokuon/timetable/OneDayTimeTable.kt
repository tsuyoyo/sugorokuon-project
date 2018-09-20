package tsuyogoro.sugorokuon.timetable

import tsuyogoro.sugorokuon.radiko.api.response.StationResponse
import tsuyogoro.sugorokuon.radiko.api.response.TimeTableResponse

data class OneDayTimeTable(
    val programs: List<TimeTableResponse.Program>,
    val station: StationResponse.Station
)