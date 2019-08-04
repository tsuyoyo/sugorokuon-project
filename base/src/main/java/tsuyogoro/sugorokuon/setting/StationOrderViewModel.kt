package tsuyogoro.sugorokuon.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.disposables.CompositeDisposable
import tsuyogoro.sugorokuon.radiko.api.response.StationResponse
import tsuyogoro.sugorokuon.service.SettingsService
import tsuyogoro.sugorokuon.station.Station

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

    private val orderedStations = MutableLiveData<List<Station>>()

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

    fun updateStationOrder(orderedStations: List<Station>) {
        settingsService.updateStationOrder(orderedStations)
    }

    fun observeOrderedStations(): LiveData<List<Station>> = orderedStations

}