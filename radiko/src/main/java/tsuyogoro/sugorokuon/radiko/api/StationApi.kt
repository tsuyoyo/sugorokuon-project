/**
 * Copyright (c)
 * 2018 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.radiko.api

import io.reactivex.Maybe
import retrofit2.http.GET
import retrofit2.http.Path
import tsuyogoro.sugorokuon.radiko.api.response.StationResponse

interface StationApi {

    @GET("v3/station/list/{areaId}.xml")
    fun getStations(@Path("areaId") areaId: String): Maybe<StationResponse>

}
