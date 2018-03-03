package tsuyogoro.sugorokuon.v3.onboarding

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import tsuyogoro.sugorokuon.R
import tsuyogoro.sugorokuon.SugorokuonApplication
import tsuyogoro.sugorokuon.v3.setting.AreaSettingsFragment
import javax.inject.Inject

class OnboardingActivity : AppCompatActivity() {

    @BindView(R.id.next)
    lateinit var nextButton: Button

    @BindView(R.id.done_onboarding)
    lateinit var onBoardingDoneButton: Button

    @Inject
    lateinit var onBoardingViewModelFactory: OnboardingViewModel.Factory

    private lateinit var viewModel: OnboardingViewModel

    companion object {
        fun createIntent(context: Context) = Intent(context, OnboardingActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SugorokuonApplication
                .application(this)
                .appComponent()
                .onBoardingSubComponent(OnboardingModule())
                .inject(this)

        viewModel = ViewModelProviders
                .of(this, onBoardingViewModelFactory)
                .get(OnboardingViewModel::class.java)

        setContentView(R.layout.activity_onboarding)
        ButterKnife.bind(this)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_area, TutorialFragment.create(0))
                    .addToBackStack(null)
                    .commit()
        }

        supportFragmentManager.addOnBackStackChangedListener {
            when (supportFragmentManager.backStackEntryCount) {
                in 1..3 -> {
                    nextButton.visibility = View.VISIBLE
                    onBoardingDoneButton.visibility = View.GONE
                }
                4 -> {
                    nextButton.visibility = View.GONE
                    onBoardingDoneButton.visibility = View.VISIBLE
                }
                else -> finish()
            }
        }

        viewModel
                .observeSetupCompletion()
                .observe(this, Observer {
                    onBoardingDoneButton.isEnabled = it ?: false
                })
    }

    @OnClick(R.id.next)
    fun onNextClicked() {
        when (supportFragmentManager.backStackEntryCount) {
            1 -> supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_area, TutorialFragment.create(1))
                    .addToBackStack(null)
                    .commit()
            2 -> supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_area, TutorialFragment.create(2))
                    .addToBackStack(null)
                    .commit()
            3 -> supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_area, AreaSettingsFragment())
                    .addToBackStack(null)
                    .commit()
        }
    }

    @OnClick(R.id.done_onboarding)
    fun onDoneClicked() {
        setResult(Activity.RESULT_OK)
        finish()
    }
}