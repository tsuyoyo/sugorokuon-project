package tsuyogoro.sugorokuon.v3.timetable

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import io.reactivex.disposables.CompositeDisposable
import tsuyogoro.sugorokuon.v3.service.SettingsService
import tsuyogoro.sugorokuon.v3.service.TimeTableService
import java.util.*

class ProgramTableViewModel(
        private val timeTableService: TimeTableService,
        private val settingsService: SettingsService,
        private val disposable: CompositeDisposable = CompositeDisposable()
) : ViewModel() {

    @Suppress("UNCHECKED_CAST")
    class Factory(
            private val timeTableService: TimeTableService,
            private val settingsService: SettingsService
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ProgramTableViewModel(timeTableService, settingsService) as T
        }
    }

    private val selectedDate: MutableLiveData<Calendar> = MutableLiveData()
    private val timeTablesData: MutableLiveData<List<OneDayTimeTable>> = MutableLiveData()
    private val isLoading: MutableLiveData<Boolean> = MutableLiveData()

    init {
        disposable.addAll(
                timeTableService
                        .observeOneDayTimeTables()
                        .doOnSubscribe { isLoading.postValue(true) }
                        .doOnNext {
                            isLoading.postValue(false)
                            timeTablesData.postValue(it)
                        }
                        .subscribe(),

                settingsService
                        .observeDate()
                        .doOnNext { selectedDate.postValue(it) }
                        .subscribe()
        )
//        disposable.addAll(
//                observeTriggerToFetchTimeTable()
//                        .doOnNext { isLoading.postValue(true) }
//                        .flatMapCompletable { (selectedDate: Calendar, selectedAreas: Set<Area>) ->
//                            return@flatMapCompletable fetchTimeTables(selectedDate, selectedAreas)
//                                    .subscribeOn(schedulerProvider.io())
//                                    .doOnComplete {
//                                        timeTablesData.postValue(timeTables)
//                                        isLoading.postValue(false)
//                                    }
//                                    .doOnError { isLoading.postValue(false) }
//                                    .observeOn(schedulerProvider.mainThread())
//                        }
//                        .onErrorResumeNext { e ->
//                            Log.d("TestTestTest", "API error : ${e.message}")
//                            Completable.complete()
//                        }
//                        .subscribeOn(schedulerProvider.io())
//                        .subscribe()
//                ,
//                observeSelectedDateAndNormalize()
//                        .doOnNext { selectedDate.postValue(it) }
//                        .subscribeOn(schedulerProvider.io())
//                        .subscribe()
//        )
    }


//    private fun observeTriggerToFetchTimeTable() = Flowable.combineLatest(
//            observeSelectedDateAndNormalize(),
//            settingsRepository.observeAreaSettings(),
//            BiFunction { selectedDate: Calendar, selectedAreas: Set<Area> ->
//                Pair(selectedDate, selectedAreas)
//            })
//
//    private fun fetchTimeTables(selectedDate: Calendar, selectedAreas: Set<Area>) =
//            Completable.concat(mutableListOf<Completable>().apply {
//                val timeTables = mutableListOf<OneDayTimeTable>()
//
//                selectedAreas.forEach { area ->
//                    val fetchProcess = Maybe.zip(
//                            timeTableRepository.fetchTimeTable(selectedDate, area),
//                            stationService.fetchStations(area),
//                            BiFunction {
//                                timeTableResponse: TimeTableResponse,
//                                stations: List<StationResponse.Station> ->
//                                Pair(timeTableResponse, stations)
//                            })
//                            .flatMapPublisher {
//                                (timeTableResponse: TimeTableResponse,
//                                        stations: List<StationResponse.Station>) ->
//
//                                Flowable.fromIterable(timeTableResponse.stations)
//                                        .filter {
//                                            timeTables.find { s ->
//                                                s.station.id == it.id } == null
//                                        }
//                                        .doOnNext {
//                                            val station = stations.find { s -> s.id == it.id }
//                                            if (station != null) {
//                                                timeTables.set(
//                                                        OneDayTimeTable(
//                                                                it.timeTable.programs.filterNotNull(),
//                                                                station
//                                                        )
//                                                )
//                                            }
//                                        }
//                            }
//                            .ignoreElements()
//
//                    this.set(fetchProcess)
//                }
//                this.set(Completable.fromAction {
//                    this@ProgramTableViewModel.timeTables.clear()
//                    this@ProgramTableViewModel.timeTables.addAll(timeTables)
//
//                    Log.d("TestTestTest", "complete : ${this@ProgramTableViewModel.timeTables.size}")
//                })
//            })

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    fun observeSelectedDate(): LiveData<Calendar> = selectedDate

    fun observeTimeTable() : LiveData<List<OneDayTimeTable>> = timeTablesData

    fun observeIsLoading(): LiveData<Boolean> = isLoading

    fun selectDate(date: Calendar) {
        settingsService.setDate(date)
    }
}