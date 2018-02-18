package tsuyogoro.sugorokuon.v3.firebase

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import tsuyogoro.sugorokuon.utils.SugorokuonLog

class SugorokuonFirebaseInstanceIdService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        super.onTokenRefresh()
        SugorokuonLog.d("Refreshed FB token : ${FirebaseInstanceId.getInstance().token}")
    }
}