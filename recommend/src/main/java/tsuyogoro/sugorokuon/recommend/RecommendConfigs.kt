package tsuyogoro.sugorokuon.recommend

import tsuyogoro.sugorokuon.recommend.debug.RecommendConfigPrefs

class RecommendConfigs(
    private val recommendConfigPrefs: RecommendConfigPrefs
) {

    private val INTERVAL_UPDATE_RECOMEND_IN_SEC = (6 * 60 * 60).toLong() // 6 hours

    fun getUpdateIntervalInSec() : Long {
        val debugUpdateInterval = recommendConfigPrefs.getUpdateIntervalForDebugInSec(0)
        if (BuildConfig.DEBUG && debugUpdateInterval > 0) {
            return debugUpdateInterval
        } else {
            return INTERVAL_UPDATE_RECOMEND_IN_SEC
        }
    }
}