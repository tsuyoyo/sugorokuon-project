package tsuyogoro.sugorokuon.dynamicfeature

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.view.ViewParent
import io.reactivex.Completable
import tsuyogoro.sugorokuon.timetable.ProgramTableAdapter

class RecommendModuleDependencyResolver {

    private companion object {
        const val RECOMMEND_PACKAGE_NAME = "tsuyogoro.sugorokuon.recommend"
    }

    interface RecommendViewHolder {
        fun onBoundWithAdapter()
        fun onUnboundFromAdapter()
    }

    interface RecommendUpdater {
        fun observeRecommendConditionAndUpdate(): Completable
    }

    fun getRecommendProgramsViewHolder(
        parent: ViewParent,
        listener: ProgramTableAdapter.ProgramTableAdapterListener
    ): RecyclerView.ViewHolder? =
        try {
            Class
                .forName("$RECOMMEND_PACKAGE_NAME.view.RecommendViewHolderImpl")
                .getConstructor(
                    ViewGroup::class.java,
                    ProgramTableAdapter.ProgramTableAdapterListener::class.java
                )
                .newInstance(parent, listener) as? RecyclerView.ViewHolder
        } catch (e: Exception) {
            null
        }

    fun getRecommendUpdater(context: Context): RecommendUpdater? =
        try {
            Class
                .forName("$RECOMMEND_PACKAGE_NAME.RecommendUpdaterImpl")
                .getConstructor(
                    Context::class.java
                )
                .newInstance(context) as? RecommendUpdater
        } catch (e: Exception) {
            null
        }

    fun getRecommendKeywordSettingsFragment(): Fragment? =
        try {
            Class
                .forName("$RECOMMEND_PACKAGE_NAME.keyword.RecommendKeywordFragment")
                .newInstance() as? Fragment
        } catch (e: ClassNotFoundException) {
            null
        }


    fun getRecommendReminderSettingsFragment(): Fragment? =
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