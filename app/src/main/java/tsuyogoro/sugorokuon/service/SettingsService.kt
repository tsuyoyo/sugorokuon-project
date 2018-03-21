package tsuyogoro.sugorokuon.service

import io.reactivex.Flowable
import io.reactivex.functions.BiFunction
import tsuyogoro.sugorokuon.api.response.StationResponse
import tsuyogoro.sugorokuon.constant.Area
import tsuyogoro.sugorokuon.repository.SettingsRepository
import java.util.*

class SettingsService(
        private val settingsRepository: SettingsRepository,
        private val stationService: StationService
) {
    init {
        val now = Calendar.getInstance()
        settingsRepository.setData(
                if (now.get(Calendar.HOUR_OF_DAY) < 5) {
                    now.apply { add(Calendar.DAY_OF_MONTH, -1) }
                } else {
                    now
                }
        )
    }

    fun setDate(date: Calendar) {
        settingsRepository.setData(date)
    }

    fun observeDate(): Flowable<Calendar> = settingsRepository.observeDate()

    fun selectArea(area: Area) {
        settingsRepository.setAreaSettings(
                settingsRepository
                        .getAreaSettings()
                        .toMutableSet()
                        .apply { add(area) }
        )
    }

    fun deselectArea(area: Area) {
        settingsRepository.setAreaSettings(
                settingsRepository
                        .getAreaSettings()
                        .toMutableSet()
                        .apply { remove(area) }
        )
    }

    fun observeAreas(): Flowable<Set<Area>> = settingsRepository.observeAreaSettings()

    fun observeOrderedStations(): Flowable<List<StationResponse.Station>> =
            Flowable.combineLatest(
                    settingsRepository.observeOrderedStationIds(),
                    stationService.observeStations(),
                    BiFunction { ids: List<String>, stations: List<StationResponse.Station> ->
                        if (ids.isEmpty()) {
                            return@BiFunction stations
                        } else {
                            val orderedStations = mutableListOf<StationResponse.Station>()
                            ids.forEach { id ->
                                stations.find { it.id == id }
                                        ?.let(orderedStations::add)
                            }
                            return@BiFunction orderedStations
                        }
                    }
            )

    fun updateStationOrder(orderedStations: List<StationResponse.Station>) {
        settingsRepository.setStationOrder(orderedStations.map { it.id })
    }

    fun clearStationOrder() {
        settingsRepository.setStationOrder(emptyList())
    }

}