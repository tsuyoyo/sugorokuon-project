/**
 * Copyright (c)
 * 2018 tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.recommend

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*

class RecommendTimerService(
    private val context: Context,
    private val recommendProgramRepository: RecommendProgramRepository,
    private val recommendConfigs: RecommendConfigs
) {
    fun setNextRemindTimer(
        requestCode: Int = RecommendBroadCastReceiver.REQUEST_CODE_REMIND_ON_AIR) {
        recommendProgramRepository
            .getRecommendPrograms()
            .let {
                if (it.isNotEmpty()) {
                    setTimer(
                        it[0].start * 1000,
                        createPendingIntentForRemindOnAir(requestCode)
                    )
                }
            }
    }

    fun cancelRemindTimer(
        requestCode: Int = RecommendBroadCastReceiver.REQUEST_CODE_REMIND_ON_AIR) {
        cancelTimer(createPendingIntentForRemindOnAir(requestCode))
    }

    fun setUpdateRecommendTimer(
        requestCode: Int = RecommendBroadCastReceiver.REQUEST_CODE_UPDATE_RECOMMEND) {
        setTimer(
            getNextUpdateTimeInMilliSec(),
            createPendingIntentForUpdateRecommend(requestCode)
        )
    }

    private fun getNextUpdateTimeInMilliSec() =
        Calendar.getInstance().timeInMillis + recommendConfigs.getUpdateIntervalInSec() * 1000

    fun cancelUpdateRecommendTimer(
        requestCode: Int = RecommendBroadCastReceiver.REQUEST_CODE_UPDATE_RECOMMEND) {
        cancelTimer(createPendingIntentForUpdateRecommend(requestCode))
    }

    private fun getAlarmManager() = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private fun setTimer(timeInMilliSec: Long, pendingIntent: PendingIntent) {
        // TODO : Androidバージョンで分岐を
        getAlarmManager().setExact(
            AlarmManager.RTC_WAKEUP,
            timeInMilliSec,
            pendingIntent
        )
    }

    private fun cancelTimer(pendingIntent: PendingIntent) {
        pendingIntent.cancel()
        getAlarmManager().cancel(pendingIntent)
    }

    private fun createPendingIntent(requestCode: Int, intent: Intent) =
        PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

    private fun createPendingIntentForRemindOnAir(requestCode: Int) =
        createPendingIntent(
            requestCode,
            RecommendBroadCastReceiver.createIntentForRemindOnAir(context)
        )

    private fun createPendingIntentForUpdateRecommend(requestCode: Int) =
        createPendingIntent(
            requestCode,
            RecommendBroadCastReceiver.createIntentForUpdateRecommend(context)
        )
}