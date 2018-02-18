package tsuyogoro.sugorokuon.v3.search

import android.graphics.Point
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import tsuyogoro.sugorokuon.R
import tsuyogoro.sugorokuon.v3.api.response.SearchResponse
import tsuyogoro.sugorokuon.v3.api.response.StationResponse
import java.text.SimpleDateFormat
import java.util.*

class SearchResultViewHolder(
        parent: ViewGroup?,
        private val listener: SearchResultListAdapter.SearchResultListAdapterListener)
    : RecyclerView.ViewHolder(
        LayoutInflater.from(parent!!.context)
                .inflate(R.layout.item_search_result, parent, false)
) {
    @BindView(R.id.click_area)
    lateinit var clickArea: View

    @BindView(R.id.image)
    lateinit var image: ImageView

    @BindView(R.id.date)
    lateinit var date: TextView

    @BindView(R.id.start_time)
    lateinit var startTime: TextView

    @BindView(R.id.end_time)
    lateinit var endTime: TextView

    @BindView(R.id.title)
    lateinit var title: TextView

    @BindView(R.id.personalities)
    lateinit var personalities: TextView

    @BindView(R.id.station_name)
    lateinit var stationName: TextView

    @BindView(R.id.station_logo)
    lateinit var stationLogo: ImageView

    init {
        ButterKnife.bind(this, itemView)
    }

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

    private fun setStationArea(station: StationResponse.Station) {
        stationName.text = station.name
        Glide.with(stationLogo).load(station.logos[0].url).into(stationLogo)
    }
}