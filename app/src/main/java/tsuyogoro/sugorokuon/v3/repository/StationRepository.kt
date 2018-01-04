package tsuyogoro.sugorokuon.v3.repository

import io.reactivex.Maybe
import tsuyogoro.sugorokuon.v3.api.StationApi
import tsuyogoro.sugorokuon.v3.api.response.StationResponse
import tsuyogoro.sugorokuon.v3.constant.Area

class StationRepository(
        private val stationApi: StationApi
) {

    fun fetchStations(area: Area): Maybe<List<StationResponse.Station>> =
            stationApi.getStations(area.id).map { it.stationList.filterNotNull() }

}