package tsuyogoro.sugorokuon.recommend

import tsuyogoro.sugorokuon.data.SettingsRepository
import tsuyogoro.sugorokuon.radiko.SearchUuidGenerator
import tsuyogoro.sugorokuon.radiko.api.SearchApi
import tsuyogoro.sugorokuon.recommend.settings.RecommendSettingsRepository

class RecommendSearchService(
    private val searchUuidGenerator: SearchUuidGenerator = SearchUuidGenerator(),
    private val searchApi: SearchApi,
    private val recommendSettingsRepository: RecommendSettingsRepository,
    private val settingsRepository: SettingsRepository
) {

    fun fetchRecommendPrograms() {
        // 1. recommendKeywordsDao から keyword を取り出してstreamにする
        // 2. settingsRepository から area を取り出して、(area, keyword) の組み合わせでstreamを作る
        // 3. searchApi を叩いて、結果をrecommendProgramsDao経由でDBに格納する
        // 4. completeを返す
    }


    // TODO :
    // area情報が必要になってくるので、dataモジュールにpreference、databaseの機能を移す
    // dataモジュールはrepositoryという形で情報を公開するようにしよう
    // - app settingsのrepository
    // - recommend dataのrepository

}