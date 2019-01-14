package tsuyogoro.sugorokuon.debug

import android.app.Activity
import com.tomoima.debot.strategy.DebotStrategy
import tsuyogoro.sugorokuon.dynamicfeature.RecommendModuleDependencyResolver

class RecommendDebugStrategy : DebotStrategy() {

    private val recommendModuleDependencyResolver = RecommendModuleDependencyResolver()

    override fun startAction(activity: Activity) {
        recommendModuleDependencyResolver.getRecommendDebugActivityIntent(activity)?.let {
            activity.startActivity(it)
        }
    }
}