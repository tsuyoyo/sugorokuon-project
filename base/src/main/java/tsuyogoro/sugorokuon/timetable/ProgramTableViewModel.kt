package tsuyogoro.sugorokuon.timetable

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import tsuyogoro.sugorokuon.recommend.RecommendProgramRepository
import tsuyogoro.sugorokuon.recommend.RecommendSearchService
import tsuyogoro.sugorokuon.recommend.settings.RecommendSettingsRepository
import tsuyogoro.sugorokuon.service.SettingsService
import tsuyogoro.sugorokuon.service.TimeTableService
import tsuyogoro.sugorokuon.station.Station
import tsuyogoro.sugorokuon.station.StationRepository
import java.util.*

class ProgramTableViewModel(
        timeTableService: TimeTableService,
        recommendProgramRepository: RecommendProgramRepository,
        stationRepository: StationRepository,
        private val settingsService: SettingsService
) : ViewModel() {

    @Suppress("UNCHECKED_CAST")
    class Factory(
            private val timeTableService: TimeTableService,
            private val recommendProgramRepository: RecommendProgramRepository,
            private val stationRepository: StationRepository,
            private val settingsService: SettingsService
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ProgramTableViewModel(
                timeTableService,
                recommendProgramRepository,
                stationRepository,
                settingsService
            ) as T
        }
    }

    private val disposable: CompositeDisposable = CompositeDisposable()

    private val selectedDate: MutableLiveData<Calendar> = MutableLiveData()
    private val timeTablesData: MutableLiveData<List<OneDayTimeTable>> = MutableLiveData()
    private val recommendPrograms: MutableLiveData<List<RecommendProgramData>> = MutableLiveData()
    private val isLoading: MutableLiveData<Boolean> = MutableLiveData()

    init {
        disposable.addAll(
                Flowable.combineLatest(
                        timeTableService.observeOneDayTimeTables(),
                        settingsService.observeOrderedStations(),
                        BiFunction { timeTables: List<OneDayTimeTable>,
                                     orderedStations: List<Station> ->
                            // Sort timeTables according to user's settings.
                            val orderedTimeTables: MutableList<OneDayTimeTable> = mutableListOf()
                            orderedStations.forEach { s ->
                                timeTables
                                        .find { it.station.id == s.id }
                                        ?.let(orderedTimeTables::add)
                            }
                            return@BiFunction orderedTimeTables
                        })
                        .doOnSubscribe { isLoading.postValue(true) }
                        .doOnNext {
                            isLoading.postValue(false)
                            timeTablesData.postValue(it.toList())
                        }
                        .subscribe(),

                settingsService
                        .observeDate()
                        .doOnNext { selectedDate.postValue(it) }
                        .subscribe()
        )
        recommendProgramRepository
            .observeRecommendPrograms()
            .observeForever {
                val recommends = (it?.map { p ->
                    stationRepository
                        .getStations()
                        .find { it.id == p.stationId }
                        ?.let { station -> RecommendProgramData(p, station) }
                } ?: emptyList<RecommendProgramData>()).filterNotNull()

                recommendPrograms.postValue(recommends)
            }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    fun observeSelectedDate(): LiveData<Calendar> = selectedDate

    fun observeTimeTable() : LiveData<List<OneDayTimeTable>> = timeTablesData

    fun observeIsLoading(): LiveData<Boolean> = isLoading

    fun observeRecommendPrograms(): LiveData<List<RecommendProgramData>> = recommendPrograms

    fun selectDate(date: Calendar) {
        settingsService.setDate(date)
    }
}