package tsuyogoro.sugorokuon.v3.rx

import io.reactivex.Scheduler

interface SchedulerProvider {

    fun io() : Scheduler

    fun computation(): Scheduler

    fun mainThread() : Scheduler
}