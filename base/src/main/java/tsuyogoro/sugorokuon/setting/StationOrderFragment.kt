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
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils
import tsuyogoro.sugorokuon.SugorokuonApplication
import tsuyogoro.sugorokuon.base.R
import tsuyogoro.sugorokuon.radiko.api.response.StationResponse
import tsuyogoro.sugorokuon.station.Station
import javax.inject.Inject

class StationOrderFragment : androidx.fragment.app.Fragment(), StationOrderAdapter.StationOrderAdapterListener {

    lateinit var viewModel: StationOrderViewModel

    lateinit var adapter: StationOrderAdapter

    private var wrappedAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>? = null

    private var recyclerViewDragDropManager: RecyclerViewDragDropManager? = null

    @Inject
    lateinit var viewModelFactory: StationOrderViewModel.Factory

    private val stationList: androidx.recyclerview.widget.RecyclerView
        get() = view!!.findViewById(R.id.stations_list)

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_settings_station_order, null)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        SugorokuonApplication.application(context)
                .appComponent()
                .settingSubComponent(SettingsModule())
                .inject(this)

        setupDraggableStationList()

        viewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(StationOrderViewModel::class.java)

        viewModel.observeOrderedStations()
                .observe(this, Observer {
                    it?.let {
                        adapter.setStations(it)
                        adapter.notifyDataSetChanged()
                    }
                })
    }

    private fun setupDraggableStationList() {
        recyclerViewDragDropManager = RecyclerViewDragDropManager().apply {
            setInitiateOnMove(false)
            setInitiateOnLongPress(true)
        }

        adapter = StationOrderAdapter(this)
        wrappedAdapter = recyclerViewDragDropManager?.createWrappedAdapter(adapter)

        stationList.apply {
            adapter = wrappedAdapter
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
//            itemAnimator = DraggableItemAnimator()
        }

        recyclerViewDragDropManager?.attachRecyclerView(stationList)
    }

    override fun onPause() {
        recyclerViewDragDropManager?.cancelDrag()
        super.onPause()
    }

    override fun onDestroyView() {
        recyclerViewDragDropManager?.let { it.release() }
        recyclerViewDragDropManager = null

        wrappedAdapter?.let { WrapperAdapterUtils.releaseAll(it) }
        wrappedAdapter = null

        super.onDestroyView()
    }

    override fun onStationOrderChanged(stations: List<Station>) {
        viewModel.updateStationOrder(stations)
    }
}