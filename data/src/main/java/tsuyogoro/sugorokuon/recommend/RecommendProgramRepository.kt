package tsuyogoro.sugorokuon.recommend

import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor

class RecommendProgramRepository {

    private val recommendPrograms: BehaviorProcessor<List<RecommendProgram>> =
        BehaviorProcessor.create()

    private lateinit var recommendProgramsDao: RecommendProgramsDao

    internal fun initialize(recommendProgramsDao: RecommendProgramsDao) {
        this.recommendProgramsDao = recommendProgramsDao
        updateRecommendPrograms()
    }

    fun setRecommendPrograms(recommendPrograms: List<RecommendProgram>) {
        recommendProgramsDao.insert(recommendPrograms)
        updateRecommendPrograms()
    }

    fun clear() {
        recommendProgramsDao.clearTable()
        updateRecommendPrograms()
    }

    fun observeRecommendPrograms(): Flowable<List<RecommendProgram>> = recommendPrograms.hide()

    fun getRecommendPrograms(): List<RecommendProgram> = recommendPrograms.value

    private fun updateRecommendPrograms() {
        recommendPrograms.onNext(recommendProgramsDao.getAll())
    }
}