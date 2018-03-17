package tsuyogoro.sugorokuon.rx

import io.reactivex.Scheduler

interface SchedulerProvider {

    fun io() : Scheduler

    fun computation(): Scheduler

    fun mainThread() : Scheduler
}