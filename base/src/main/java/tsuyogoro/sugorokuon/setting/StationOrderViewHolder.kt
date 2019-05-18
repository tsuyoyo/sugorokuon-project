package tsuyogoro.sugorokuon.setting

import androidx.annotation.IntRange
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder
import tsuyogoro.sugorokuon.base.R
import tsuyogoro.sugorokuon.station.Station

class StationOrderViewHolder(parent: ViewGroup) : AbstractDraggableItemViewHolder(
        LayoutInflater.from(parent.context)
                .inflate(R.layout.item_station_order_settings, parent, false)
) {
    private val stationName: TextView
        get() = itemView.findViewById(R.id.station_name)

    private val orderLabel: TextView
        get() = itemView.findViewById(R.id.order)

    private val logo: ImageView
        get() = itemView.findViewById(R.id.station_logo)

    fun setStation(station: Station,
                   @IntRange(from = 1) order: Int) {
        stationName.text = station.name
        orderLabel.text = order.toString()
        Glide.with(itemView).load(station.logo[0].url).into(logo)
    }
}