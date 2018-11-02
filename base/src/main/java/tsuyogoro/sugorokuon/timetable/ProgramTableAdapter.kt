package tsuyogoro.sugorokuon.timetable

import android.graphics.Point
import android.graphics.Rect
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import tsuyogoro.sugorokuon.base.R
import tsuyogoro.sugorokuon.radiko.api.response.TimeTableResponse
import tsuyogoro.sugorokuon.recommend.RecommendProgram
import tsuyogoro.sugorokuon.recommend.keyword.RecommendKeyword
import tsuyogoro.sugorokuon.recommend.settings.RecommendSettingsRepository
import tsuyogoro.sugorokuon.station.Station

class ProgramTableAdapter(
    private val listener: ProgramTableAdapterListener,
    private val recommendSettingsRepository: RecommendSettingsRepository
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_RECOMMEND = 0
        private const val TYPE_TIMETABLE = 1
    }

    interface ProgramTableAdapterListener {
        fun onStationSiteClicked(station: Station)

        fun onProgramClicked(program: TimeTableResponse.Program, clickedPosition: Point)

        fun onRecommendProgramClicked(program: RecommendProgram)
    }

    private var timeTables: List<OneDayTimeTable> = emptyList()

    private var recommends: List<RecommendProgramData> = emptyList()

    fun setTimeTables(timeTables: List<OneDayTimeTable>) {
        this.timeTables = timeTables
    }

    fun setRecommend(recommends: List<RecommendProgramData>) {
        this.recommends = recommends
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TimeTableViewHolder) {
            holder.setStation(timeTables[position - 1])
        } else if (holder is RecommendViewHolder) {
            holder.setRecommendPrograms(
                recommends, recommendSettingsRepository.getRecommentKeywords())
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (position) {
            0 -> TYPE_RECOMMEND
            else -> TYPE_TIMETABLE
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            TYPE_RECOMMEND -> RecommendViewHolder(parent)
            else -> TimeTableViewHolder(parent, listener)
        }

    override fun getItemCount(): Int = timeTables.size + 1

    inner class RecommendViewHolder(
        parent: ViewGroup
    ) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recommend_carousel, parent, false)
    ) {
        private val recommendTitle: TextView
            get() = itemView.findViewById(R.id.recommend_title)

        private val noRecommend: TextView
            get() = itemView.findViewById(R.id.no_recommend)

        private val recommendPrograms: RecyclerView
            get() = itemView.findViewById(R.id.recommend_programs)

        private val gotoKeywordSettings: Button
            get() = itemView.findViewById(R.id.goto_keyword_settings_button)

        private val carouselAdapter: RecommendProgramsCarouselAdapter =
            RecommendProgramsCarouselAdapter(listener)

        init {
            recommendPrograms.apply {
                adapter = carouselAdapter
                layoutManager = LinearLayoutManager(
                    itemView.context, LinearLayoutManager.HORIZONTAL, false)
            }
            recommendPrograms.addItemDecoration(ProgramListItemDecoration())
        }

        fun setRecommendPrograms(
            recommends: List<RecommendProgramData>,
            keywords: List<RecommendKeyword>
        ) {
            noRecommend.visibility = if (keywords.isNotEmpty() && recommends.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
            recommendPrograms.visibility = if (keywords.isNotEmpty() && recommends.isNotEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
            gotoKeywordSettings.visibility = if (keywords.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
            recommendTitle.visibility = if (keywords.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
            carouselAdapter.setRecommendPrograms(recommends)
            carouselAdapter.notifyDataSetChanged()
        }
    }

    class TimeTableViewHolder(
        parent: ViewGroup?,
        private val listener: ProgramTableAdapterListener) : RecyclerView.ViewHolder(

        LayoutInflater
            .from(parent!!.context)
            .inflate(R.layout.item_program_one_station, parent, false)
    ) {
        private val stationLogo: ImageView
            get() = itemView.findViewById(R.id.station_logo)

        private val stationName: TextView
            get() = itemView.findViewById(R.id.station_name)

        private val stationSiteButton: TextView
            get() = itemView.findViewById(R.id.station_site_btn)

        private val programList: RecyclerView
            get() = itemView.findViewById(R.id.program_list)

        private lateinit var layoutManager: LinearLayoutManager

        init {
            programList.addItemDecoration(ProgramListItemDecoration())
        }

        fun setStation(timeTable: OneDayTimeTable) {
            if (timeTable.station.logo.isNotEmpty()) {
                Glide.with(stationLogo)
                    .load(timeTable.station.logo[0].url)
                    .into(stationLogo)
            }

            stationName.text = timeTable.station.name

            val programsAdapter = TimeTableAdapter(listener).apply {
                setPrograms(timeTable.programs)
            }
            layoutManager = LinearLayoutManager(
                itemView.context, LinearLayoutManager.HORIZONTAL, false)
            programList.apply {
                adapter = programsAdapter
                layoutManager = this@TimeTableViewHolder.layoutManager
            }
            programsAdapter.notifyDataSetChanged()

            // Focus the position of now.
            programList.scrollToPosition(programsAdapter.getCurrentTimePosition())

            programList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                }
            })

            stationSiteButton.setOnClickListener {
                listener.onStationSiteClicked(timeTable.station)
            }
        }
    }

    class ProgramListItemDecoration : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect?,
                                    view: View?,
                                    parent: RecyclerView?,
                                    state: RecyclerView.State?) {
            super.getItemOffsets(outRect, view, parent, state)
            if (view != null) {
                val resources = view.context.resources
                val density = resources.displayMetrics.density

                outRect?.left = (density * 4).toInt()
                outRect?.right = (density * 4).toInt()
            }
        }
    }
}