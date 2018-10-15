package tsuyogoro.sugorokuon.recommend

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import tsuyogoro.sugorokuon.SugorokuonLog
import tsuyogoro.sugorokuon.recommend.reminder.RecommendRemindNotifier
import javax.inject.Inject

class RecommendBroadCastReceiver : BroadcastReceiver() {

    companion object {
        internal const val REQUEST_CODE_REMIND_ON_AIR = 100
        internal const val REQUEST_CODE_UPDATE_RECOMMEND = 101

        private const val ACTION_REMIND_ON_AIR = "remindOnAir"
        private const val ACTION_UPDATE_RECOMMEND = "updateRecommend"

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
    internal lateinit var recommendTimerService: RecommendTimerService

    @Inject
    internal lateinit var recommendConfigs: RecommendConfigs

    private val disposables = CompositeDisposable()

    override fun onReceive(context: Context, intent: Intent?) {
        SugorokuonLog.d("RecommendBroadCastReceiver - S")
        DaggerRecommendComponent.builder()
            .recommendInternalModule(RecommendInternalModule(context))
            .build()
            .inject(this)

        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                SugorokuonLog.d("Boot completed!!!")
                updateRecommend()
            }
            ACTION_REMIND_ON_AIR -> remindOnAirProgram(context)
            ACTION_UPDATE_RECOMMEND -> updateRecommend()
        }
        SugorokuonLog.d("RecommendBroadCastReceiver - E")
    }

    private fun remindOnAirProgram(context: Context) {
        val programs = recommendProgramRepository.getRecommendPrograms()

        if (programs.isEmpty()) {
            SugorokuonLog.d("No more program to remind")
        }

        val remindProgram = programs[0]
        SugorokuonLog.d("- Remind : ${remindProgram.title}")

        recommendProgramRepository.delete(remindProgram)

        recommendRemindNotifier
            .notifyReminder(context, remindProgram)
            .subscribeOn(Schedulers.io())
            .doOnComplete {
                recommendTimerService.setNextRemindTimer(REQUEST_CODE_REMIND_ON_AIR)
            }
            .subscribe()
            .addTo(disposables)
    }

    private fun updateRecommend() {
        recommendTimerService.cancelUpdateRecommendTimer(REQUEST_CODE_UPDATE_RECOMMEND)

        recommendSearchService
            .fetchRecommendPrograms()
            .doOnSuccess {
                recommendSearchService.updateRecommendProgramsInDatabase(it)
            }
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                recommendTimerService.setUpdateRecommendTimer(REQUEST_CODE_UPDATE_RECOMMEND)
                recommendTimerService.setNextRemindTimer(REQUEST_CODE_REMIND_ON_AIR)
            }
            .subscribeBy(
                onSuccess = {
                    SugorokuonLog.d(
                        "Success to update recommend" +
                            "-- # of recommend programs in DB : " +
                            "${recommendProgramRepository.getRecommendPrograms().size}")
                },
                onError = {
                    // Anyway, set timer to invoke update next time.
                    recommendTimerService.setNextRemindTimer(REQUEST_CODE_REMIND_ON_AIR)
                    SugorokuonLog.e("Failed to update recommend : ${it.message}")
                }
            )
            .addTo(disposables)
    }

    // TODO : Bootcompleteを取る

}