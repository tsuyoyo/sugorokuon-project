package tsuyogoro.sugorokuon.setting

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import tsuyogoro.sugorokuon.constant.SearchSongMethod
import tsuyogoro.sugorokuon.service.SettingsService

class SearchSongMethodViewModel(
        private val settingsService: SettingsService,
        private val disposable: CompositeDisposable = CompositeDisposable()
) : ViewModel() {

    @Suppress("UNCHECKED_CAST")
    class Factory(
            private val settingsService: SettingsService
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                SearchSongMethodViewModel(settingsService) as T
    }

    private val selectedMethod: MutableLiveData<SearchSongMethod> = MutableLiveData()

    init {
        disposable.addAll(
                settingsService
                        .observeSelectedSongSearchMethod()
                        .subscribe(),

                settingsService.observeSelectedSongSearchMethod()
                        .doOnNext(selectedMethod::postValue)
                        .subscribe()
        )
    }

    fun getOptions(): List<SearchSongMethod> = SearchSongMethod.values().toList()

    fun observeSelectedMethod(): LiveData<SearchSongMethod> = selectedMethod

    fun selectSearchSongMethod(method: SearchSongMethod) =
            disposable.add(
                    settingsService.setSongSearchMethod(method)
                            .observeOn(Schedulers.io())
                            .subscribe()
            )

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }
}