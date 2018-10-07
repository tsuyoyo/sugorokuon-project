/**
 * Copyright (c)
 * 2018 tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.recommend.database

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import tsuyogoro.sugorokuon.radiko.api.response.SearchResponse
import java.util.*

@Entity(tableName = "recommend_programs")
data class RecommendProgram constructor(
    @PrimaryKey val id: String, // start + end + stationId のstring
    val start: Long,
    val end: Long,
    val stationId: String,
    val title: String,
    val personality: String,
    val image: String,
    val url: String,
    val description: String,
    val info: String
) {
    companion object {
        fun create(program: SearchResponse.Program) =
            RecommendProgram(
                "${program.start.time}${program.end.time}${program.stationId}",
                program.start.time,
                program.end.time,
                program.stationId,
                program.title,
                program.personality,
                program.image,
                program.url,
                program.description,
                program.info
            )
    }
}
