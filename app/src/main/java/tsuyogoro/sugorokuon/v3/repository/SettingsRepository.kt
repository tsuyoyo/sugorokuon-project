package tsuyogoro.sugorokuon.v3.repository

import io.reactivex.processors.BehaviorProcessor
import tsuyogoro.sugorokuon.v3.constant.Area
import tsuyogoro.sugorokuon.v3.preference.AreaPrefs
import java.util.*

class SettingsRepository(
        private val areaPrefs: AreaPrefs,
        private val areaSettings: BehaviorProcessor<Set<Area>> = BehaviorProcessor.create(),
        private val selectedDate: BehaviorProcessor<Calendar> = BehaviorProcessor.create()
) {

    init {
        areaSettings.onNext(areaPrefs.areaIds ?: emptySet() )
    }

    fun setAreaSettings(areas: Set<Area>) {
        areaPrefs.putAreaIds(areas)
        areaSettings.onNext(areas)
    }

    /**
     * When area has not been set yet, then empty set is returned.
     */
    fun observeAreaSettings(): BehaviorProcessor<Set<Area>> = areaSettings

    fun observeDate(): BehaviorProcessor<Calendar> = selectedDate

    fun setData(date: Calendar) {
        selectedDate.onNext(date)
    }
}