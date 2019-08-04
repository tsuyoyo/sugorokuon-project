package tsuyogoro.sugorokuon.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import tsuyogoro.sugorokuon.service.SettingsService
import tsuyogoro.sugorokuon.service.TutorialService

class OnboardingViewModel(
        private val settingsService: SettingsService,
        private val tutorialService: TutorialService,
        private val disposables: CompositeDisposable = CompositeDisposable()
) : ViewModel() {

    @Suppress("UNCHECKED_CAST")
    class Factory(
            private val settingsService: SettingsService,
            private val tutorialService: TutorialService
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return OnboardingViewModel(
                    settingsService, tutorialService
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

    fun completeTutorial() : Completable = tutorialService.doneTutorialV3()

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}