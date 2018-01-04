package tsuyogoro.sugorokuon.v3.rx

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SchedulerProviderForApp : SchedulerProvider {

    override fun io(): Scheduler = Schedulers.io()

    override fun computation(): Scheduler = Schedulers.computation()

    override fun mainThread(): Scheduler = AndroidSchedulers.mainThread()
}