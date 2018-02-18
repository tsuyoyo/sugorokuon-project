package tsuyogoro.sugorokuon.v3.onboarding

import dagger.Subcomponent

@Subcomponent(modules = [
    OnboardingModule::class
])
interface OnboardingComponent {

    fun inject(activity: OnboardingActivity)

}