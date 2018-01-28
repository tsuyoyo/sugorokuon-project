package tsuyogoro.sugorokuon.v3.repository

import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import tsuyogoro.sugorokuon.v3.api.response.TimeTableResponse
import tsuyogoro.sugorokuon.v3.constant.Area
import java.util.*

class TimeTableRepository(
        private val cachedTimeTableResponse: BehaviorProcessor<Map<String, TimeTableResponse>> =
            BehaviorProcessor.createDefault(emptyMap())
) {


    /**
     * Key of map is areaId, value is TimeTableResponse.
     *
     */
    fun observeAllResponses(): Flowable<Map<String, TimeTableResponse>> =
            cachedTimeTableResponse.hide()


    fun setTimeTableResponse(area: Area, response: TimeTableResponse) {
        cachedTimeTableResponse.onNext(
                cachedTimeTableResponse.value.toMutableMap().apply {
                    put(area.id, response)
                }
        )
    }

//    fun fetchTimeTable(date: Calendar,
//                       area: Area,
//                       update: Boolean = false): Maybe<TimeTableResponse> =
//            String.format("%04d%02d%02d",
//                    date.get(Calendar.YEAR),
//                    date.get(Calendar.MONTH) + 1,
//                    date.get(Calendar.DAY_OF_MONTH)
//            ).let { apiParam ->
//                val mapKey = "${apiParam}${area.id}"
//                if (update || cachedTimeTableResponse[mapKey] == null) {
//                    timeTableApi
//                            .getTimeTable(apiParam, area.id)
//                            .doOnSuccess { cachedTimeTableResponse.put(mapKey, it) }
//                } else {
//                    Maybe.just(cachedTimeTableResponse[mapKey])
//                }
//            }
}