package tsuyogoro.sugorokuon.v3.setting

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.res.Resources
import io.reactivex.disposables.CompositeDisposable
import tsuyogoro.sugorokuon.R
import tsuyogoro.sugorokuon.v3.constant.Area
import tsuyogoro.sugorokuon.v3.service.SettingsService

class AreaSettingsViewModel(
        private val settingsService: SettingsService,
        private val resources: Resources,
        private val disposable: CompositeDisposable = CompositeDisposable()
) : ViewModel() {

    @Suppress("UNCHECKED_CAST")
    class Factory(
            private val settingsService: SettingsService,
            private val resources: Resources
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                AreaSettingsViewModel(settingsService, resources) as T
    }

    private val allAreas = MutableLiveData<List<Area>>()
    private val selectedAreas = MutableLiveData<Set<Area>>()
    private val selectedAreasLabel = MutableLiveData<String>()

    init {
        disposable.addAll(
                settingsService.observeAreas()
                        .subscribe {
                            selectedAreas.value = it
                            selectedAreasLabel.value = if (it.isEmpty()) {
                                resources.getString(R.string.settings_area_not_selected)
                            } else {
                                resources.getString(
                                        R.string.settings_area_selected,
                                        it.joinToString(
                                                separator = "、",
                                                transform = { area -> resources.getString(area.strId) }
                                        )
                                )
                            }
                        }
        )
        allAreas.value = Area.values().sortedBy { it.ordinal }
    }

    fun observeAllAreas(): LiveData<List<Area>> = allAreas

    fun observeSelectedAreas(): LiveData<Set<Area>> = selectedAreas

    fun observeSelectedAreasLabel(): LiveData<String> = selectedAreasLabel

    fun selectArea(area: Area) {
        settingsService.selectArea(area)
    }

    fun deselectArea(area: Area) {
        settingsService.deselectArea(area)

        // TODO : 選択項目が0になってしまった時にアラートを出す
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}
