package tsuyogoro.sugorokuon.recommend.view

import android.support.constraint.Group
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import tsuyogoro.sugorokuon.base.R
import tsuyogoro.sugorokuon.timetable.ProgramTableAdapter
import tsuyogoro.sugorokuon.timetable.RecommendProgramData
import java.text.SimpleDateFormat
import java.util.*

class RecommendProgramsCarouselAdapter(
    private val listener: ProgramTableAdapter.ProgramTableAdapterListener
) : RecyclerView.Adapter<RecommendProgramsCarouselAdapter.RecommendProgramViewHolder>() {

    private var recommendPrograms: List<RecommendProgramData> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int) = RecommendProgramViewHolder(parent)

    override fun getItemCount(): Int = recommendPrograms.size

    override fun onBindViewHolder(holder: RecommendProgramViewHolder, position: Int) {
        holder.setData(recommendPrograms[position])
    }

    fun setRecommendPrograms(recommendPrograms: List<RecommendProgramData>) {
        recommendPrograms.sortedBy { it.program.start }
        this.recommendPrograms = recommendPrograms
    }

    inner class RecommendProgramViewHolder(parent: ViewGroup?) : RecyclerView.ViewHolder(
        LayoutInflater
            .from(parent!!.context)
            .inflate(R.layout.item_recommend_program, parent, false)
    ) {
        private val thumbnail: ImageView
            get() = itemView.findViewById(R.id.thumbnail)

        private val onAirDate: TextView
            get() = itemView.findViewById(R.id.on_air_date)

        private val onAirStart: TextView
            get() = itemView.findViewById(R.id.on_air_time_start)

        private val onAirEnd: TextView
            get() = itemView.findViewById(R.id.on_air_time_end)

        private val stationLogo: ImageView
            get() = itemView.findViewById(R.id.station_logo)

        private val title: TextView
            get() = itemView.findViewById(R.id.title)

        private val personality: TextView
            get() = itemView.findViewById(R.id.personalities)

        fun setData(recommendProgramData: RecommendProgramData) {
            val recommendProgram = recommendProgramData.program

            itemView.setOnClickListener { listener.onRecommendProgramClicked(recommendProgram) }
            itemView
                .findViewById<Group>(R.id.tap_area_group)
                .referencedIds
                .map { itemView.findViewById<View>(it) }
                .forEach {
                    it.setOnClickListener {
                        listener.onRecommendProgramClicked(recommendProgram)
                    }
                }

            Glide.with(thumbnail)
                .load(recommendProgram.image)
                .into(thumbnail)

            SimpleDateFormat(itemView.resources.getString(R.string.date_hhmm), Locale.JAPAN).let {
                onAirStart.text = it.format(Date(recommendProgram.start))
                onAirEnd.text = it.format(Date(recommendProgram.end))
            }
            SimpleDateFormat(itemView.resources.getString(R.string.onair_date), Locale.JAPAN).let {
                onAirDate.text = it.format(Date(recommendProgram.start))
            }
            Glide.with(stationLogo)
                .load(recommendProgramData.station.logo[0].url)
                .into(stationLogo)

            title.text = recommendProgram.title

            personality.text = recommendProgram.personality
        }
    }
}