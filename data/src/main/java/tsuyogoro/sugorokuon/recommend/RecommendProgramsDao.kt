/**
 * Copyright (c)
 * 2018 tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.recommend

import androidx.room.*
import io.reactivex.Flowable

@Dao
internal interface RecommendProgramsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(recommendProgram: RecommendProgram)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(recommendPrograms: List<RecommendProgram>)

    @Query("SELECT * FROM recommend_programs ORDER BY start ASC")
    fun observePrograms(): Flowable<List<RecommendProgram>>

    @Query("SELECT * FROM recommend_programs ORDER BY start ASC")
    fun getPrograms(): List<RecommendProgram>

    @Query("SELECT * FROM recommend_programs GROUP BY start")
    fun getOnAirSoon(): List<RecommendProgram>

    @Delete
    fun delete(recommendProgram: RecommendProgram)

    @Query("DELETE From recommend_programs")
    fun clearTable()
}