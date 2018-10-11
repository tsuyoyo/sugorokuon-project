package tsuyogoro.sugorokuon.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.reactivex.schedulers.Schedulers
import tsuyogoro.sugorokuon.SugorokuonApplication
import tsuyogoro.sugorokuon.SugorokuonLog
import tsuyogoro.sugorokuon.recommend.RecommendProgramsDao
import tsuyogoro.sugorokuon.recommend.reminder.RecommendRemindNotifier
import javax.inject.Inject

class RecommendRemindBroadCastReceiver: BroadcastReceiver() {

    companion object {
        const val REQUEST_CODE = 100

        fun createIntent(context: Context) =
            Intent(context, RecommendRemindBroadCastReceiver::class.java)
    }

    @Inject
    lateinit var recommendRemindNotifier: RecommendRemindNotifier

    @Inject
    lateinit var recommendProgramsDao: RecommendProgramsDao

    @Inject
    lateinit var recommendRemindTimerSubmitter: RecommendRemindTimerSubmitter

    override fun onReceive(context: Context, intent: Intent?) {
        SugorokuonLog.d("RecommendRemindBroadCastReceiver - S")

        SugorokuonApplication
            .application(context)
            .appComponent()
            .timerComponent(TimerModule())
            .inject(this)

        val programs = recommendProgramsDao.getAll()
        if (programs.isNotEmpty()) {
            recommendRemindNotifier.notifyReminder(context, programs[0])
                .subscribeOn(Schedulers.io())
                .doOnComplete {
                    // TODO : 次のtimerをセットして、programs[0] をDBから消す
                }
                .subscribe()
        }

        SugorokuonLog.d("RecommendRemindBroadCastReceiver - E")
    }

}