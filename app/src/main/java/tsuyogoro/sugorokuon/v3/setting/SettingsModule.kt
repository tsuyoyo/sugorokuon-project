package tsuyogoro.sugorokuon.v3.setting

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.v3.repository.SettingsRepository
import tsuyogoro.sugorokuon.v3.service.SettingsService

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

}