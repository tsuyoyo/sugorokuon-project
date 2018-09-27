/**
 * Copyright (c)
 * 2018 tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.recommend.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface RecommendProgramsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(recommendProgram: RecommendProgram)

    @Query("SELECT * FROM recommend_programs ORDER BY date ASC")
    fun getAll(): List<RecommendProgram>

    @Query("SELECT * ,MIN(date) FROM recommend_programs GROUP BY date")
    fun getOnAirSoon(): List<RecommendProgram>

    @Query("DELETE From recommend_programs")
    fun clearTable()
}