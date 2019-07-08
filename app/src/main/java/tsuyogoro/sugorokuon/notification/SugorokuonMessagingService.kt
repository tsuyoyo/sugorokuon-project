package tsuyogoro.sugorokuon.notification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import tsuyogoro.sugorokuon.SugorokuonLog

/**
 * Note : This service to receive message when app is in foreground.
 * https://firebase.google.com/docs/cloud-messaging/android/receive?authuser=0#onmessagereceived-
 */
class SugorokuonMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        SugorokuonLog.d("onMessageReceived")
        SugorokuonLog.d("  from : ${remoteMessage?.from}")
        SugorokuonLog.d("  message : ${remoteMessage?.notification?.body}")
    }

//    override fun zzd(p0: Intent?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
}