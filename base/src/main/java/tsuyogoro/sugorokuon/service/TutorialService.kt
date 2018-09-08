package tsuyogoro.sugorokuon.service

import io.reactivex.Completable
import io.reactivex.Flowable
import tsuyogoro.sugorokuon.repository.AppPrefRepository

class TutorialService(
        private val appPrefRepository: AppPrefRepository
) {
    fun observeDoneTutorialV3(): Flowable<Boolean> = appPrefRepository.observeTutorialV3Done()

    fun doneTutorialV3(): Completable = Completable.fromAction {
        appPrefRepository.setTutorialV3Done(true)
    }
}