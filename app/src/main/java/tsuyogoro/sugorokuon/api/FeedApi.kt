package tsuyogoro.sugorokuon.api

import io.reactivex.Maybe
import retrofit2.http.GET
import retrofit2.http.Path
import tsuyogoro.sugorokuon.api.response.FeedResponse

interface FeedApi {

    @GET("v3/feed/pc/noa/{stationId}.xml")
    fun getFeed(@Path("stationId") stationId: String): Maybe<FeedResponse>
}