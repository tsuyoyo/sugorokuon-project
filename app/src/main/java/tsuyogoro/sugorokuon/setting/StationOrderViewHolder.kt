package tsuyogoro.sugorokuon.setting

import android.support.annotation.IntRange
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder
import tsuyogoro.sugorokuon.R
import tsuyogoro.sugorokuon.api.response.StationResponse

class StationOrderViewHolder(parent: ViewGroup) : AbstractDraggableItemViewHolder(
        LayoutInflater.from(parent.context)
                .inflate(R.layout.item_station_order_settings, parent, false)
) {
    @BindView(R.id.station_name)
    lateinit var stationName: TextView

    @BindView(R.id.order)
    lateinit var orderLabel: TextView

    @BindView(R.id.station_logo)
    lateinit var logo: ImageView

    init {
        ButterKnife.bind(this, itemView)
    }

    fun setStation(station: StationResponse.Station,
                   @IntRange(from = 1) order: Int) {
        stationName.text = station.name
        orderLabel.text = order.toString()
        Glide.with(itemView).load(station.logos[0].url).into(logo)
    }
}