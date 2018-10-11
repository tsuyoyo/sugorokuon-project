package tsuyogoro.sugorokuon.notification

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class TimerModule {

    @Provides
    fun provideRecommentRemindTimerSubmitter(context: Context): RecommendRemindTimerSubmitter =
        RecommendRemindTimerSubmitter(context)

}