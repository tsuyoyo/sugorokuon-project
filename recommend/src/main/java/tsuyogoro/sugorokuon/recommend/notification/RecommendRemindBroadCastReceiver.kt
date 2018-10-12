package tsuyogoro.sugorokuon.recommend.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.reactivex.schedulers.Schedulers
import tsuyogoro.sugorokuon.SugorokuonLog
import tsuyogoro.sugorokuon.data.DataModule
import tsuyogoro.sugorokuon.recommend.RecommendModule
import tsuyogoro.sugorokuon.recommend.RecommendProgramRepository
import tsuyogoro.sugorokuon.recommend.reminder.RecommendRemindNotifier
import javax.inject.Inject

class RecommendRemindBroadCastReceiver: BroadcastReceiver() {

    companion object {
        const val REQUEST_CODE = 100

        fun createIntent(context: Context) =
            Intent(context, RecommendRemindBroadCastReceiver::class.java)
    }

    @Inject
    lateinit var recommendProgramRepository: RecommendProgramRepository

    @Inject
    internal lateinit var recommendRemindNotifier: RecommendRemindNotifier

    @Inject
    internal lateinit var recommendRemindTimerSubmitter: RecommendRemindTimerSubmitter

    override fun onReceive(context: Context, intent: Intent?) {
        SugorokuonLog.d("RecommendRemindBroadCastReceiver - S")

        DaggerRecommendReminderComponent.builder()
            .module(RecommendReminderComponent.Module(context))
            .build()
            .inject(this)

        val programs = recommendProgramRepository.getRecommendPrograms()
        if (programs.isNotEmpty()) {
            recommendRemindNotifier.notifyReminder(context, programs[0])
                .subscribeOn(Schedulers.io())
                .doOnComplete {
                    // TODO : optoion 1) 次のtimerをセットして、programs[0] をDBから消す

                    // TODO : option 2) intentの中にprogram更新フラグが入ってたらfetchしなおして、全てのrecommendのnotificationをセットしておく
                }
                .subscribe()
        }

        SugorokuonLog.d("RecommendRemindBroadCastReceiver - E")
    }

    // TODO : Bootcompleteを取る

}