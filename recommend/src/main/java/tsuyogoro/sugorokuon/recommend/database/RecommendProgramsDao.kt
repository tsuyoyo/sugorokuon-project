/**
 * Copyright (c)
 * 2018 tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.recommend.database

import android.arch.persistence.room.*

@Dao
interface RecommendProgramsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(recommendProgram: RecommendProgram)

    @Query("SELECT * FROM recommend_programs ORDER BY start ASC")
    fun getAll(): List<RecommendProgram>

    @Query("SELECT * ,MIN(start) FROM recommend_programs GROUP BY start")
    fun getOnAirSoon(): List<RecommendProgram>

    @Delete
    fun delete(recommendProgram: RecommendProgram)

    @Query("DELETE From recommend_programs")
    fun clearTable()
}