package tsuyogoro.sugorokuon.appstate

import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import tsuyogoro.sugorokuon.appstate.AppPrefs

class AppPrefRepository(
    private val appPrefs: AppPrefs,
    private val doneTutorialV3: BehaviorProcessor<Boolean> = BehaviorProcessor.create()
) {

    init {
        doneTutorialV3.onNext(appPrefs.getTutorial3Done(false))
    }

    fun observeTutorialV3Done() : Flowable<Boolean> = doneTutorialV3.hide()

    fun setTutorialV3Done(done: Boolean) {
        appPrefs.putTutorial3Done(done)
        doneTutorialV3.onNext(done)
    }
}