package tsuyogoro.sugorokuon.v3.api

import io.reactivex.Flowable
import io.reactivex.Maybe
import retrofit2.http.GET
import retrofit2.http.Path
import tsuyogoro.sugorokuon.v3.api.response.FeedResponse

interface FeedApi {

    @GET("v3/feed/pc/noa/{stationId}.xml")
    fun getFeed(@Path("stationId") stationId: String): Maybe<FeedResponse>
}