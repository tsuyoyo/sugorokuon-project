package tsuyogoro.sugorokuon.setting

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import tsuyogoro.sugorokuon.SugorokuonApplication
import tsuyogoro.sugorokuon.base.R
import tsuyogoro.sugorokuon.constant.Area
import javax.inject.Inject

class AreaSettingsFragment : androidx.fragment.app.Fragment(), AreaSettingsListAdapter.OnAreaSelectedListener {

    private val areaList: androidx.recyclerview.widget.RecyclerView
        get() = view!!.findViewById(R.id.area_list)

    private val selectedAreasLabel: TextView
        get() = view!!.findViewById(R.id.selected_areas)

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

        viewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(AreaSettingsViewModel::class.java)

        viewModel.observeAllAreas()
                .observe(this, Observer {
                    if (it != null) {
                        areaListAdapter = AreaSettingsListAdapter(it, this@AreaSettingsFragment)
                        areaList.apply {
                            adapter = areaListAdapter
                            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                                context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
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