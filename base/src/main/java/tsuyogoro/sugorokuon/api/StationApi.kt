package tsuyogoro.sugorokuon.api

import io.reactivex.Maybe
import retrofit2.http.GET
import retrofit2.http.Path
import tsuyogoro.sugorokuon.api.response.StationResponse

interface StationApi {

    @GET("v3/station/list/{areaId}.xml")
    fun getStations(@Path("areaId") areaId: String): Maybe<StationResponse>

}
