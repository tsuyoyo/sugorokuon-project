package tsuyogoro.sugorokuon.station

import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor

class StationRepository {

    private val stations: BehaviorProcessor<List<Station>> = BehaviorProcessor.create()

    private lateinit var stationDao: StationDao

    internal fun initialize(stationDao: StationDao) {
        this.stationDao = stationDao
        updateStations()
    }

    fun setStations(stations: List<Station>) {
        stationDao.insert(stations)
        updateStations()
    }

    fun clear() {
        stationDao.clearTable()
        updateStations()
    }

    fun observeStations(): Flowable<List<Station>> = stations.hide()

    fun getStations(): List<Station> = stations.value

    private fun updateStations() {
        stations.onNext(stationDao.getAll())
    }
}