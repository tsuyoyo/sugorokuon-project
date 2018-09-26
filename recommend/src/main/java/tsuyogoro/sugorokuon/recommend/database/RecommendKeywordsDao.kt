package tsuyogoro.sugorokuon.recommend.database

import android.arch.persistence.room.*

@Dao
interface RecommendKeywordsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(recommendKeyword: RecommendKeyword)

    @Update
    fun update(recommendKeyword: RecommendKeyword)

    @Query("SELECT * FROM recommend_keywords ORDER BY `index` ASC")
    fun getAll(): List<RecommendKeyword>

    @Query("DELETE From recommend_keywords")
    fun clearTable()
}