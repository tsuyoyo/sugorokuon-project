package tsuyogoro.sugorokuon.dynamicfeature

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment

class RecommendModuleDependencyResolver {

    private companion object {
        const val RECOMMEND_PACKAGE_NAME = "tsuyogoro.sugorokuon.recommend"
    }

    fun getRecommendKeywordSettingsFragent(): Fragment? =
        try {
            Class
                .forName("$RECOMMEND_PACKAGE_NAME.keyword.RecommendKeywordFragment")
                .newInstance() as? Fragment
        } catch (e: ClassNotFoundException) {
            null
        }


    fun getRecommendReminderSettingsFragent(): Fragment? =
        try {
            Class
                .forName("$RECOMMEND_PACKAGE_NAME.reminder.ReminderSettingsFragment")
                .newInstance() as? Fragment
        } catch (e: ClassNotFoundException) {
            null
        }

    fun getRecommendDebugActivityIntent(context: Context): Intent? =
        try {
            Intent(
                context,
                Class.forName("$RECOMMEND_PACKAGE_NAME.debug.RecommendDebugActivity")
            )
        } catch (e: ClassNotFoundException) {
            null
        }

}