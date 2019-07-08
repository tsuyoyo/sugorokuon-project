/**
 * Copyright (c)
 * 2018 tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.recommend.reminder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import io.reactivex.Completable
import tsuyogoro.sugorokuon.base.R
import tsuyogoro.sugorokuon.recommend.RecommendProgram
import tsuyogoro.sugorokuon.recommend.settings.RecommendSettingsRepository
import tsuyogoro.sugorokuon.station.StationRepository

internal class RecommendRemindNotifier(
    context: Context,
    private val recommendSettingsRepository: RecommendSettingsRepository,
    private val stationRepository: StationRepository
) {
    companion object {
        private const val CHANNEL_ID = "sugorokuon_recommend"

        private const val CHANNEL_NAME = "SugorokuonRecommendChannel"

        private const val REMINDER_NOTIFICATION_ID = 100
    }

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val reminderContentCreator = RecommendReminderContentCreator()

    // メモ :
    // Channelがいっしょなら、IDを変えることでnotificationがまとめられるような気がする
    // IDは現在時刻にすることで、通知がnotificationトレーで束になっていくのでは？
    fun notifyReminder(context: Context, program: RecommendProgram): Completable =
        stationRepository.observeStations().firstElement()
            .map { it.find { s -> s.id == program.stationId } }
            .map {
                reminderContentCreator.createReminderNotification(
                    context, program, it, createNotificationBuilder(context))
            }
            .doOnSuccess { notificationManager.notify(REMINDER_NOTIFICATION_ID, it) }
            .ignoreElement()

    private fun createNotificationBuilder(context: Context): NotificationCompat.Builder {
        // From Oreo, it's been required notificationChannel setup.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(makeSugorokuonNotificationChannel())
        }
        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CHANNEL_ID
        } else {
            ""
        }
        return NotificationCompat
            .Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification_small_icon)
            .setDefaults(makeDefaultNotificationSettingsFlag())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun makeSugorokuonNotificationChannel(): NotificationChannel =
        NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            lightColor = Color.BLUE
            lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        }

    private fun makeDefaultNotificationSettingsFlag() =
        recommendSettingsRepository.getReminderTypes()
            .let {
                var flag = 0
                if (it.contains(ReminderType.LIGHT)) {
                    flag = flag.or(Notification.DEFAULT_LIGHTS)
                }
                if (it.contains(ReminderType.SOUND)) {
                    flag = flag.or(Notification.DEFAULT_SOUND)
                }
                if (it.contains(ReminderType.VIBRATION)) {
                    flag = flag.or(Notification.DEFAULT_VIBRATE)
                }
                return@let flag
            }
}