package tsuyogoro.sugorokuon.model

import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor

class SugorokuonAppState(
        private val isLoadingTimeTable: BehaviorProcessor<Boolean> =
            BehaviorProcessor.createDefault(false),
        private val isLoadingFeed: BehaviorProcessor<Boolean> =
            BehaviorProcessor.createDefault(false),
        private val stationListVisibility: BehaviorProcessor<Boolean> =
            BehaviorProcessor.createDefault(false)
) {

    fun setIsLoadingTimeTable(isLoading: Boolean) {
        isLoadingTimeTable.onNext(isLoading)
    }

    fun setIsLoadingFeed(isLoading: Boolean) {
        isLoadingFeed.onNext(isLoading)
    }

    fun setStationListVisibility(isVisible: Boolean) {
        stationListVisibility.onNext(isVisible)
    }

    fun observeIsLoadingTimeTable(): Flowable<Boolean> = isLoadingTimeTable.hide()

    fun observeIsLoadingFeed(): Flowable<Boolean> = isLoadingFeed.hide()

    fun observeStationListVisibility(): Flowable<Boolean> = stationListVisibility.hide()

}