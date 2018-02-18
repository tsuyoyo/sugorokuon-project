package tsuyogoro.sugorokuon.v3.service

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import tsuyogoro.sugorokuon.v3.api.StationApi
import tsuyogoro.sugorokuon.v3.api.response.StationResponse
import tsuyogoro.sugorokuon.v3.constant.Area
import tsuyogoro.sugorokuon.v3.repository.StationRepository

class StationService(
        private val stationApi: StationApi,
        private val stationRepository: StationRepository
) {

    fun observeStations(): Flowable<List<StationResponse.Station>> = stationRepository
            .observeStationResponses()
            .map { responses ->
                val stations = mutableSetOf<StationResponse.Station>()

                // TODO : これだと重複してしまうので、重複チェックしながらいれるようにする
                responses.forEach {
                    stations.addAll(it.stationList)
                }
                return@map stations.toList()
            }

    fun fetchStation(areas: List<Area>): Completable {
        val completables = mutableListOf<Completable>()
        val responses = mutableListOf<StationResponse>()
        areas.forEach {
            completables.add(
                    stationApi.getStations(it.id)
                            .doOnSuccess { responses.add(it) }
                            .ignoreElement()
            )
        }
        return Completable.concat(completables)
                .doOnComplete { stationRepository.setStationResponses(responses) }
    }

    /**
     * Get list of stations in specified area.
     *
     */
    fun getStationsInArea(area: Area): Maybe<List<StationResponse.Station>> = stationRepository
            .observeStationResponses()
            .firstElement()
            .flatMap {
                Maybe.fromCallable {
                    val resForArea = it.find { r -> r.areaId == area.id }
                    if (resForArea == null) {
                        throw IllegalArgumentException("${area.id} is not found in fetched result")
                    } else {
                        return@fromCallable resForArea.stationList
                    }
                }
            }

}