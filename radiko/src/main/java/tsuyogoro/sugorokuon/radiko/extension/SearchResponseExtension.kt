/**
 * Copyright (c)
 * 2018 tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.radiko.extension

import tsuyogoro.sugorokuon.radiko.api.response.SearchResponse
import tsuyogoro.sugorokuon.recommend.RecommendProgram

fun SearchResponse.Program.toRecommendProgram(): RecommendProgram =
    RecommendProgram(
        "${start.time}${end.time}${stationId}",
        start.time,
        end.time,
        stationId,
        title,
        personality,
        image,
        url,
        description,
        info
    )