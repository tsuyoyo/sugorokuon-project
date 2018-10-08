package tsuyogoro.sugorokuon.recommend

import io.reactivex.Completable
import io.reactivex.Flowable
import tsuyogoro.sugorokuon.constant.Area
import tsuyogoro.sugorokuon.data.SettingsRepository
import tsuyogoro.sugorokuon.radiko.SearchUuidGenerator
import tsuyogoro.sugorokuon.radiko.api.SearchApi
import tsuyogoro.sugorokuon.radiko.api.response.SearchResponse
import tsuyogoro.sugorokuon.recommend.database.RecommendProgram
import tsuyogoro.sugorokuon.recommend.database.RecommendProgramsDao
import tsuyogoro.sugorokuon.recommend.settings.RecommendSettingsRepository
import java.net.URLEncoder
import java.util.*

class RecommendSearchService(
    private val searchUuidGenerator: SearchUuidGenerator = SearchUuidGenerator(),
    private val searchApi: SearchApi,
    private val recommendProgramsDao: RecommendProgramsDao,
    private val recommendSettingsRepository: RecommendSettingsRepository,
    private val settingsRepository: SettingsRepository
) {

    fun fetchRecommendPrograms(): Completable =
        recommendKeywordsStream().flatMapCompletable { recommendKey ->
            areasStream().flatMapCompletable { area ->
                fetchRecommendsAndStoreDb(recommendKey.keyword, area)
            }
        }

    private fun recommendKeywordsStream() = Flowable
        .fromIterable(recommendSettingsRepository.getRecommentKeywords())
        .filter { it.keyword.isNotBlank() }

    private fun areasStream() = Flowable
        .fromIterable(settingsRepository.getAreaSettings())

    private fun fetchRecommendsAndStoreDb(keyword: String, area: Area) = searchApi
        .search(
            encodedWord = URLEncoder.encode(keyword, "UTF-8"),
            areaId = area.id,
            culAreaId = area.id,
            uid = searchUuidGenerator.generateSearchUuid())
        .doOnSuccess(this::storeRecommendToDb)
        .ignoreElement()

    private fun storeRecommendToDb(searchResponse: SearchResponse) {
        searchResponse.programs
            .filter { it.start.time > Calendar.getInstance().timeInMillis }
            .map { RecommendProgram.create(it) }
            .forEach(recommendProgramsDao::insert)
    }

}