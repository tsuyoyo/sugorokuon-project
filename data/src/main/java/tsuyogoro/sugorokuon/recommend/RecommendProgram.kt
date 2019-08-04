/**
 * Copyright (c)
 * 2018 tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.recommend

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recommend_programs")
data class RecommendProgram constructor(
    @PrimaryKey val id: String, // start + end + stationId のstring
    val start: Long, // sec
    val end: Long, // sec
    val stationId: String,
    val title: String,
    val personality: String,
    val image: String,
    val url: String,
    val description: String,
    val info: String
)
