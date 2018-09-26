package tsuyogoro.sugorokuon.setting

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.data.SettingsRepository
import tsuyogoro.sugorokuon.service.SettingsService

@Module
class SettingsModule {

    @Provides
    fun provideAreaSettingViewModelFactory(
            settingsService: SettingsService,
            resources: Resources
    ) = AreaSettingsViewModel.Factory(settingsService, resources)

    @Provides
    fun provideSettingsTopViewModelFactory(
        settingsRepository: SettingsRepository,
        resources: Resources
    ) = SettingsTopViewModel.Factory(settingsRepository, resources)

    @Provides
    fun provideStationsOrderSettingViewModelFactory(
            settingsService: SettingsService
    ) = StationOrderViewModel.Factory(settingsService)

    @Provides
    fun provideSearchSongMethodSettingViewModelFactory(
            settingsService: SettingsService
    ) = SearchSongMethodViewModel.Factory(settingsService)

}