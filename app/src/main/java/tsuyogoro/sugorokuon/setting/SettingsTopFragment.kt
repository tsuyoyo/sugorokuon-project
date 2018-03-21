package tsuyogoro.sugorokuon.setting

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.transition.Slide
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import tsuyogoro.sugorokuon.R
import tsuyogoro.sugorokuon.SugorokuonApplication
import tsuyogoro.sugorokuon.SugorokuonTopActivity
import javax.inject.Inject
import android.content.pm.PackageManager
import android.content.pm.PackageInfo

class SettingsTopFragment : Fragment() {

    object SubFragmentTags {
        val AREA_SETTINGS = "area"
        val STATION_ORDER_SETTINGS = "stationOrder"
    }

    @Inject
    lateinit var viewModelFactory: SettingsTopViewModel.Factory

    @BindView(R.id.selected_areas)
    lateinit var selectedAreas: TextView

    @BindView(R.id.area_settings)
    lateinit var areaSettings: View

    @BindView(R.id.station_order_settings)
    lateinit var stationOrder: View

    @BindView(R.id.app_version_text)
    lateinit var appVersion: TextView

    private lateinit var viewModel: SettingsTopViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_settings_top, null)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)

        SugorokuonApplication.application(context)
                .appComponent()
                .settingSubComponent(SettingsModule())
                .inject(this)

        viewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(SettingsTopViewModel::class.java)

        viewModel.observeSelectedAreas()
                .observe(this, Observer(selectedAreas::setText))

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

        context?.let {
            it.packageManager
                    ?.getPackageInfo(it.packageName, PackageManager.GET_META_DATA)
                    ?.let { packageInfo ->
                        appVersion.text =
                                "${getString(R.string.app_version)} ${packageInfo.versionName}"
                    }
        }
    }

    @OnClick(R.id.license)
    fun onLicenseClicked() {
        val intent = Intent(context, OssLicensesMenuActivity::class.java)
        intent.putExtra("title", getString(R.string.license))
        startActivity(intent)
    }

}