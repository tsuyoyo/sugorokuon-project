package tsuyogoro.sugorokuon.v3.setting

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import tsuyogoro.sugorokuon.R
import tsuyogoro.sugorokuon.SugorokuonApplication
import tsuyogoro.sugorokuon.v3.SugorokuonTopActivity
import javax.inject.Inject

class SettingsTopFragment : Fragment() {

    object SubFragmentTags {
        val AREA_SETTINGS = "area"
    }

    @Inject
    lateinit var viewModelFactory: SettingsTopViewModel.Factory

    @BindView(R.id.selected_areas)
    lateinit var selectedAreas: TextView

    @BindView(R.id.area_settings)
    lateinit var areaSettings: View

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
            (activity as? SugorokuonTopActivity)
                    ?.pushFragment(AreaSettingsFragment(), SubFragmentTags.AREA_SETTINGS)
        }
    }

}