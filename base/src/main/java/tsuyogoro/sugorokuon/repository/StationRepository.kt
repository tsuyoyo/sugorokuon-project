package tsuyogoro.sugorokuon.repository

import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import tsuyogoro.sugorokuon.radiko.api.response.StationResponse

class StationRepository(
        private val stationResponses: BehaviorProcessor<List<StationResponse>>
            = BehaviorProcessor.create()
) {
    fun setStationResponse(response: StationResponse) {
        stationResponses.onNext(listOf(response))
    }

    fun setStationResponses(responses: List<StationResponse>) {
        stationResponses.onNext(responses)
    }

    fun observeStationResponses() : Flowable<List<StationResponse>> = stationResponses
}