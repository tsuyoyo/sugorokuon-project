package tsuyogoro.sugorokuon.recommend.debug

import com.rejasupotaro.android.kvs.annotations.Key
import com.rejasupotaro.android.kvs.annotations.Table

@Table(name = "recommendConfig")
internal class RecommendConfigPrefsSchema {

    @Key(name = "update_interval_for_debug")
    var updateIntervalForDebugInSec: Long = 0

}