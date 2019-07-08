package tsuyogoro.sugorokuon.recommend.view

import io.reactivex.Flowable
import io.reactivex.functions.BiFunction
import tsuyogoro.sugorokuon.recommend.RecommendProgramRepository
import tsuyogoro.sugorokuon.recommend.settings.RecommendSettingsRepository
import tsuyogoro.sugorokuon.station.StationRepository
import tsuyogoro.sugorokuon.timetable.RecommendProgramData

class RecommendViewHolderViewModel(
    private val recommendProgramRepository: RecommendProgramRepository,
    private val stationRepository: StationRepository,
    private val recommendSettingsRepository: RecommendSettingsRepository
) {
    fun observeRecommendPrograms(): Flowable<List<RecommendProgramData>> =
        recommendProgramRepository
            .observeRecommendPrograms()
            .map {
                it.mapNotNull { p ->
                    stationRepository
                        .getStations()
                        .find { s -> s.id == p.stationId }
                        ?.let { station -> RecommendProgramData(p, station) }
                }
            }

    fun observeRecommendProgramsVisibility(): Flowable<Boolean> =
        recommendSettingsRepository
            .observeRecommendKeywords()
            .map { it.any { k -> k.keyword.isNotEmpty() } }

    fun observeNoRecommendLabelVisibility(): Flowable<Boolean> =
        Flowable.combineLatest(
            observeRecommendProgramsVisibility(),
            observeRecommendPrograms().map { it.size },
            BiFunction { isRecommendVisible, numberOfRecommends ->
                isRecommendVisible && numberOfRecommends == 0
            }
        )

    fun observeGotoKeywordSettingsVisibility(): Flowable<Boolean> =
        recommendSettingsRepository
            .observeRecommendKeywords()
            .map { it.filter { k -> k.keyword.isNotEmpty() }.isEmpty() }
}