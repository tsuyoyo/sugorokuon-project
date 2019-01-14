package tsuyogoro.sugorokuon.timetable

import android.graphics.Point
import android.graphics.Rect
import android.support.constraint.Group
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import tsuyogoro.sugorokuon.SugorokuonLog
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

        fun onRecommendModuleInstallStarted()

        fun onRecommendModuleInstallError()

        fun onRecommendModuleInstallCompleted()
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
        } else if (holder is RecommendModuleDependencyResolver.RecommendViewHolder) {
            holder.onBoundWithAdapter()
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (position) {
            0 -> TYPE_RECOMMEND
            else -> TYPE_TIMETABLE
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            TYPE_RECOMMEND -> {
                recommendModuleDependencyResolver.getRecommendProgramsViewHolder(parent, listener)
                    ?: DummyViewHolderNotInstalled(parent)
            }
            else -> TimeTableViewHolder(parent, listener)
        }

    override fun getItemCount(): Int = timeTables.size + 1

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder is RecommendModuleDependencyResolver.RecommendViewHolder) {
            holder.onUnboundFromAdapter()
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

    inner class DummyViewHolderNotInstalled(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_install_recommend, parent, false)
    ) {
        private val message: TextView
            get() = itemView.findViewById(R.id.message)

        private val installingViews: Group
            get() = itemView.findViewById(R.id.install_icons)

        private val installManager = SplitInstallManagerFactory.create(parent.context)

        init {
            val installStateListener = SplitInstallStateUpdatedListener { state ->
                state.moduleNames().forEach { name ->
                    when (state.status()) {
                        SplitInstallSessionStatus.DOWNLOADING -> {
                            SugorokuonLog.d("Recommend module - downloading")
//                            Toast.makeText(parent.context, "downloading", Toast.LENGTH_SHORT).show()
                        }
                        SplitInstallSessionStatus.INSTALLED -> {
                            SugorokuonLog.d("Recommend module - installed")
                            listener.onRecommendModuleInstallCompleted()
                        }
                        SplitInstallSessionStatus.INSTALLING -> {
                            SugorokuonLog.d("Recommend module - installing")
//                            Toast.makeText(parent.context, "installing", Toast.LENGTH_SHORT).show()
                        }
                        SplitInstallSessionStatus.FAILED -> {
                            SugorokuonLog.d("Recommend module - failed")
                            message.visibility = View.VISIBLE
                            installingViews.visibility = View.GONE
                            listener.onRecommendModuleInstallError()
//                            Toast.makeText(parent.context, "failed", Toast.LENGTH_SHORT).show()
                        }
                        SplitInstallSessionStatus.DOWNLOADED -> {
                            SugorokuonLog.d("Recommend module - downloaded")
//                            Toast.makeText(parent.context, "downloaded", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            SugorokuonLog.d("Recommend module - else ${state.status()}")
//                            Toast.makeText(parent.context, "else ${state.status()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            message.setOnClickListener {
                val request = SplitInstallRequest.newBuilder()
                    .addModule("recommend")
                    .build()

                installManager.registerListener(installStateListener)
                installManager
                    .startInstall(request)
                    .addOnSuccessListener {
                        // Memo : it's just "success to start install" (not install success)
                        message.visibility = View.GONE
                        installingViews.visibility = View.VISIBLE
                    }
                    .addOnFailureListener {
                        message.visibility = View.VISIBLE
                        installingViews.visibility = View.GONE
                        listener.onRecommendModuleInstallError()
                    }
            }
        }
    }
}