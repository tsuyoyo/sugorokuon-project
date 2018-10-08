package tsuyogoro.sugorokuon.recommend.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import io.reactivex.Completable
import io.reactivex.Maybe
import tsuyogoro.sugorokuon.recommend.R
import tsuyogoro.sugorokuon.recommend.database.RecommendProgram
import tsuyogoro.sugorokuon.recommend.reminder.ReminderType
import tsuyogoro.sugorokuon.recommend.settings.RecommendSettingsRepository
import java.net.URL

class RecommendRemindNotifier(
    context: Context,
    private val recommendSettingsRepository: RecommendSettingsRepository
) {
    companion object {
        private const val CHANNEL_ID = "sugorokuon_recommend"
        private const val CHANNEL_NAME = "SugorokuonRecommendChannel"

        private const val REMINDER_NOTIFICATION_ID = 100
    }

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


    // TODO : ここにStationResponse.Stationのインスタンスを食わせるようにする
    // しかし、どのみち、station情報はDBに格納するなどしないとstation情報とか取れないので、StationRepositoryの作りを変えねば
    fun notifyReminder(context: Context, program: RecommendProgram): Completable =
        createReminderNotification(context, program)
            .doOnSuccess { notificationManager.notify(REMINDER_NOTIFICATION_ID, it) }
            .ignoreElement()

    private fun createReminderNotification(
        context: Context,
        program: RecommendProgram
    ) : Maybe<Notification> = Maybe.fromCallable {
        val programImageInputStream = URL(program.image).openStream()
        val programImageBitmap = BitmapFactory.decodeStream(programImageInputStream)
        val builder = createNotificationBuilder(context)
            .setContentTitle(program.title)
            .setSubText(context.getString(R.string.recommend_reminder_ticker))
            .setContentText(program.description)
            .setLargeIcon(programImageBitmap)

        val notification = NotificationCompat.BigPictureStyle(builder)
//            .setBigContentTitle(program.title)
//            .setSummaryText(program.description)
            .bigPicture(programImageBitmap)
            .bigLargeIcon(programImageBitmap)
            .build()

        programImageInputStream.close()

        return@fromCallable notification
    }



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
            .setSmallIcon(android.R.drawable.ic_delete)
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