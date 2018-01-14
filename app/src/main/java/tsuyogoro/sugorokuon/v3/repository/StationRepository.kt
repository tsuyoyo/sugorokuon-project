package tsuyogoro.sugorokuon.v3.repository

import io.reactivex.Maybe
import tsuyogoro.sugorokuon.v3.api.StationApi
import tsuyogoro.sugorokuon.v3.api.response.StationResponse
import tsuyogoro.sugorokuon.v3.constant.Area

class StationRepository(
        private val stationApi: StationApi,
        private val cachedResponse: MutableMap<String, StationResponse> = mutableMapOf()
) {

    fun fetchStations(area: Area,
                      forceUpdate: Boolean = false): Maybe<List<StationResponse.Station>> =
            if (forceUpdate || cachedResponse[area.id] == null) {
                stationApi.getStations(area.id)
                        .doOnSuccess { cachedResponse.put(area.id, it) }
                        .map { it.stationList.filterNotNull() }
            } else {
                Maybe.just(cachedResponse[area.id]?.stationList ?: emptyList())
            }

}