package tsuyogoro.sugorokuon.recommend

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import tsuyogoro.sugorokuon.SugorokuonLog
import tsuyogoro.sugorokuon.recommend.reminder.RecommendRemindNotifier
import java.util.*
import javax.inject.Inject

class RecommendBroadCastReceiver : BroadcastReceiver() {

    companion object {
        private const val REQUEST_CODE_REMIND_ON_AIR = 100
        private const val REQUEST_CODE_UPDATE_RECOMMEND = 101

        private const val ACTION_REMIND_ON_AIR = "remindOnAir"
        private const val ACTION_UPDATE_RECOMMEND = "updateRecommend"

        // 6 hours
        private const val INTERVAL_UPDATE_RECOMMEND_MILLISEC = 1000 * 60 * 60 * 6

        fun createIntentForRemindOnAir(context: Context) =
            Intent(context, RecommendBroadCastReceiver::class.java).apply {
                action = ACTION_REMIND_ON_AIR
            }

        fun createIntentForUpdateRecommend(context: Context) =
            Intent(context, RecommendBroadCastReceiver::class.java).apply {
                action = ACTION_UPDATE_RECOMMEND
            }
    }

    @Inject
    lateinit var recommendSearchService: RecommendSearchService

    @Inject
    lateinit var recommendProgramRepository: RecommendProgramRepository

    @Inject
    internal lateinit var recommendRemindNotifier: RecommendRemindNotifier

    @Inject
    internal lateinit var recommendTimerSubmitter: RecommendTimerSubmitter

    private val disposables = CompositeDisposable()

    override fun onReceive(context: Context, intent: Intent?) {
        SugorokuonLog.d("RecommendBroadCastReceiver - S")

        DaggerRecommendComponent.builder()
            .module(RecommendComponent.Module(context))
            .build()
            .inject(this)

        when (intent?.action) {
            ACTION_REMIND_ON_AIR -> remindOnAirProgram(context)
            ACTION_UPDATE_RECOMMEND -> updateRecommend()
        }

        SugorokuonLog.d("RecommendBroadCastReceiver - E")
    }

    private fun remindOnAirProgram(context: Context) {
        val programs = recommendProgramRepository.getRecommendPrograms()
        if (programs.isNotEmpty()) {
            val remindProgram = programs[0]
            recommendProgramRepository.delete(remindProgram)
            recommendRemindNotifier
                .notifyReminder(context, remindProgram)
                .subscribeOn(Schedulers.io())
                .doOnComplete { setNextRemindTimer() }
                .subscribe()
        }
    }

    private fun updateRecommend() {
        recommendProgramRepository.clear()
        recommendTimerSubmitter.cancelUpdateRecommendTimer(REQUEST_CODE_UPDATE_RECOMMEND)

        disposables.add(
            recommendSearchService.fetchRecommendPrograms()
                .subscribeOn(Schedulers.io())
                .subscribe {
                    // Timer for next update
                    val nextUpdate = Calendar.getInstance().timeInMillis +
                        INTERVAL_UPDATE_RECOMMEND_MILLISEC
                    recommendTimerSubmitter.setUpdateRecommendTimer(
                        nextUpdate, REQUEST_CODE_UPDATE_RECOMMEND
                    )

                    // Timer for next reminder
                    setNextRemindTimer()
                }
        )
    }

    private fun setNextRemindTimer() {
        recommendProgramRepository
            .getRecommendPrograms()
            .let {
                if (it.isNotEmpty()) {
                    val nextProgram = it[0]
                    recommendTimerSubmitter.setRemindTimer(
                        nextProgram.start * 1000,
                        REQUEST_CODE_REMIND_ON_AIR
                    )
                }

            }
    }

    // TODO : Bootcompleteを取る

}