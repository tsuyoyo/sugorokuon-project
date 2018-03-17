package tsuyogoro.sugorokuon.api

import java.util.*

class SearchUuidGenerator {

    fun generateSearchUuid() = UUID.randomUUID().toString().replace("-", "")

}