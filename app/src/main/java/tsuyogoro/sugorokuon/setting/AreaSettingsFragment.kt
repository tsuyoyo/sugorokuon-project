package tsuyogoro.sugorokuon.setting

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import tsuyogoro.sugorokuon.R
import tsuyogoro.sugorokuon.SugorokuonApplication
import tsuyogoro.sugorokuon.constant.Area
import javax.inject.Inject

class AreaSettingsFragment : Fragment(), AreaSettingsListAdapter.OnAreaSelectedListener {

    @BindView(R.id.area_list)
    lateinit var areaList: RecyclerView

    @BindView(R.id.selected_areas)
    lateinit var selectedAreasLabel: TextView

    @Inject
    lateinit var viewModelFactory: AreaSettingsViewModel.Factory

    private lateinit var viewModel: AreaSettingsViewModel
    private lateinit var areaListAdapter: AreaSettingsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SugorokuonApplication.application(context)
                .appComponent()
                .settingSubComponent(SettingsModule())
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.fragment_settings_area, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)

        viewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(AreaSettingsViewModel::class.java)

        viewModel.observeAllAreas()
                .observe(this, Observer {
                    if (it != null) {
                        areaListAdapter = AreaSettingsListAdapter(it, this@AreaSettingsFragment)
                        areaList.apply {
                            adapter = areaListAdapter
                            layoutManager = LinearLayoutManager(
                                    context, LinearLayoutManager.VERTICAL, false)
                        }
                        areaListAdapter.notifyDataSetChanged()
                    }
                })

        viewModel.observeSelectedAreas()
                .observe(this, Observer {
                    if (it != null) {
                        areaListAdapter.setSelectedAreas(it)
                        areaListAdapter.notifyDataSetChanged()
                    }
                })

        viewModel.observeSelectedAreasLabel()
                .observe(this, Observer {
                    if (it != null) {
                        selectedAreasLabel.text = it
                    }
                })
    }

    override fun onAreaSelected(area: Area) {
        viewModel.selectArea(area)
    }

    override fun onAreaDeselected(area: Area) {
        viewModel.deselectArea(area)
    }
}