package tsuyogoro.sugorokuon.v3.extension

import tsuyogoro.sugorokuon.v3.api.response.TimeTableResponse

fun TimeTableResponse.searchProgram(id: String) : TimeTableResponse.Program? {
    stations.forEach {
        it.timeTable.programs
                .find { p -> p.id == id }
                ?.let { return it }
    }
    return null
}