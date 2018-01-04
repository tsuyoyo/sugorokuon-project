package tsuyogoro.sugorokuon.v3

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import tsuyogoro.sugorokuon.R
import tsuyogoro.sugorokuon.v3.setting.SettingsTopFragment
import tsuyogoro.sugorokuon.v3.songs.OnAirSongsFragment
import tsuyogoro.sugorokuon.v3.songs.OnAirSongsRootFragment
import tsuyogoro.sugorokuon.v3.timetable.ProgramTableFragment

class SugorokuonActivity : AppCompatActivity() {

    object FragmentTags {
        val PROGRAM_TABLE = "program_table"
        val ON_AIR_SONGS = "on_air_songs"
        val SETTINGS = "settings"
    }

    @BindView(R.id.toolbar)
    lateinit var toolBar: Toolbar

    @BindView(R.id.bottom_navigation)
    lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top)
        ButterKnife.bind(this)

        setupActionBar()
        setupBottomNavigation()

        if (savedInstanceState == null) {
            bottomNavigationView.selectedItemId = R.id.program_table
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolBar)
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
}