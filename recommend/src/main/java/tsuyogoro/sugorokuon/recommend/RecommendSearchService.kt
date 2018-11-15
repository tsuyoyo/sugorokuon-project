package tsuyogoro.sugorokuon.recommend

import io.reactivex.Flowable
import io.reactivex.Single
import tsuyogoro.sugorokuon.SugorokuonLog
import tsuyogoro.sugorokuon.constant.Area
import tsuyogoro.sugorokuon.radiko.SearchUuidGenerator
import tsuyogoro.sugorokuon.radiko.api.SearchApi
import tsuyogoro.sugorokuon.radiko.api.response.SearchResponse
import tsuyogoro.sugorokuon.radiko.extension.toRecommendProgram
import tsuyogoro.sugorokuon.recommend.settings.RecommendSettingsRepository
import tsuyogoro.sugorokuon.settings.SettingsRepository
import java.net.URLEncoder
import java.util.*

class RecommendSearchService(
    private val searchUuidGenerator: SearchUuidGenerator = SearchUuidGenerator(),
    private val searchApi: SearchApi,
    private val recommendProgramRepository: RecommendProgramRepository,
    private val recommendSettingsRepository: RecommendSettingsRepository,
    private val settingsRepository: SettingsRepository
) {
    fun fetchRecommendPrograms(): Single<List<RecommendProgram>> =
        recommendKeywordsStream()
            .flatMap { keyword ->
                areasStream()
                    .flatMapMaybe { area ->
                        SugorokuonLog.d("- search with ${keyword.keyword} in ${area.code}")
                        callSearchApi(keyword.keyword, area).retry(3)
                    }
                    .map { it.filterComingPrograms().map { it.toRecommendProgram() } }
                    .flatMap { recommendPrograms -> Flowable.fromIterable(recommendPrograms) }
            }
            .toList()

    fun updateRecommendProgramsInDatabase(recommendPrograms: List<RecommendProgram>) {
        recommendProgramRepository.clear()
        recommendProgramRepository.setRecommendPrograms(recommendPrograms)
    }

    private fun recommendKeywordsStream() = Flowable
        .fromIterable(recommendSettingsRepository.getRecommentKeywords())
        .filter { it.keyword.isNotBlank() }

    private fun areasStream() = Flowable
        .fromIterable(settingsRepository.getAreaSettings())

    private fun SearchResponse.filterComingPrograms(): List<SearchResponse.Program> =
        programs.filter {
            it.start.time  > (Calendar.getInstance().timeInMillis)
        }

    private fun callSearchApi(keyword: String, area: Area) = searchApi
        .search(
            encodedWord = URLEncoder.encode(keyword, "UTF-8"),
            areaId = area.id,
            culAreaId = area.id,
            uid = searchUuidGenerator.generateSearchUuid())
}