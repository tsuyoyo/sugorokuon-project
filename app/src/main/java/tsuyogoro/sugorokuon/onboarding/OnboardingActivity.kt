package tsuyogoro.sugorokuon.onboarding

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.widget.Button
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import tsuyogoro.sugorokuon.R
import tsuyogoro.sugorokuon.SugorokuonApplication
import tsuyogoro.sugorokuon.setting.AreaSettingsFragment
import javax.inject.Inject

class OnboardingActivity : AppCompatActivity() {

    private val nextButton: Button
        get() = findViewById(R.id.next)

    private val onBoardingDoneButton: Button
        get() = findViewById(R.id.done_onboarding)

    @Inject
    lateinit var onBoardingViewModelFactory: OnboardingViewModel.Factory

    private val disposable = CompositeDisposable()

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

        nextButton.setOnClickListener { onNextClicked() }
        onBoardingDoneButton.setOnClickListener { onDoneClicked() }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_area, createTutorialFragment(0))
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

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    private fun onNextClicked() {
        when (supportFragmentManager.backStackEntryCount) {
            1 -> supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_area, createTutorialFragment(1))
                    .addToBackStack(null)
                    .commit()
            2 -> supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_area, createTutorialFragment(2))
                    .addToBackStack(null)
                    .commit()
            3 ->
                disposable.add(viewModel
                        .completeTutorial()
                        .observeOn(AndroidSchedulers.mainThread())
                        .andThen(Completable.fromAction {
                            supportFragmentManager.beginTransaction()
                                    .add(R.id.fragment_area, createAreaSettingsFragment())
                                    .addToBackStack(null)
                                    .commit()
                        })
                        .subscribe()
                )
        }
    }

    private fun createTutorialFragment(index: Int) = TutorialFragment
            .create(index)
            .apply {
                enterTransition = Slide(Gravity.RIGHT)
                exitTransition = Slide(Gravity.LEFT)
            }

    private fun createAreaSettingsFragment() = AreaSettingsFragment()
            .apply {
                enterTransition = Slide(Gravity.RIGHT)
                exitTransition = Slide(Gravity.LEFT)
            }

    private fun onDoneClicked() {
        setResult(Activity.RESULT_OK)
        finish()
    }
}