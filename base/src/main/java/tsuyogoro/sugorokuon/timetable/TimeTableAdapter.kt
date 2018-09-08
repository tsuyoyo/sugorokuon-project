package tsuyogoro.sugorokuon.timetable

import android.graphics.Point
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import tsuyogoro.sugorokuon.base.R
import tsuyogoro.sugorokuon.api.response.TimeTableResponse
import java.text.SimpleDateFormat
import java.util.*

class TimeTableAdapter(
        private val listener: ProgramTableAdapter.ProgramTableAdapterListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var programs: List<TimeTableResponse.Program>

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ProgramViewHolder)?.setProgram(programs[position])
    }

    override fun getItemCount(): Int = programs.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            ProgramViewHolder(parent, listener)

    fun setPrograms(programs: List<TimeTableResponse.Program>) {
        this.programs = programs
    }

    fun getCurrentTimePosition() : Int{
        val now = Calendar.getInstance()
        programs.forEachIndexed { index, p ->
            if (now.timeInMillis in p.start.timeInMillis..p.end.timeInMillis) {
                return index
            }
        }
        return 0
    }

    class ProgramViewHolder(parent: ViewGroup?,
                            private val listener: ProgramTableAdapter.ProgramTableAdapterListener
    ): RecyclerView.ViewHolder(
            LayoutInflater.from(parent?.context)
                    .inflate(R.layout.item_program, parent, false)
    ) {
        private val thumbnail: ImageView
            get() = itemView.findViewById(R.id.thumbnail)

        private val onAirTimeStart: TextView
            get() = itemView.findViewById(R.id.on_air_time_start)

        private val onAirTimeEnd: TextView
            get() = itemView.findViewById(R.id.on_air_time_end)

        private val title: TextView
            get() = itemView.findViewById(R.id.title)

        private val personalities: TextView
            get() = itemView.findViewById(R.id.personalities)

        private val tapArea: View
            get() = itemView.findViewById(R.id.tap_area)

        fun setProgram(program: TimeTableResponse.Program) {
            tapArea.setOnClickListener {
                val tappedPosition = IntArray(2).apply {
                    itemView.getLocationOnScreen(this)
                }
                listener.onProgramClicked(program,
                        Point(
                                tappedPosition[0] + itemView.width / 2,
                                tappedPosition[1]
                        )
                )
            }

            Glide.with(thumbnail)
                    .load(program.image)
                    .into(thumbnail)

            title.text = program.title
            personalities.text = program.perfonality

            SimpleDateFormat(itemView.resources.getString(R.string.date_hhmm), Locale.JAPAN).let {
                onAirTimeStart.text = it.format(Date(program.start.timeInMillis))
                onAirTimeEnd.text = it.format(Date(program.end.timeInMillis))
            }
        }

    }
}