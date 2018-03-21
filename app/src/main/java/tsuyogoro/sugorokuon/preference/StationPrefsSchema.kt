package tsuyogoro.sugorokuon.preference

import com.rejasupotaro.android.kvs.annotations.Key
import com.rejasupotaro.android.kvs.annotations.Table

@Table(name = "stations")
class StationPrefsSchema {

    @Key(name = "display_order")
    lateinit var displayOrder: String

}