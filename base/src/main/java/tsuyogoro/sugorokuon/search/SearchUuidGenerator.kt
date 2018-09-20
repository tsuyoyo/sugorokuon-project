/**
 * Copyright (c)
 * 2018 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.search

import java.util.*

class SearchUuidGenerator {

    fun generateSearchUuid() = UUID.randomUUID().toString().replace("-", "")

}