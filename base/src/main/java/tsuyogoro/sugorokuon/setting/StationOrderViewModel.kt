package tsuyogoro.sugorokuon.setting

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import io.reactivex.disposables.CompositeDisposable
import tsuyogoro.sugorokuon.api.response.StationResponse
import tsuyogoro.sugorokuon.service.SettingsService

class StationOrderViewModel(
        private val settingsService: SettingsService
) : ViewModel() {

    @Suppress("UNCHECKED_CAST")
    class Factory(
            private val settingsService: SettingsService
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return StationOrderViewModel(settingsService) as T
        }
    }

    private val orderedStations = MutableLiveData<List<StationResponse.Station>>()

    private val disposables = CompositeDisposable()

    init {
        disposables.add(
                settingsService
                        .observeOrderedStations()
                        .subscribe(orderedStations::postValue)
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }

    fun updateStationOrder(orderedStations: List<StationResponse.Station>) {
        settingsService.updateStationOrder(orderedStations)
    }

    fun observeOrderedStations(): LiveData<List<StationResponse.Station>> = orderedStations

}