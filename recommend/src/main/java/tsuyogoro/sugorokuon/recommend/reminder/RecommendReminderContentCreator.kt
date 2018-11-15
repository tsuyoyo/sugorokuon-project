package tsuyogoro.sugorokuon.recommend.reminder

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.graphics.BitmapFactory
import android.support.v4.app.NotificationCompat
import tsuyogoro.sugorokuon.recommend.R
import tsuyogoro.sugorokuon.recommend.RecommendProgram
import tsuyogoro.sugorokuon.station.Station
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

internal class RecommendReminderContentCreator {

    fun createReminderNotification(
        context: Context,
        program: RecommendProgram,
        station: Station,
        notificationBuilder: NotificationCompat.Builder
    ) : Notification {
        val programImageInputStream = URL(program.image).openStream()
        val programImageBitmap = BitmapFactory.decodeStream(programImageInputStream)
        val startTime = SimpleDateFormat("MM/dd(EEE) HH:mm", Locale.JAPAN)
            .let { it.format(program.start) }

        notificationBuilder
            .setContentTitle(program.title)
            .setSubText(context.getString(R.string.recommend_reminder_ticker))
            .setContentText(
                context.getString(R.string.recommend_reminder_text, startTime, station.name)
            )
            .setLargeIcon(programImageBitmap)
            .setContentIntent(PendingIntent.getActivity(
                context,
                0,
                context.packageManager.getLaunchIntentForPackage(
                    context.applicationContext.packageName
                ),
                PendingIntent.FLAG_UPDATE_CURRENT
            ))

        programImageInputStream.close()

        return notificationBuilder.build().apply {
            flags = flags or Notification.FLAG_AUTO_CANCEL
        }
    }
}