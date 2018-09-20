package tsuyogoro.sugorokuon

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.BottomNavigationView
import android.support.transition.Transition
import android.support.transition.TransitionSet
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.google.android.gms.ads.MobileAds
import com.tomoima.debot.Debot
import io.reactivex.Flowable
import io.reactivex.processors.PublishProcessor
import tsuyogoro.sugorokuon.base.R
import tsuyogoro.sugorokuon.onboarding.OnboardingActivity
import tsuyogoro.sugorokuon.search.SearchFragment
import tsuyogoro.sugorokuon.setting.SettingsTopFragment
import tsuyogoro.sugorokuon.songs.OnAirSongsRootFragment
import tsuyogoro.sugorokuon.timetable.ProgramTableFragment
import tsuyogoro.sugorokuon.utils.RadikoLauncher
import javax.inject.Inject

class SugorokuonTopActivity : AppCompatActivity() {

    private object FragmentTags {
        val PROGRAM_TABLE = "program_table"
        val ON_AIR_SONGS = "on_air_songs"
        val SEARCH = "search"
        val SETTINGS = "settings"
        val AREA_SETTINGS = "area_settings"
    }

    private object REQUEST_CODE {
        val ON_BOARDING = 1
    }

    private val toolBar: Toolbar
        get() = findViewById(R.id.toolbar)

    private val appBarLayout: AppBarLayout
        get() = findViewById(R.id.appbar_layout)

    private val bottomNavigationView: BottomNavigationView
        get() = findViewById(R.id.bottom_navigation)

    private val searchForm: EditText
        get() = findViewById(R.id.search_form)

    private val radioMenuButton: View
        get() = findViewById(R.id.radio_menu)

    @Inject
    lateinit var viewModelFactory: SugorokuonTopViewModel.Factory

    private lateinit var viewModel: SugorokuonTopViewModel

    private val showTutorialSignalObserver = Observer<Boolean> {
        if (it != null && it) {
            startActivityForResult(
                    OnboardingActivity.createIntent(this),
                    REQUEST_CODE.ON_BOARDING)
        }
    }

    private val searchWordPublisher: PublishProcessor<String> = PublishProcessor.create()

    lateinit var debot: Debot

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        debot = Debot.getInstance()
        debot.allowShake(applicationContext)

        SugorokuonApplication.application(this)
                .appComponent()
                .sugorokuonTopSubComponent(SugorokuonTopModule())
                .inject(this)

        setContentView(R.layout.activity_top)

        setupActionBar()
        setupBottomNavigation()

        if (savedInstanceState == null) {
            bottomNavigationView.selectedItemId = R.id.program_table
        }

        viewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(SugorokuonTopViewModel::class.java)

        supportFragmentManager.addOnBackStackChangedListener {
            val fm = supportFragmentManager
            if (fm.backStackEntryCount == 0) {
                finish()
                return@addOnBackStackChangedListener
            }

            // When the top is one of Fragments which can be activated via bottom navigation,
            // then set focus on bottom navigation. It's required in case of "back" operation.
            val topFragmentName = fm.getBackStackEntryAt(fm.backStackEntryCount - 1)?.name
            val bottomMenuIdForTopFragment = when (topFragmentName) {
                FragmentTags.ON_AIR_SONGS -> R.id.on_air_songs
                FragmentTags.SETTINGS -> R.id.settings
                FragmentTags.PROGRAM_TABLE -> R.id.program_table
                else -> null
            }
            if (bottomMenuIdForTopFragment != null) {
                bottomNavigationView.selectedItemId = bottomMenuIdForTopFragment
            }
        }

        MobileAds.initialize(this, getString(R.string.radibangBannerAppId))
    }

    override fun onResume() {
        super.onResume()
        viewModel.observeRequestToShowTutorial()
                .observe(this, showTutorialSignalObserver)
        debot.startSensor(this)
    }

    override fun onPause() {
        viewModel.observeRequestToShowTutorial()
                .removeObserver(showTutorialSignalObserver)
        super.onPause()
        debot.stopSensor()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE.ON_BOARDING ->
                if (resultCode != Activity.RESULT_OK) {
                    finish()
                } else {
                    super.onActivityResult(requestCode, resultCode, data)
                }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && searchForm.text.isNotBlank()) {
            searchForm.text.clear()
            searchWordPublisher.onNext("")
            return true
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            debot.showDebugMenu(this)
            return super.onKeyDown(keyCode, event)
        } else {
            return super.onKeyDown(keyCode, event)
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolBar)
        supportActionBar?.let {
            it.title = ""
        }

        searchForm.setOnTouchListener { _, event ->
            if (event.action == ACTION_UP) {
                searchForm.requestFocusFromTouch()
            }
            return@setOnTouchListener false
        }
        searchForm.setOnClickListener {
            switchFragment(SearchFragment(), FragmentTags.SEARCH)
        }
        searchForm.imeOptions = EditorInfo.IME_ACTION_SEARCH

        searchForm.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchWordPublisher.onNext(v.text.toString())
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        radioMenuButton.setOnClickListener {
            RadikoLauncher.launch(this)
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.program_table -> openProgramTable()
                R.id.on_air_songs -> openOnAirSongs()
                R.id.settings -> openSettings()
            }
            return@setOnNavigationItemSelectedListener true
        }
    }

    private fun openProgramTable() {
        appBarLayout.setExpanded(true, true)
        switchFragment(ProgramTableFragment(), FragmentTags.PROGRAM_TABLE)
    }

    private fun openOnAirSongs() {
        appBarLayout.setExpanded(true, true)
        switchFragment(OnAirSongsRootFragment(), FragmentTags.ON_AIR_SONGS)
    }

    private fun openSettings() {
        appBarLayout.setExpanded(true, true)
        switchFragment(SettingsTopFragment(), FragmentTags.SETTINGS)
    }

    fun switchFragment(fragment: Fragment, tag: String, transition: Transition? = null) {
        val fm = supportFragmentManager
        if (fm.findFragmentByTag(tag) == null) {
            if (transition != null) {
                fragment.enterTransition = TransitionSet().addTransition(transition)
            }
            fm.beginTransaction()
                    .add(R.id.fragment_area, fragment, tag)
                    .addToBackStack(tag)
                    .commit()
        } else {
            while (fm.getBackStackEntryAt(fm.backStackEntryCount - 1).name != tag) {
                fm.popBackStackImmediate()
            }
        }
    }

    fun pushFragment(fragment: Fragment, tag: String, transition: Transition? = null) {
        val fm = supportFragmentManager
        val topIndex = fm.backStackEntryCount - 1
        if (topIndex >= 0
                && fm.getBackStackEntryAt(topIndex).name != tag) {
            if (transition != null) {
                fragment.enterTransition = TransitionSet().addTransition(transition)
            }
            fm.beginTransaction()
                    .add(R.id.fragment_area, fragment, tag)
                    .addToBackStack(tag)
                    .commit()
        }
    }

    fun setFocusOnSearchForm() {
        searchForm.requestFocusFromTouch()
    }

    fun observeSearchKeyword(): Flowable<String> = searchWordPublisher.hide()

}