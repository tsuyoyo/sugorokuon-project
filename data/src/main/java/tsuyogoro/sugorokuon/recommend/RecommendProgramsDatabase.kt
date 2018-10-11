/**
 * Copyright (c)
 * 2018 tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.recommend

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = [RecommendProgram::class], version = 1, exportSchema = false)
abstract class RecommendProgramsDatabase : RoomDatabase() {
    abstract fun recommendProgramsDao() : RecommendProgramsDao
}