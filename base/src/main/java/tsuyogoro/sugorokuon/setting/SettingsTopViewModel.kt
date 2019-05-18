package tsuyogoro.sugorokuon.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.content.res.Resources
import io.reactivex.disposables.CompositeDisposable
import tsuyogoro.sugorokuon.constant.SearchSongMethod
import tsuyogoro.sugorokuon.settings.SettingsRepository

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

    private val searchSongWay = MutableLiveData<SearchSongMethod>()

    init {
        disposable.addAll(
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
                        },
                settingsRepository
                    .observeSelectedWaySerachSong()
                    .subscribe(searchSongWay::postValue)
        )
    }

    fun observeSelectedAreas(): LiveData<String> = selectedAreas

    fun observeSelectedSerachSongWay(): LiveData<SearchSongMethod> = searchSongWay

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}