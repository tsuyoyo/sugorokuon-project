package tsuyogoro.sugorokuon.v3.onboarding

import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.v3.service.SettingsService
import tsuyogoro.sugorokuon.v3.service.TutorialService

@Module
class OnboardingModule {

    @Provides
    fun provideOnboardingViewModelFactory(
            settingsService: SettingsService,
            tutorialService: TutorialService
    ) : OnboardingViewModel.Factory = OnboardingViewModel.Factory(settingsService, tutorialService)

}