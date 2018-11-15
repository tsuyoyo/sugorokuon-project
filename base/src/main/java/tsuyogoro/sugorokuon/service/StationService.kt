package tsuyogoro.sugorokuon.service

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import tsuyogoro.sugorokuon.constant.Area
import tsuyogoro.sugorokuon.extension.convertToStations
import tsuyogoro.sugorokuon.radiko.api.StationApi
import tsuyogoro.sugorokuon.radiko.api.response.StationResponse
import tsuyogoro.sugorokuon.station.Station
import tsuyogoro.sugorokuon.station.StationRepository

class StationService(
    private val stationApi: StationApi,
    private val stationRepository: StationRepository
) {

    fun observeStations(): Flowable<List<Station>> = stationRepository
        .observeStations()
        .map {
            val stations = mutableSetOf<Station>()
            it.forEach { s ->
                if (stations.find { it.id == s.id } == null) {
                    stations.add(s)
                }
            }
            return@map stations.toList()
        }

    fun fetchStation(areas: List<Area>): Completable {
        val completables = mutableListOf<Completable>()
        val stations = mutableListOf<Station>()
        areas.forEach {
            completables.add(
                stationApi
                    .getStations(it.id)
                    .doOnSuccess { response ->
                        response
                            .convertToStations()
                            .forEach { s ->
                                if (stations.find { it.id == s.id } == null) {
                                    stations.add(s)
                                }
                            }
                    }
                    .ignoreElement()
            )
        }
        return Completable
            .concat(completables)
            .doOnComplete { stationRepository.setStations(stations) }
    }

    /**
     * Get list of stations in specified area.
     *
     */
    fun getStationsInArea(area: Area): Maybe<List<Station>> = stationRepository
        .observeStations()
        .firstElement()
        .map { it.filter { s -> s.area == area } }

}