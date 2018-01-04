package tsuyogoro.sugorokuon.v3.repository

import io.reactivex.processors.BehaviorProcessor
import tsuyogoro.sugorokuon.v3.constant.Area
import tsuyogoro.sugorokuon.v3.preference.AreaPrefs

class SettingsRepository(
        private val areaPrefs: AreaPrefs,
        private val areaSettings: BehaviorProcessor<Set<Area>> = BehaviorProcessor.create()
) {

    init {
        areaSettings.onNext(areaPrefs.areaIds)
    }

    fun setAreaSettings(areas: Set<Area>) {
        areaPrefs.putAreaIds(areas)
        areaSettings.onNext(areas)
    }

    fun observeAreaSettings(): BehaviorProcessor<Set<Area>> = areaSettings

}