package tsuyogoro.sugorokuon.v3.onboarding

import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.v3.service.SettingsService

@Module
class OnboardingModule {

    @Provides
    fun provideOnboardingViewModelFactory(
            settingsService: SettingsService
    ) : OnboardingViewModel.Factory = OnboardingViewModel.Factory(settingsService)

}