package tsuyogoro.sugorokuon.recommend.database

import android.arch.persistence.room.Database

@Database(entities = [RecommendProgram::class], version = 1)
abstract class RecommendProgramsDatabase {
    abstract fun recommendProgramsDao() : RecommendProgramsDao
}