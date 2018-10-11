package tsuyogoro.sugorokuon.recommend.reminder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import io.reactivex.Completable
import io.reactivex.Maybe
import tsuyogoro.sugorokuon.recommend.R
import tsuyogoro.sugorokuon.recommend.RecommendProgram
import tsuyogoro.sugorokuon.recommend.settings.RecommendSettingsRepository
import tsuyogoro.sugorokuon.station.StationRepository
import java.net.URL


class RecommendRemindNotifier(
    context: Context,
    private val recommendSettingsRepository: RecommendSettingsRepository,
    private val stationRepository: StationRepository
) {
    companion object {
        private const val CHANNEL_ID = "sugorokuon_recommend"
        private const val CHANNEL_NAME = "SugorokuonRecommendChannel"
        private const val NOTIFICATION_LARGE_ICON_SIZE_DP = 16f

        private const val REMINDER_NOTIFICATION_ID = 100
    }

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // メモ :
    // Channelがいっしょなら、IDを変えることでnotificationがまとめられるような気がする
    // IDは現在時刻にすることで、通知がnotificationトレーで束になっていくのでは？
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

        val station = stationRepository.getStations().find { it.id == program.stationId }
        val stationImageInputStream = station?.let {
            val logoUrl = it.logo[0].url ?: return@let null
            return@let URL(logoUrl).openStream()
        }
        val stationImageBitmap = adjustLargeIcon(
            BitmapFactory.decodeStream(stationImageInputStream), context)

        val builder = createNotificationBuilder(context)
            .setContentTitle(program.title)
            .setSubText(context.getString(R.string.recommend_reminder_ticker))
            .setContentText(program.description)
//            .setLargeIcon(stationImageBitmap)

        val notification = NotificationCompat.BigPictureStyle(builder)
//            .setBigContentTitle(program.title)
//            .setSummaryText(program.description)
            .bigPicture(programImageBitmap)
            .bigLargeIcon(programImageBitmap)
            .build()

        programImageInputStream.close()
        stationImageInputStream?.close()

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

    private fun adjustLargeIcon(icon: Bitmap, context: Context): Bitmap {

//        val sourceWDp = calculateDpfromPx(context, icon.getWidth().toFloat())
//        val sourceHDp = calculateDpfromPx(context, icon.getHeight().toFloat())
//
//        val scale: Float
//        if (NOTIFICATION_LARGE_ICON_SIZE_DP / sourceHDp < NOTIFICATION_LARGE_ICON_SIZE_DP / sourceWDp) {
//            scale = NOTIFICATION_LARGE_ICON_SIZE_DP / sourceHDp
//        } else {
//            scale = NOTIFICATION_LARGE_ICON_SIZE_DP / sourceWDp
//        }

        val scale = NOTIFICATION_LARGE_ICON_SIZE_DP / icon.width

        val matrix = Matrix()

        matrix.postScale(scale, scale)

        return Bitmap.createBitmap(icon, 0, 0, icon.getWidth(), icon.getHeight(), matrix, true)
    }

    fun calculateDpfromPx(context: Context, px: Float): Float {
        return px / context.resources.displayMetrics.density
    }
}