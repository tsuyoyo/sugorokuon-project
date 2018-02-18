package tsuyogoro.sugorokuon.v3.onboarding

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import io.reactivex.disposables.CompositeDisposable
import tsuyogoro.sugorokuon.v3.service.SettingsService

class OnboardingViewModel(
        private val settingsService: SettingsService,
        private val disposables: CompositeDisposable = CompositeDisposable()
) : ViewModel() {

    @Suppress("UNCHECKED_CAST")
    class Factory(
            private val settingsService: SettingsService
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return OnboardingViewModel(
                    settingsService
            ) as T
        }
    }

    private val isSetupCompleted: MutableLiveData<Boolean> = MutableLiveData()

    init {
        disposables.addAll(
                settingsService
                        .observeAreas()
                        .doOnNext {
                            isSetupCompleted.postValue(it.isNotEmpty())
                        }
                        .subscribe()
        )
    }

    fun observeSetupCompletion() : LiveData<Boolean> = isSetupCompleted

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}