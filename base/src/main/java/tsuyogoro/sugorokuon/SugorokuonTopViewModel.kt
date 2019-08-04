package tsuyogoro.sugorokuon

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import tsuyogoro.sugorokuon.constant.Area
import tsuyogoro.sugorokuon.recommend.RecommendSearchService
import tsuyogoro.sugorokuon.recommend.RecommendTimerService
import tsuyogoro.sugorokuon.recommend.settings.RecommendSettingsRepository
import tsuyogoro.sugorokuon.rx.SchedulerProvider
import tsuyogoro.sugorokuon.service.*
import java.util.*
import java.util.concurrent.TimeUnit

class SugorokuonTopViewModel(
        private val settingsService: SettingsService,
        private val timeTableService: TimeTableService,
        private val stationService: StationService,
        private val feedService: FeedService,
        private val tutorialService: TutorialService,
        private val recommendSearchService: RecommendSearchService,
        private val recommendTimerService: RecommendTimerService,
        private val recommendSettingsRepository: RecommendSettingsRepository,
        private val schedulerProvider: SchedulerProvider,
        private val disposables: CompositeDisposable = CompositeDisposable()
): ViewModel() {

    object Constants {
        const val RETRY_TIME: Long = 3
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
            private val settingsService: SettingsService,
            private val timeTableService: TimeTableService,
            private val stationService: StationService,
            private val feedService: FeedService,
            private val tutorialService: TutorialService,
            private val recommendSearchService: RecommendSearchService,
            private val recommendTimerService: RecommendTimerService,
            private val recommendSettingsRepository: RecommendSettingsRepository,
            private val schedulerProvider: SchedulerProvider
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SugorokuonTopViewModel(
                    settingsService,
                    timeTableService,
                    stationService,
                    feedService,
                    tutorialService,
                    recommendSearchService,
                    recommendTimerService,
                    recommendSettingsRepository,
                    schedulerProvider
            ) as T
        }
    }

    private val signalShowTutorial: MutableLiveData<Boolean> = MutableLiveData()
    private val signalOnErrorFetchTimeTable: MutableLiveData<Boolean> = MutableLiveData()
    private val signalOnErrorFetchStation: MutableLiveData<Boolean> = MutableLiveData()
    private val signalOnErrorFetchFeeds: MutableLiveData<Boolean> = MutableLiveData()
    private val signalOnErrorFetchRecommends: MutableLiveData<Boolean> = MutableLiveData()

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
                        ),

                Flowable
                        .merge(
                            recommendSettingsRepository.observeRecommendKeywords().doOnNext { SugorokuonLog.d("SugorokuonTopViewModel detected change") },
                            recommendSettingsRepository.observeReminderTiming(),
                            settingsService.observeAreas()
                        )
                        .throttleLast(3, TimeUnit.SECONDS)
                        .doOnNext {
                            recommendTimerService.cancelUpdateRecommendTimer()
                        }
                        .flatMapSingle {
                            recommendSearchService
                                .fetchRecommendPrograms()
                                .doOnSuccess(
                                    recommendSearchService::updateRecommendProgramsInDatabase
                                )
                        }
                        .doOnNext {
                            recommendTimerService.setUpdateRecommendTimer()
                        }
                        .subscribeOn(Schedulers.io())
                        .subscribeBy (
                            onError = { e ->
                                SugorokuonLog.e("Failed to fetch recommends : ${e.message}")
                                signalOnErrorFetchRecommends.postValue(true)
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