package tsuyogoro.sugorokuon.recommend

import android.arch.lifecycle.LiveData

class RecommendProgramRepository {

    private lateinit var recommendProgramsDao: RecommendProgramsDao

    internal fun initialize(recommendProgramsDao: RecommendProgramsDao) {
        this.recommendProgramsDao = recommendProgramsDao
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

    fun observeRecommendPrograms(): LiveData<List<RecommendProgram>> =
        recommendProgramsDao.observePrograms()

    fun getRecommendPrograms(): List<RecommendProgram> =
        recommendProgramsDao.getPrograms()

}