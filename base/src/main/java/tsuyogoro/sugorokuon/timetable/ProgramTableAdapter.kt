package tsuyogoro.sugorokuon.timetable

import android.graphics.Point
import android.graphics.Rect
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import tsuyogoro.sugorokuon.base.R
import tsuyogoro.sugorokuon.dynamicfeature.RecommendModuleDependencyResolver
import tsuyogoro.sugorokuon.radiko.api.response.TimeTableResponse
import tsuyogoro.sugorokuon.recommend.RecommendProgram
import tsuyogoro.sugorokuon.station.Station

class ProgramTableAdapter(
    private val listener: ProgramTableAdapterListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_RECOMMEND = 0
        private const val TYPE_TIMETABLE = 1
    }

    interface ProgramTableAdapterListener {
        fun onStationSiteClicked(station: Station)

        fun onProgramClicked(program: TimeTableResponse.Program, clickedPosition: Point)

        fun onRecommendProgramClicked(program: RecommendProgram)

        fun onGotoKeywordSettingsClicked()
    }

    private var timeTables: List<OneDayTimeTable> = emptyList()

    private var recommends: List<RecommendProgramData> = emptyList()

    private val recommendModuleDependencyResolver = RecommendModuleDependencyResolver()

    fun setTimeTables(timeTables: List<OneDayTimeTable>) {
        this.timeTables = timeTables
    }

    fun setRecommend(recommends: List<RecommendProgramData>) {
        this.recommends = recommends
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TimeTableViewHolder) {
            holder.setStation(timeTables[position - 1])
        }
        // TODO : 別に無くてもいいかも(viewの中で全て閉じる)
//        else if (holder is RecommendViewHolder) {
//            holder.setRecommendPrograms(
//                recommends, recommendSettingsRepository.getRecommentKeywords())
//        }
    }

    override fun getItemViewType(position: Int): Int =
        when (position) {
            0 -> TYPE_RECOMMEND
            else -> TYPE_TIMETABLE
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
//        TimeTableViewHolder(parent, listener)
        when (viewType) {
            TYPE_RECOMMEND -> {
                SplitInstallManagerFactory.create(parent.context).let {
                    val isRecommendInstalled = it.installedModules.contains("recommend")
                    if (isRecommendInstalled) {
                        DummyViewHolderAlreadyInstalled(parent)
                    } else {
                        DummyViewHolderNotInstalled(parent)
                    }
                }
             // RecommendViewHolder(parent)
             // recommendモジュールがあるかどうかで表示するviewを変える
        }
            else -> TimeTableViewHolder(parent, listener)
        }

    override fun getItemCount(): Int = timeTables.size + 1

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

    class DummyViewHolderNotInstalled(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_install_recommend, parent, false)
    ) {

    }

    class DummyViewHolderAlreadyInstalled(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_install_recommend_dummy, parent, false)
    ) {

    }
}