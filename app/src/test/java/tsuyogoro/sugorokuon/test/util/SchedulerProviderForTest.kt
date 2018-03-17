package tsuyogoro.sugorokuon.test.util

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import tsuyogoro.sugorokuon.rx.SchedulerProvider

class SchedulerProviderForTest : SchedulerProvider {

    override fun io(): Scheduler = Schedulers.trampoline()

    override fun computation(): Scheduler = Schedulers.trampoline()

    override fun mainThread(): Scheduler = Schedulers.trampoline()
}