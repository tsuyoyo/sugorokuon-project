package tsuyogoro.sugorokuon.extension

import tsuyogoro.sugorokuon.api.response.TimeTableResponse

fun TimeTableResponse.searchProgram(id: String) : TimeTableResponse.Program? {
    stations.forEach {
        it.timeTable.programs
                .find { p -> p.id == id }
                ?.let { return it }
    }
    return null
}