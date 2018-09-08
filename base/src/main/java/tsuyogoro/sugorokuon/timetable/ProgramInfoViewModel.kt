package tsuyogoro.sugorokuon.timetable

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import io.reactivex.disposables.CompositeDisposable
import tsuyogoro.sugorokuon.api.response.TimeTableResponse
import tsuyogoro.sugorokuon.rx.SchedulerProvider
import tsuyogoro.sugorokuon.service.TimeTableService

class ProgramInfoViewModel(
        timeTableService: TimeTableService,
        programId: String,
        schedulerProvider: SchedulerProvider,
        private val disposable: CompositeDisposable = CompositeDisposable()
) : ViewModel() {

    @Suppress("UNCHECKED_CAST")
    class Factory(
            private val timeTableService: TimeTableService,
            private val programId: String,
            private val schedulerProvider: SchedulerProvider
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ProgramInfoViewModel(
                    timeTableService,
                    programId,
                    schedulerProvider
            ) as T
        }
    }

    private val program: MutableLiveData<TimeTableResponse.Program> = MutableLiveData()

    init {
        disposable.add(
                timeTableService
                        .getProgram(programId)
                        .doOnSuccess(program::postValue)
                        .subscribeOn(schedulerProvider.io())
                        .subscribe()
        )
    }

    fun observeProgram(): LiveData<TimeTableResponse.Program> = program

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}