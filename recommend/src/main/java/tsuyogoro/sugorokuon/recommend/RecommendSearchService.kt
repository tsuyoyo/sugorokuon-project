package tsuyogoro.sugorokuon.recommend

import io.reactivex.Completable
import io.reactivex.Flowable
import tsuyogoro.sugorokuon.constant.Area
import tsuyogoro.sugorokuon.settings.SettingsRepository
import tsuyogoro.sugorokuon.radiko.SearchUuidGenerator
import tsuyogoro.sugorokuon.radiko.api.SearchApi
import tsuyogoro.sugorokuon.radiko.api.response.SearchResponse
import tsuyogoro.sugorokuon.radiko.extension.toRecommendProgram
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

    // TODO : set remiders
    //  - DB上にある全ての番組のtimerを張る
    //  - idは番組の放送日時 + stationIDで作ったhash値
    //  - 最後のnotificationには、"program取り直しのflag" を入れる
    //  - もし1つもrecommendが無かったら、1日後にprogram取り直しを走らせるtimerを張る

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
            .map { it.toRecommendProgram() }
            .forEach(recommendProgramsDao::insert)
    }

}