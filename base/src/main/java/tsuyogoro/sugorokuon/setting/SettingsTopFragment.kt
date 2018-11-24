package tsuyogoro.sugorokuon.setting

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.transition.Slide
import android.support.v4.app.Fragment
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
import tsuyogoro.sugorokuon.dynamicfeature.RecommendModuleDependencyResolver
import tsuyogoro.sugorokuon.utils.SugorokuonUtils
import javax.inject.Inject

class SettingsTopFragment : Fragment() {

    object SubFragmentTags {
        val AREA_SETTINGS = "area"
        val STATION_ORDER_SETTINGS = "stationOrder"
        val SONG_SEARCH_METHOD_SETTINGS = "songSearchMethod"
        val KEYWORD_SETTINGS = "keyword"
        val REMINDER_SETTINGS = "reminder"
    }

    @Inject
    lateinit var viewModelFactory: SettingsTopViewModel.Factory

    private val recommendModuleDependencyResolver = RecommendModuleDependencyResolver()

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

        // Switch visibility according to module installation state.
        val recommendKeywordFragment = recommendModuleDependencyResolver
            .getRecommendKeywordSettingsFragent()
        if (recommendKeywordFragment != null) {
            keywordSettings.visibility = View.VISIBLE
            keywordSettings.setOnClickListener {
                (activity as? SugorokuonTopActivity)?.switchFragment(
                    recommendKeywordFragment,
                    SubFragmentTags.KEYWORD_SETTINGS,
                    Slide(Gravity.START)
                )
            }
        } else {
            keywordSettings.visibility = View.GONE
        }

        // Switch visibility according to module installation state.
        val reminderSettingFragment = recommendModuleDependencyResolver
            .getRecommendReminderSettingsFragent()
        if (reminderSettingFragment != null) {
            reminderSettings.visibility = View.VISIBLE
            reminderSettings.setOnClickListener {
                (activity as? SugorokuonTopActivity)?.switchFragment(
                    reminderSettingFragment,
                    SubFragmentTags.REMINDER_SETTINGS,
                    Slide(Gravity.START)
                )
            }
        } else {
            reminderSettings.visibility = View.GONE
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
        val recommendDebugIntent = context?.let {
            recommendModuleDependencyResolver.getRecommendDebugActivityIntent(it)
        }
        if (recommendDebugIntent != null) {
            debugRecommendFeature.visibility = View.VISIBLE
            debugRecommendFeature.setOnClickListener {
                startActivity(recommendDebugIntent)
            }
        } else {
            debugRecommendFeature.visibility = View.GONE
        }
    }

}