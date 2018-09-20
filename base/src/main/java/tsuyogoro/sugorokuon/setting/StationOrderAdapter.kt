package tsuyogoro.sugorokuon.setting

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange
import tsuyogoro.sugorokuon.radiko.api.response.StationResponse

class StationOrderAdapter(private val listener: StationOrderAdapterListener) :
        RecyclerView.Adapter<StationOrderViewHolder>(),
        DraggableItemAdapter<StationOrderViewHolder> {

    interface StationOrderAdapterListener {
        fun onStationOrderChanged(stations: List<StationResponse.Station>)
    }

    private var stations: MutableList<StationResponse.Station> = mutableListOf()

    init {
        setHasStableIds(true)
    }

    // Note :
    // need to return stable (= not change even after reordered) value
    // otherwise, unnatural animation is caused at swapping items.
    override fun getItemId(position: Int): Long = stations[position].id.hashCode().toLong()

    fun setStations(stations: List<StationResponse.Station>) {
        this.stations.clear()
        this.stations.addAll(stations)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationOrderViewHolder =
            StationOrderViewHolder(parent)

    override fun getItemCount(): Int = stations.size

    override fun onBindViewHolder(holder: StationOrderViewHolder, position: Int) =
            holder.setStation(stations[position], position + 1)

    override fun onGetItemDraggableRange(
            holder: StationOrderViewHolder?, position: Int): ItemDraggableRange? = null

    override fun onCheckCanStartDrag(
            holder: StationOrderViewHolder?, position: Int, x: Int, y: Int): Boolean = true

    override fun onItemDragStarted(position: Int) {
    }

    override fun onMoveItem(fromPosition: Int, toPosition: Int) {
        val movedElement = stations.removeAt(fromPosition)
        stations.add(toPosition, movedElement)
        listener.onStationOrderChanged(stations)
    }

    override fun onCheckCanDrop(draggingPosition: Int, dropPosition: Int): Boolean = true

    override fun onItemDragFinished(fromPosition: Int, toPosition: Int, result: Boolean) {
        notifyDataSetChanged()
    }
}