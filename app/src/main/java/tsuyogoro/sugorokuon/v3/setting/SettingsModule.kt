package tsuyogoro.sugorokuon.v3.setting

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.v3.repository.SettingsRepository

@Module
class SettingsModule {

    @Provides
    fun provideAreaSettingViewModelFactory(
            settingsRepository: SettingsRepository,
            resources: Resources
    ) = AreaSettingsViewModel.Factory(settingsRepository, resources)

    @Provides
    fun provideSettingsTopViewModelFactory(
            settingsRepository: SettingsRepository,
            resources: Resources
    ) = SettingsTopViewModel.Factory(settingsRepository, resources)

}