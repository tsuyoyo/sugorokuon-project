package tsuyogoro.sugorokuon.v3

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import tsuyogoro.sugorokuon.utils.SugorokuonLog
import tsuyogoro.sugorokuon.v3.constant.Area
import tsuyogoro.sugorokuon.v3.rx.SchedulerProvider
import tsuyogoro.sugorokuon.v3.service.*
import java.util.*

class SugorokuonTopViewModel(
        private val settingsService: SettingsService,
        private val timeTableService: TimeTableService,
        private val stationService: StationService,
        private val feedService: FeedService,
        private val tutorialService: TutorialService,
        private val schedulerProvider: SchedulerProvider,
        private val disposables: CompositeDisposable = CompositeDisposable()
): ViewModel() {

    object Constants {
        val RETRY_TIME: Long = 3
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
            private val settingsService: SettingsService,
            private val timeTableService: TimeTableService,
            private val stationService: StationService,
            private val feedService: FeedService,
            private val tutorialService: TutorialService,
            private val schedulerProvider: SchedulerProvider
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SugorokuonTopViewModel(
                    settingsService,
                    timeTableService,
                    stationService,
                    feedService,
                    tutorialService,
                    schedulerProvider
            ) as T
        }
    }

    private val signalShowTutorial: MutableLiveData<Boolean> = MutableLiveData()
    private val signalOnErrorFetchTimeTable: MutableLiveData<Boolean> = MutableLiveData()
    private val signalOnErrorFetchStation: MutableLiveData<Boolean> = MutableLiveData()
    private val signalOnErrorFetchFeeds: MutableLiveData<Boolean> = MutableLiveData()

    init {
        disposables.addAll(
                Flowable.combineLatest(
                        settingsService.observeAreas(),
                        tutorialService.observeDoneTutorialV3(),
                        BiFunction { areas: Set<Area>, doneTutorialV3: Boolean ->
                            // TODO : tutorialV3done だったらdialogだす、とかかな?
                            // !doneTutorialV3 &&
                            SugorokuonLog.d("Tutorial v3 done - $doneTutorialV3")
                            if (areas.isEmpty()) {
                                signalShowTutorial.postValue(true)
                            } else {
                                signalShowTutorial.postValue(false)
                            }
                        })
                        .subscribe(),

                Flowable.combineLatest(
                        settingsService.observeDate(),
                        settingsService.observeAreas().filter { it.isNotEmpty() },
                        BiFunction {
                            date: Calendar, areas: Set<Area> -> Pair(date, areas)
                        })
                        .flatMapCompletable { (date: Calendar, areas: Set<Area>) ->
                                timeTableService
                                        .fetchTimeTable(date, areas.toList())
                                        .subscribeOn(schedulerProvider.io())
                                        .retry(Constants.RETRY_TIME)
                        }
                        .subscribe(
                                { },
                                { e ->
                                    SugorokuonLog.e("Failed to fetch timetable : ${e.message}")
                                    signalOnErrorFetchTimeTable.postValue(true)
                                }
                        ),

                settingsService
                        .observeAreas()
                        .filter { it.isNotEmpty() }
                        .map { it.toList() }
                        .flatMapCompletable {
                            stationService
                                    .fetchStation(it)
                                    .subscribeOn(schedulerProvider.io())
                                    .retry(Constants.RETRY_TIME)
                        }
                        .subscribe(
                                { },
                                { e ->
                                    SugorokuonLog.e("Failed to fetch station : ${e.message}")
                                    signalOnErrorFetchStation.postValue(true)
                                }
                        ),

                stationService
                        .observeStations()
                        .flatMapCompletable {
                            feedService
                                    .fetchFeeds(it.mapNotNull { s -> s.id })
                                    .subscribeOn(schedulerProvider.io())
                                    .retry(Constants.RETRY_TIME)
                        }
                        .subscribe(
                                { },
                                { e ->
                                    SugorokuonLog.e("Failed to fetch feeds : ${e.message}")
                                    signalOnErrorFetchFeeds.postValue(true)
                                }
                        )
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }

    fun observeRequestToShowTutorial(): LiveData<Boolean> = signalShowTutorial

}