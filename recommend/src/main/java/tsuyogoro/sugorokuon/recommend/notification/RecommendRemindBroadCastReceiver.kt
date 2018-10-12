package tsuyogoro.sugorokuon.recommend.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.reactivex.schedulers.Schedulers
import tsuyogoro.sugorokuon.SugorokuonLog
import tsuyogoro.sugorokuon.data.DataModule
import tsuyogoro.sugorokuon.recommend.RecommendModule
import tsuyogoro.sugorokuon.recommend.RecommendProgramsDao
import tsuyogoro.sugorokuon.recommend.reminder.RecommendRemindNotifier

class RecommendRemindBroadCastReceiver: BroadcastReceiver() {

    companion object {
        const val REQUEST_CODE = 100

        fun createIntent(context: Context) =
            Intent(context, RecommendRemindBroadCastReceiver::class.java)
    }

    lateinit var recommendRemindNotifier: RecommendRemindNotifier

    // TODO : RecommendProgramRepositoryの方がよさそう (Daoはinternalクラスにする)
    lateinit var recommendProgramsDao: RecommendProgramsDao

    lateinit var recommendRemindTimerSubmitter: RecommendRemindTimerSubmitter

    private fun solveDependencies(context: Context) {
        val dataModule = DataModule()
        val recommendModule = RecommendModule()

        recommendRemindNotifier = recommendModule.provideRecommendRemindNotifier(
            context,
            recommendModule.provideRecommendSettingsRepository(context),
            dataModule.provideStationRepository(context)
        )

        recommendProgramsDao = dataModule.provideRecommendProgramsDao(
            dataModule.provideRecommendProgramsDatabase(context)
        )

        recommendRemindTimerSubmitter = RecommendRemindTimerSubmitter(context)
    }

    override fun onReceive(context: Context, intent: Intent?) {
        SugorokuonLog.d("RecommendRemindBroadCastReceiver - S")

        solveDependencies(context)

        val programs = recommendProgramsDao.getAll()
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

}