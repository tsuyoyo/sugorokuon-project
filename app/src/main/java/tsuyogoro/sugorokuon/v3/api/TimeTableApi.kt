package tsuyogoro.sugorokuon.v3.api

import io.reactivex.Maybe
import retrofit2.http.GET
import retrofit2.http.Path
import tsuyogoro.sugorokuon.v3.api.response.TimeTableResponse

interface TimeTableApi {

    @GET("v3/program/date/{date}/{region}.xml")
    fun getTimeTable(
            @Path("date") date: String,
            @Path("region") region: String) : Maybe<TimeTableResponse>

}