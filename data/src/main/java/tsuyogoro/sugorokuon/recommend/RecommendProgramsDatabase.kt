/**
 * Copyright (c)
 * 2018 tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.recommend

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RecommendProgram::class], version = 1, exportSchema = false)
internal abstract class RecommendProgramsDatabase : RoomDatabase() {
    abstract fun recommendProgramsDao() : RecommendProgramsDao
}