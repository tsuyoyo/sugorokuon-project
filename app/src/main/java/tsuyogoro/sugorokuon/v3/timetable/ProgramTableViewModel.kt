package tsuyogoro.sugorokuon.v3.timetable

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.util.Log
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import tsuyogoro.sugorokuon.v3.api.response.StationResponse
import tsuyogoro.sugorokuon.v3.api.response.TimeTableResponse
import tsuyogoro.sugorokuon.v3.constant.Area
import tsuyogoro.sugorokuon.v3.repository.SettingsRepository
import tsuyogoro.sugorokuon.v3.repository.StationRepository
import tsuyogoro.sugorokuon.v3.repository.TimeTableRepository
import tsuyogoro.sugorokuon.v3.rx.SchedulerProvider
import java.util.*

class ProgramTableViewModel(
        private val timeTableRepository: TimeTableRepository,
        private val stationRepository: StationRepository,
        private val settingsRepository: SettingsRepository,
        private val schedulerProvider: SchedulerProvider,
        private val disposable: CompositeDisposable = CompositeDisposable()
) : ViewModel() {

    @Suppress("UNCHECKED_CAST")
    class Factory(
            private val settingsRepository: SettingsRepository,
            private val timeTableRepository: TimeTableRepository,
            private val stationRepository: StationRepository,
            private val schedulerProvider: SchedulerProvider
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ProgramTableViewModel(
                    timeTableRepository,
                    stationRepository,
                    settingsRepository,
                    schedulerProvider) as T
        }
    }

    private val timeTables: MutableList<OneDayTimeTable> = mutableListOf()

    private val selectedDate: MutableLiveData<Calendar> = MutableLiveData()
    private val timeTablesData: MutableLiveData<List<OneDayTimeTable>> = MutableLiveData()
    private val isLoading: MutableLiveData<Boolean> = MutableLiveData()

    init {
        disposable.addAll(
                observeTriggerToFetchTimeTable()
                        .doOnNext { isLoading.postValue(true) }
                        .flatMapCompletable { (selectedDate: Calendar, selectedAreas: Set<Area>) ->
                            return@flatMapCompletable fetchTimeTables(selectedDate, selectedAreas)
                                    .subscribeOn(schedulerProvider.io())
                                    .doOnComplete {
                                        timeTablesData.postValue(timeTables)
                                        isLoading.postValue(false)
                                    }
                                    .doOnError { isLoading.postValue(false) }
                                    .observeOn(schedulerProvider.mainThread())
                        }
                        .onErrorResumeNext { e ->
                            Log.d("TestTestTest", "API error : ${e.message}")
                            Completable.complete()
                        }
                        .subscribeOn(schedulerProvider.io())
                        .subscribe()
                ,
                observeSelectedDateAndNormalize()
                        .doOnNext { selectedDate.postValue(it) }
                        .subscribeOn(schedulerProvider.io())
                        .subscribe()
        )
    }

    private fun observeSelectedDateAndNormalize() = timeTableRepository
            .observeSelectedDate()
            .map {
                if (it.get(Calendar.HOUR_OF_DAY) < 5) {
                    it.apply { add(Calendar.DAY_OF_MONTH, -1) }
                } else {
                    it
                }
            }

    private fun observeTriggerToFetchTimeTable() = Flowable.combineLatest(
            observeSelectedDateAndNormalize(),
            settingsRepository.observeAreaSettings(),
            BiFunction { selectedDate: Calendar, selectedAreas: Set<Area> ->
                Pair(selectedDate, selectedAreas)
            })

    private fun fetchTimeTables(selectedDate: Calendar, selectedAreas: Set<Area>) =
            Completable.concat(mutableListOf<Completable>().apply {
                val timeTables = mutableListOf<OneDayTimeTable>()

                selectedAreas.forEach { area ->
                    val fetchProcess = Maybe.zip(
                            timeTableRepository.fetchTimeTable(selectedDate, area),
                            stationRepository.fetchStations(area),
                            BiFunction {
                                timeTableResponse: TimeTableResponse,
                                stations: List<StationResponse.Station> ->
                                Pair(timeTableResponse, stations)
                            })
                            .flatMapPublisher {
                                (timeTableResponse: TimeTableResponse,
                                        stations: List<StationResponse.Station>) ->

                                Flowable.fromIterable(timeTableResponse.stations)
                                        .filter {
                                            timeTables.find { s ->
                                                s.station.id == it.id } == null
                                        }
                                        .doOnNext {
                                            val station = stations.find { s -> s.id == it.id }
                                            if (station != null) {
                                                timeTables.add(
                                                        OneDayTimeTable(
                                                                it.timeTable.programs.filterNotNull(),
                                                                station
                                                        )
                                                )
                                            }
                                        }
                            }
                            .ignoreElements()

                    this.add(fetchProcess)
                }
                this.add(Completable.fromAction {
                    this@ProgramTableViewModel.timeTables.clear()
                    this@ProgramTableViewModel.timeTables.addAll(timeTables)

                    Log.d("TestTestTest", "complete : ${this@ProgramTableViewModel.timeTables.size}")
                })
            })

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    fun observeSelectedDate(): LiveData<Calendar> = selectedDate

    fun observeTimeTable() : LiveData<List<OneDayTimeTable>> = timeTablesData

    fun observeIsLoading(): LiveData<Boolean> = isLoading

    fun selectDate(date: Calendar) {
        timeTableRepository.selectDate(date)
    }
}