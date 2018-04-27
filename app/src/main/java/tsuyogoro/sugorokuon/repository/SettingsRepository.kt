package tsuyogoro.sugorokuon.repository

import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import tsuyogoro.sugorokuon.constant.Area
import tsuyogoro.sugorokuon.constant.SearchSongMethod
import tsuyogoro.sugorokuon.preference.AreaPrefs
import tsuyogoro.sugorokuon.preference.SearchMethodPrefs
import tsuyogoro.sugorokuon.preference.StationPrefs
import java.util.*

class SettingsRepository(
        private val areaPrefs: AreaPrefs,
        private val stationsPrefs: StationPrefs,
        private val searchMethodPrefs: SearchMethodPrefs,
        private val areaSettings: BehaviorProcessor<Set<Area>> = BehaviorProcessor.create(),
        private val selectedDate: BehaviorProcessor<Calendar> = BehaviorProcessor.create(),
        private val orderedStationIds: BehaviorProcessor<List<String>> = BehaviorProcessor.create(),
        private val waySearchSong: BehaviorProcessor<SearchSongMethod> =
            BehaviorProcessor.createDefault(SearchSongMethod.EVERY_TIME_SELECT)
) {

    init {
        areaSettings.onNext(areaPrefs.areaIds ?: emptySet() )
        orderedStationIds.onNext(
                stationsPrefs.displayOrder
                        ?.split(",")
                        ?.filter { it.isNotBlank() }
                        ?: emptyList())
        waySearchSong.onNext(searchMethodPrefs.searchSongWay)
    }

    fun setAreaSettings(areas: Set<Area>) {
        areaPrefs.putAreaIds(areas)
        areaSettings.onNext(areas)
    }

    /**
     * When area has not been set yet, then empty set is returned.
     */
    fun observeAreaSettings(): Flowable<Set<Area>> = areaSettings.hide()

    fun getAreaSettings(): Set<Area> = areaSettings.value

    fun setData(date: Calendar) {
        selectedDate.onNext(date)
    }

    fun observeDate(): Flowable<Calendar> = selectedDate.hide()

    fun setStationOrder(orderedStationIds: List<String>) {
        val saveValue = orderedStationIds.joinToString(",")
        this.stationsPrefs.displayOrder = saveValue
        this.orderedStationIds.onNext(orderedStationIds)
    }

    fun observeOrderedStationIds(): Flowable<List<String>> = orderedStationIds.hide()

    fun setWaySearchSong(method: SearchSongMethod) {
        this.searchMethodPrefs.searchSongWay = method
        waySearchSong.onNext(method)
    }

    fun observeSelectedWaySerachSong(): Flowable<SearchSongMethod> = waySearchSong.hide()
}