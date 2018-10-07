package tsuyogoro.sugorokuon.debug

import android.app.Activity
import android.content.Intent
import com.tomoima.debot.strategy.DebotStrategy

class RecommendDebugStrategy : DebotStrategy() {

    override fun startAction(activity: Activity) {
        activity.startActivity(Intent(activity, RecommendDebugActivity::class.java))
    }
}