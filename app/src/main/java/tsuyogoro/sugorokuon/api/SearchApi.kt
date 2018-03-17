package tsuyogoro.sugorokuon.api

import io.reactivex.Maybe
import retrofit2.http.GET
import retrofit2.http.Query
import tsuyogoro.sugorokuon.api.response.SearchResponse

interface SearchApi {

//    http://radiko.jp/v3/api/program/search?key=music&filter=&start_day=&end_day=&area_id=JP13&region_id=&cul_area_id=JP13&page_idx=&uid=bbe7a3b3775ff55cbc886b820e634ec7&row_limit=12&app_id=pc&action_id=0
    @GET("v3/api/program/search")
    fun search(@Query("key", encoded = true) encodedWord: String,
               @Query("area_id") areaId: String,
               @Query("cul_area_id") culAreaId: String,
               @Query("uid") uid: String,
               @Query("filter") filter: String = "",
               @Query("start_day") startDay: String = "",
               @Query("end_day") endDay: String = "",
               @Query("region_id") regionId: String = "",
               @Query("page_idx") pageIdx: String = "0",
               @Query("row_limit") rowLimit: Int = 12,
               @Query("app_id") appId: String = "pc",
               @Query("action_id") actionId : String = "0"): Maybe<SearchResponse>

}