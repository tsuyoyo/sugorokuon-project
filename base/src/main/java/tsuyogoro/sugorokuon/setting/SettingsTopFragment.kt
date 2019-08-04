package tsuyogoro.sugorokuon.setting

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.transition.Slide
import androidx.fragment.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.common.wrappers.InstantApps
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import tsuyogoro.sugorokuon.SugorokuonApplication
import tsuyogoro.sugorokuon.SugorokuonTopActivity
import tsuyogoro.sugorokuon.base.BuildConfig
import tsuyogoro.sugorokuon.base.R
import tsuyogoro.sugorokuon.recommend.debug.RecommendDebugActivity
import tsuyogoro.sugorokuon.recommend.keyword.RecommendKeywordFragment
import tsuyogoro.sugorokuon.recommend.reminder.ReminderSettingsFragment
import tsuyogoro.sugorokuon.utils.SugorokuonUtils
import javax.inject.Inject

class SettingsTopFragment : androidx.fragment.app.Fragment() {

    object SubFragmentTags {
        val AREA_SETTINGS = "area"
        val STATION_ORDER_SETTINGS = "stationOrder"
        val SONG_SEARCH_METHOD_SETTINGS = "songSearchMethod"
        val KEYWORD_SETTINGS = "keyword"
        val REMINDER_SETTINGS = "reminder"
    }

    @Inject
    lateinit var viewModelFactory: SettingsTopViewModel.Factory

    private val selectedAreas: TextView
        get() = view!!.findViewById(R.id.selected_areas)

    private val areaSettings: View
        get() = view!!.findViewById(R.id.area_settings)

    private val keywordSettings: View
        get() = view!!.findViewById(R.id.keyword_settings)

    private val reminderSettings: View
        get() = view!!.findViewById(R.id.reminder_settings)

    private val selectedSearchSongWay: TextView
        get() = view!!.findViewById(R.id.selected_way_to_search_song)

    private val stationOrder: View
        get() = view!!.findViewById(R.id.station_order_settings)

    private val appVersion: TextView
        get() = view!!.findViewById(R.id.app_version_text)

    private val privacyPolicy: View
        get() = view!!.findViewById(R.id.privacy_policy)

    private val debugRecommendFeature: View
        get() = view!!.findViewById(R.id.recommend_debug)

    private lateinit var viewModel: SettingsTopViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_settings_top, null)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        SugorokuonApplication.application(context)
            .appComponent()
            .settingSubComponent(SettingsModule())
            .inject(this)

        view.findViewById<LinearLayout>(R.id.license)
            .setOnClickListener { onLicenseClicked() }

        view.findViewById<LinearLayout>(R.id.way_to_search_song)
            .apply {
                if (InstantApps.isInstantApp(context)) {
                    visibility = View.GONE
                } else {
                    setOnClickListener { onWaySearchSongClicked() }
                }
            }

        privacyPolicy.setOnClickListener {
            SugorokuonUtils.launchChromeTab(activity, Uri.parse(
                "https://sugorokuonapp.firebaseapp.com/policy/privacy_policy.html"))
        }

        viewModel = ViewModelProviders
            .of(this, viewModelFactory)
            .get(SettingsTopViewModel::class.java)

        viewModel.observeSelectedAreas()
            .observe(this, Observer(selectedAreas::setText))

        viewModel.observeSelectedSerachSongWay()
            .observe(this, Observer {
                if (it != null) {
                    selectedSearchSongWay.text = it.getDisplayName(resources)
                }
            })

        areaSettings.setOnClickListener {
            (activity as? SugorokuonTopActivity)?.switchFragment(
                AreaSettingsFragment(),
                SubFragmentTags.AREA_SETTINGS,
                Slide(Gravity.START)
            )
        }

        stationOrder.setOnClickListener {
            (activity as? SugorokuonTopActivity)?.switchFragment(
                StationOrderFragment(),
                SubFragmentTags.STATION_ORDER_SETTINGS,
                Slide(Gravity.START)
            )
        }

        keywordSettings.setOnClickListener {
            (activity as? SugorokuonTopActivity)?.switchFragment(
                RecommendKeywordFragment.createInstance(),
                SubFragmentTags.KEYWORD_SETTINGS,
                Slide(Gravity.START)
            )
        }

        reminderSettings.setOnClickListener {
            (activity as? SugorokuonTopActivity)?.switchFragment(
                ReminderSettingsFragment.createInstance(),
                SubFragmentTags.REMINDER_SETTINGS,
                Slide(Gravity.START)
            )
        }

        context?.let {
            it.packageManager
                ?.getPackageInfo(it.packageName, PackageManager.GET_META_DATA)
                ?.let { packageInfo ->
                    appVersion.text =
                        "${getString(R.string.app_version)} ${packageInfo.versionName}"
                }
        }

        if (BuildConfig.DEBUG) {
            setupDebugMenu()
        }
    }

    private fun onLicenseClicked() {
        val intent = Intent(context, OssLicensesMenuActivity::class.java)
        intent.putExtra("title", getString(R.string.license))
        startActivity(intent)
    }

    private fun onWaySearchSongClicked() {
        (activity as? SugorokuonTopActivity)?.switchFragment(
            SearchSongMethodFragment(),
            SubFragmentTags.SONG_SEARCH_METHOD_SETTINGS,
            Slide(Gravity.START)
        )
    }

    private fun setupDebugMenu() {
        debugRecommendFeature.visibility = View.VISIBLE
        debugRecommendFeature.setOnClickListener {
            startActivity(Intent(activity, RecommendDebugActivity::class.java))
        }
    }

}