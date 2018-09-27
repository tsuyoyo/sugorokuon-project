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
    @PrimaryKey
    val date: Date,

    val program: SearchResponse.Program
)
