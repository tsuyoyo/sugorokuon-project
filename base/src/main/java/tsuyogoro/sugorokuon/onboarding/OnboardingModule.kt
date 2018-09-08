package tsuyogoro.sugorokuon.onboarding

import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.service.SettingsService
import tsuyogoro.sugorokuon.service.TutorialService

@Module
class OnboardingModule {

    @Provides
    fun provideOnboardingViewModelFactory(
            settingsService: SettingsService,
            tutorialService: TutorialService
    ) : OnboardingViewModel.Factory = OnboardingViewModel.Factory(settingsService, tutorialService)

}