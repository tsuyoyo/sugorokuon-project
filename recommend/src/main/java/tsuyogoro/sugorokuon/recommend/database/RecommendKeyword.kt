package tsuyogoro.sugorokuon.recommend.database

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "recommend_keywords")
data class RecommendKeyword constructor(
    @PrimaryKey
    val index: Int,

    val keyword: String
)