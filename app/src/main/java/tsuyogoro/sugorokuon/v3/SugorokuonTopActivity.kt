package tsuyogoro.sugorokuon.v3

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import butterknife.BindView
import butterknife.ButterKnife
import tsuyogoro.sugorokuon.R
import tsuyogoro.sugorokuon.SugorokuonApplication
import tsuyogoro.sugorokuon.v3.setting.SettingsTopFragment
import tsuyogoro.sugorokuon.v3.songs.OnAirSongsRootFragment
import tsuyogoro.sugorokuon.v3.timetable.ProgramTableFragment
import javax.inject.Inject

class SugorokuonTopActivity : AppCompatActivity() {

    object FragmentTags {
        val PROGRAM_TABLE = "program_table"
        val ON_AIR_SONGS = "on_air_songs"
        val SETTINGS = "settings"
    }

    @BindView(R.id.toolbar)
    lateinit var toolBar: Toolbar

    @BindView(R.id.appbar_layout)
    lateinit var appBarLayout: AppBarLayout

    @BindView(R.id.bottom_navigation)
    lateinit var bottomNavigationView: BottomNavigationView

    @Inject
    lateinit var viewModelFactory: SugorokuonTopViewModel.Factory

    private lateinit var viewModel: SugorokuonTopViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SugorokuonApplication.application(this)
                .appComponent()
                .sugorokuonTopSubComponent(SugorokuonTopModule())
                .inject(this)

        setContentView(R.layout.activity_top)
        ButterKnife.bind(this)

        setupActionBar()
        setupBottomNavigation()

        if (savedInstanceState == null) {
            bottomNavigationView.selectedItemId = R.id.program_table
        }

        viewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(SugorokuonTopViewModel::class.java)
    }

    private fun setupActionBar() {
        setSupportActionBar(toolBar)

        supportActionBar?.let {
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
        when(item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
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
        replaceFragment(ProgramTableFragment(), FragmentTags.PROGRAM_TABLE)
    }

    private fun openOnAirSongs() {
        replaceFragment(OnAirSongsRootFragment(), FragmentTags.ON_AIR_SONGS)
    }

    private fun openSettings() {
        replaceFragment(SettingsTopFragment(), FragmentTags.SETTINGS)
    }

    private fun replaceFragment(fragment: Fragment, tag: String) {
        if (supportFragmentManager.findFragmentByTag(tag) == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_area, fragment, tag)
                    .commit()
        }
    }

    fun pushFragment(fragment: Fragment, tag: String) {
//        if (supportFragmentManager.findFragmentByTag(tag) == null) {
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_area, fragment, tag)
                    .addToBackStack(null)
                    .commit()
//        }
    }

    fun setActionBarContent(view: View) {
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.customView= view
        supportActionBar?.setDisplayShowCustomEnabled(true)
    }

    fun showActionBar() {
        appBarLayout.setExpanded(true, true)
    }

//    https://stackoverflow.com/questions/6503189/fragments-onresume-from-back-stack
    // これでstation listの表示非表示を切り替え

}