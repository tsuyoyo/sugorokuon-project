package tsuyogoro.sugorokuon.preference

import com.rejasupotaro.android.kvs.annotations.Key
import com.rejasupotaro.android.kvs.annotations.Table
import com.rejasupotaro.android.kvs.serializers.PrefsSerializer
import tsuyogoro.sugorokuon.constant.Area

@Table(name = "area")
class AreaPrefsSchema {

    @Key(name = "area_ids", serializer = AreaSerializer::class)
    lateinit var areaIds: String

    class AreaSerializer : PrefsSerializer<Set<Area>, String> {

        override fun deserialize(src: String?): Set<Area> = src
                ?.split(",")
                ?.filter { it.isNotBlank() }
                ?.map { id -> Area.values().first { id == it.id } }
                ?.toSet()
                ?: emptySet()

        override fun serialize(src: Set<Area>?): String =
                src?.joinToString(
                        separator = ",",
                        transform = { area -> area.id }
                ) ?: ""
    }
}
