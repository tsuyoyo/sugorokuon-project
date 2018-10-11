/**
 * Copyright (c)
 * 2018 tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context

class RecommendRemindTimerSubmitter(private val context: Context) {

    fun setTimer(timeInMilliSec: Long) {
        // TODO : Androidバージョンで分岐を
        getAlarmManager().setExact(
            AlarmManager.RTC_WAKEUP,
            timeInMilliSec,
            createPendingIntent()
        )
    }

    private fun getAlarmManager() = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private fun createPendingIntent() = PendingIntent.getBroadcast(
        context,
        RecommendRemindBroadCastReceiver.REQUEST_CODE,
        RecommendRemindBroadCastReceiver.createIntent(context),
        PendingIntent.FLAG_CANCEL_CURRENT
    )
}