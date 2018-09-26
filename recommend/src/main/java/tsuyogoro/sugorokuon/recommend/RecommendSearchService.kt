package tsuyogoro.sugorokuon.recommend

import io.reactivex.Completable
import tsuyogoro.sugorokuon.radiko.SearchUuidGenerator
import tsuyogoro.sugorokuon.radiko.api.SearchApi
import tsuyogoro.sugorokuon.recommend.database.RecommendKeywordsDao
import tsuyogoro.sugorokuon.recommend.database.RecommendProgramsDao

class RecommendSearchService(
    private val searchUuidGenerator: SearchUuidGenerator,
    private val searchApi: SearchApi,
    private val recommendKeywordsDao: RecommendKeywordsDao,
    private val recommendProgramDao: RecommendProgramsDao
) {


    // TODO :
    // area情報が必要になってくるので、dataモジュールにpreference、databaseの機能を移す
    // dataモジュールはrepositoryという形で情報を公開するようにしよう
    // - app settingsのrepository
    // - recommend dataのrepository

}