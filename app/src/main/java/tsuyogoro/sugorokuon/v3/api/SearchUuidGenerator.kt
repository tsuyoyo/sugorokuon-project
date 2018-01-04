package tsuyogoro.sugorokuon.v3.api

import java.util.*

class SearchUuidGenerator {

    fun generateSearchUuid() = UUID.randomUUID().toString().replace("-", "")

}