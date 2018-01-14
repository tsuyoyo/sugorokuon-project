package tsuyogoro.sugorokuon.v3.timetable

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import io.reactivex.disposables.CompositeDisposable
import tsuyogoro.sugorokuon.v3.api.response.TimeTableResponse
import tsuyogoro.sugorokuon.v3.constant.Area
import tsuyogoro.sugorokuon.v3.extension.searchProgram
import tsuyogoro.sugorokuon.v3.repository.TimeTableRepository
import tsuyogoro.sugorokuon.v3.rx.SchedulerProvider
import java.util.*

class ProgramInfoViewModel(
        date: Calendar,
        area: Area,
        timeTableRepository: TimeTableRepository,
        schedulerProvider: SchedulerProvider,
        private val programId: String,
        private val disposable: CompositeDisposable = CompositeDisposable()
) : ViewModel() {

    @Suppress("UNCHECKED_CAST")
    class Factory(
            private val date: Calendar,
            private val area: Area,
            private val programId: String,
            private val timeTableRepository: TimeTableRepository,
            private val schedulerProvider: SchedulerProvider
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ProgramInfoViewModel(
                    date,
                    area,
                    timeTableRepository,
                    schedulerProvider,
                    programId
            ) as T
        }
    }

    private val program: MutableLiveData<TimeTableResponse.Program> = MutableLiveData()

    init {
        disposable.add(
                timeTableRepository
                        .fetchTimeTable(date, area)
                        .subscribeOn(schedulerProvider.io())
                        .doOnSuccess {
                            program.postValue(it.searchProgram(programId))
                        }
                        .subscribe()
        )
    }

    fun observeProgram(): LiveData<TimeTableResponse.Program> = program

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}