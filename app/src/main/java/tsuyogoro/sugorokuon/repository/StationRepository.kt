package tsuyogoro.sugorokuon.repository

import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import tsuyogoro.sugorokuon.api.response.StationResponse

class StationRepository(
        private val stationResponses: BehaviorProcessor<List<StationResponse>>
            = BehaviorProcessor.create()
) {

    fun setStationResponse(response: StationResponse) {
        stationResponses.onNext(listOf(response))
    }

    fun setStationResponses(responses: List<StationResponse>) {
        stationResponses.onNext(responses)
    }

    fun observeStationResponses() : Flowable<List<StationResponse>> = stationResponses

//
//
//    fun fetchStations(area: Area,
//                      forceUpdate: Boolean = false): Maybe<List<StationResponse.Station>> =
//            if (forceUpdate || cachedResponse[area.id] == null) {
//                stationApi.getStations(area.id)
//                        .doOnSuccess { cachedResponse.put(area.id, it) }
//                        .map { it.stationList.filterNotNull() }
//            } else {
//                Maybe.just(cachedResponse[area.id]?.stationList ?: emptyList())
//            }

}