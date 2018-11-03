package tsuyogoro.sugorokuon.recommend

import io.reactivex.Flowable
import tsuyogoro.sugorokuon.SugorokuonLog

class RecommendProgramRepository {

    private lateinit var recommendProgramsDao: RecommendProgramsDao

    internal fun initialize(recommendProgramsDao: RecommendProgramsDao) {
        this.recommendProgramsDao = recommendProgramsDao
        SugorokuonLog.d("Initialize RecommendProgramRepository : ${this}")
    }

    fun setRecommendPrograms(recommendPrograms: List<RecommendProgram>) {
        recommendProgramsDao.insert(recommendPrograms)
    }

    fun delete(recommendProgram: RecommendProgram) {
        recommendProgramsDao.delete(recommendProgram)
    }

    fun clear() {
        recommendProgramsDao.clearTable()
    }

    fun observeRecommendPrograms(): Flowable<List<RecommendProgram>> =
        recommendProgramsDao.observePrograms()

    fun getRecommendPrograms(): List<RecommendProgram> =
        recommendProgramsDao.getPrograms()

}