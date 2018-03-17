package tsuyogoro.sugorokuon.setting

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.res.Resources
import io.reactivex.disposables.CompositeDisposable
import tsuyogoro.sugorokuon.repository.SettingsRepository

class SettingsTopViewModel(
        settingsRepository: SettingsRepository,
        private val resources: Resources,
        private val disposable: CompositeDisposable = CompositeDisposable()
) : ViewModel() {

    @Suppress("UNCHECKED_CAST")
    class Factory(
            private val settingsRepository: SettingsRepository,
            private val resources: Resources
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SettingsTopViewModel(settingsRepository, resources) as T
        }
    }

    private val selectedAreas = MutableLiveData<String>()

    init {
        disposable.add(
                settingsRepository
                        .observeAreaSettings()
                        .subscribe { areas ->
                            val selectedAreasValue = areas.joinToString(
                                    separator = ",",
                                    transform = { resources.getString(it.strId) }
                            )
                            selectedAreas.value = if (selectedAreasValue.isNotBlank()) {
                                selectedAreasValue
                            } else {
                                " - "
                            }
                        }
        )
    }

    fun observeSelectedAreas(): LiveData<String> = selectedAreas

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}