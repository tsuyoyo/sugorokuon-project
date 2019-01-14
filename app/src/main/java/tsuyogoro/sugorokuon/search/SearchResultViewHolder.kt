package tsuyogoro.sugorokuon.search

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import tsuyogoro.sugorokuon.base.R
import tsuyogoro.sugorokuon.radiko.api.response.SearchResponse
import tsuyogoro.sugorokuon.radiko.api.response.StationResponse
import tsuyogoro.sugorokuon.station.Station
import java.text.SimpleDateFormat
import java.util.*

class SearchResultViewHolder(
        parent: ViewGroup?,
        private val listener: SearchResultListAdapter.SearchResultListAdapterListener)
    : RecyclerView.ViewHolder(
        LayoutInflater.from(parent!!.context)
                .inflate(R.layout.item_search_result, parent, false)
) {
    private val clickArea: View 
        get() = itemView.findViewById(R.id.click_area)

    private val image: ImageView
        get() = itemView.findViewById(R.id.image)

    private val date: TextView
    get() = itemView.findViewById(R.id.date)

    private val startTime: TextView
        get() = itemView.findViewById(R.id.start_time)

    private val endTime: TextView
        get() = itemView.findViewById(R.id.end_time)

    private val title: TextView
        get() = itemView.findViewById(R.id.title)

    private val personalities: TextView
        get() = itemView.findViewById(R.id.personalities)

    private val stationName: TextView
        get() = itemView.findViewById(R.id.station_name)

    private val stationLogo: ImageView
        get() = itemView.findViewById(R.id.station_logo)

    fun setSearchResult(searchResult: SearchViewModel.SearchResultData) {
        clickArea.setOnClickListener {
            listener.onSearchResultClicked(searchResult)
        }
        setProgramArea(searchResult.program)
        setStationArea(searchResult.station)
    }

    private fun setProgramArea(program: SearchResponse.Program) {
        val resources = itemView.resources

        Glide.with(image)
                .load(program.image)
                .apply(RequestOptions.circleCropTransform())
                .into(image)

        SimpleDateFormat(resources.getString(R.string.onair_date), Locale.JAPAN).let {
            date.text = it.format(program.start)
        }
        SimpleDateFormat(resources.getString(R.string.date_hhmm), Locale.JAPAN).let {
            startTime.text = it.format(program.start)
            endTime.text = it.format(program.end)
        }
        title.text = program.title
        personalities.text = program.personality
    }

    private fun setStationArea(station: Station) {
        stationName.text = station.name
        Glide.with(stationLogo).load(station.logo[0].url).into(stationLogo)
    }
}