package tsuyogoro.sugorokuon.v3.timetable

import android.graphics.Rect
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import tsuyogoro.sugorokuon.R
import tsuyogoro.sugorokuon.v3.api.response.StationResponse
import tsuyogoro.sugorokuon.v3.api.response.TimeTableResponse

class ProgramTableAdapter(
        private val listener: ProgramTableAdapterListener
) : RecyclerView.Adapter<ProgramTableAdapter.ViewHolder>() {

    interface ProgramTableAdapterListener {
        fun onStationSiteClicked(station: StationResponse.Station)

        fun onProgramClicked(program: TimeTableResponse.Program)
    }

    private var timeTables : List<OneDayTimeTable> = emptyList()

    fun setTimeTables(timeTables: List<OneDayTimeTable>) {
        this.timeTables = timeTables
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.setStation(timeTables[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder =
            ViewHolder(parent, listener)

    override fun getItemCount(): Int = timeTables.size

    class ViewHolder(parent: ViewGroup?,
                     private val listener: ProgramTableAdapterListener
    ) : RecyclerView.ViewHolder(
            LayoutInflater.from(parent!!.context)
                    .inflate(R.layout.item_program_one_station, parent, false)
    ) {

        @BindView(R.id.station_logo)
        lateinit var stationLogo: ImageView

        @BindView(R.id.station_name)
        lateinit var stationName: TextView

        @BindView(R.id.station_site_btn)
        lateinit var stationSiteButton: TextView

        @BindView(R.id.program_list)
        lateinit var programList: RecyclerView

        init {
            ButterKnife.bind(this, itemView)
            programList.addItemDecoration(ProgramListItemDecoration())
        }

        fun setStation(timeTable: OneDayTimeTable) {
            if (timeTable.station.logos.isNotEmpty()) {
                Glide.with(stationLogo)
                        .load(timeTable.station.logos[0].url)
                        .into(stationLogo)
            }

            stationName.text = timeTable.station.name

            val programsAdapter = TimeTableAdapter(listener).apply {
                setPrograms(timeTable.programs)
            }
            programList.apply {
                adapter = programsAdapter
                layoutManager = LinearLayoutManager(
                        itemView.context, LinearLayoutManager.HORIZONTAL, false)
            }
            programsAdapter.notifyDataSetChanged()

            // Focus the position of now.
            programList.scrollToPosition(programsAdapter.getCurrentTimePosition())

            stationSiteButton.setOnClickListener {
                listener.onStationSiteClicked(timeTable.station)
            }
        }
    }

    class ProgramListItemDecoration: RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect?,
                                    view: View?,
                                    parent: RecyclerView?,
                                    state: RecyclerView.State?) {
            super.getItemOffsets(outRect, view, parent, state)
            if (view != null) {
                outRect?.left = (view.context.resources.displayMetrics.density * 4).toInt()
                outRect?.right = (view.context.resources.displayMetrics.density * 4).toInt()
            }
        }
    }
}