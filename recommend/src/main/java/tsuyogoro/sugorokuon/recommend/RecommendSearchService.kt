package tsuyogoro.sugorokuon.recommend

import android.util.Log
import io.reactivex.Completable
import io.reactivex.Flowable
import tsuyogoro.sugorokuon.data.SettingsRepository
import tsuyogoro.sugorokuon.radiko.SearchUuidGenerator
import tsuyogoro.sugorokuon.radiko.api.SearchApi
import tsuyogoro.sugorokuon.recommend.database.RecommendProgram
import tsuyogoro.sugorokuon.recommend.database.RecommendProgramsDao
import tsuyogoro.sugorokuon.recommend.settings.RecommendSettingsRepository
import java.net.URLEncoder

class RecommendSearchService(
    private val searchUuidGenerator: SearchUuidGenerator = SearchUuidGenerator(),
    private val searchApi: SearchApi,
    private val recommendProgramsDao: RecommendProgramsDao,
    private val recommendSettingsRepository: RecommendSettingsRepository,
    private val settingsRepository: SettingsRepository
) {

    fun fetchRecommendPrograms() : Completable =
        Flowable
            .fromIterable(recommendSettingsRepository.getRecommentKeywords())
            .filter { it.keyword.isNotBlank() }
            .flatMapCompletable { recommendKey ->
                Log.d("TestTestTest", "Start - ${recommendKey.keyword}")
                Flowable
                    .fromIterable(settingsRepository.getAreaSettings())
                    .flatMapCompletable { area ->
                        Log.d("TestTestTest", "Area - ${area.code}")

                        return@flatMapCompletable searchApi.search(
                            encodedWord = URLEncoder.encode(recommendKey.keyword, "UTF-8"),
                            areaId = area.id,
                            culAreaId = area.id,
                            uid = searchUuidGenerator.generateSearchUuid()
                        ).doOnSuccess {
                            it.programs
                                .map { RecommendProgram.create(it) }
                                .forEach(recommendProgramsDao::insert)
//
//                            Log.d("TestTestTest", "Result (${area.code}, ${recommendKey.keyword}")
//                            it.programs.forEach {
//                                Log.d("TestTestTest", " - ${it.title}")
//                            }
//                            // store DB
                        }.doOnError {
                            Log.d("TestTestTest", "Error (${area.code}, ${recommendKey.keyword} (${it.message}")
                        }
                            .ignoreElement()


                    }
            }



    // TODO :
    // area情報が必要になってくるので、dataモジュールにpreference、databaseの機能を移す
    // dataモジュールはrepositoryという形で情報を公開するようにしよう
    // - app settingsのrepository
    // - recommend dataのrepository

}