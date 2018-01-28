package tsuyogoro.sugorokuon.v3.timetable

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import io.reactivex.disposables.CompositeDisposable
import tsuyogoro.sugorokuon.v3.api.response.TimeTableResponse
import tsuyogoro.sugorokuon.v3.service.TimeTableService

class ProgramInfoViewModel(
        timeTableService: TimeTableService,
        programId: String,
        private val disposable: CompositeDisposable = CompositeDisposable()
) : ViewModel() {

    @Suppress("UNCHECKED_CAST")
    class Factory(
            private val timeTableService: TimeTableService,
            private val programId: String
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ProgramInfoViewModel(
                    timeTableService,
                    programId
            ) as T
        }
    }

    private val program: MutableLiveData<TimeTableResponse.Program> = MutableLiveData()

    init {
        disposable.add(
                timeTableService
                        .getProgram(programId)
                        .doOnSuccess(program::postValue)
                        .subscribe()
        )
    }

    fun observeProgram(): LiveData<TimeTableResponse.Program> = program

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}