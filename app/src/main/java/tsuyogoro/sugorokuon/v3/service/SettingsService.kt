package tsuyogoro.sugorokuon.v3.service

import io.reactivex.Flowable
import tsuyogoro.sugorokuon.v3.constant.Area
import tsuyogoro.sugorokuon.v3.repository.SettingsRepository
import java.util.*

class SettingsService(
        private val settingsRepository: SettingsRepository
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

    fun setAreas(areas: Set<Area>) {
        settingsRepository.setAreaSettings(areas)
    }

    fun observeAreas(): Flowable<Set<Area>> = settingsRepository.observeAreaSettings()

}