package tsuyogoro.sugorokuon.timetable

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import tsuyogoro.sugorokuon.base.R
import java.text.SimpleDateFormat
import java.util.*

class DateSelectorAdapter(
        private val listener: DateSelectorListener
) : androidx.recyclerview.widget.RecyclerView.Adapter<DateSelectorAdapter.DateSelectorViewHolder>() {

    private val selectableDates: List<Calendar>

    private var selectedDate: Calendar? = null

    init {
        selectableDates = mutableListOf<Calendar>().apply {
            add(Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -7) })
            add(Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -6) })
            add(Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -5) })
            add(Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -4) })
            add(Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -3) })
            add(Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -2) })
            add(Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) })
            add(Calendar.getInstance())
            add(Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, +1) })
            add(Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, +2) })
            add(Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, +3) })
            add(Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, +4) })
            add(Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, +5) })
            add(Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, +6) })
        }
    }

    interface DateSelectorListener {
        fun onDateSelected(date: Calendar)
    }

    override fun onBindViewHolder(holder: DateSelectorViewHolder, position: Int) {
        val date = selectableDates[position]
        val isSelected = date.get(Calendar.YEAR) == selectedDate?.get(Calendar.YEAR) &&
                date.get(Calendar.MONTH) == selectedDate?.get(Calendar.MONTH) &&
                date.get(Calendar.DAY_OF_MONTH) == selectedDate?.get(Calendar.DAY_OF_MONTH)

        holder.setDate(date, isSelected)
    }

    override fun getItemCount(): Int = selectableDates.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateSelectorViewHolder =
            DateSelectorViewHolder(parent, listener)

    fun setSelectedDate(date: Calendar) {
        selectedDate = date
    }

    fun getPosition(date: Calendar) : Int {
        selectableDates.forEachIndexed { index, d ->
            if (d.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                    d.get(Calendar.MONTH) == date.get(Calendar.MONTH) &&
                    d.get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH)) {
                return@getPosition index
            }
        }
        return@getPosition 0
    }

    class DateSelectorViewHolder(
            parent: ViewGroup,
            private val listener: DateSelectorListener) : androidx.recyclerview.widget.RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_date_selector, parent, false)
    ) {
        private val date: TextView
            get() = itemView.findViewById(R.id.date)

        private val day: TextView
            get() = itemView.findViewById(R.id.day)

        fun setDate(date: Calendar, isSelected: Boolean) {
            this.date.text = date.get(Calendar.DAY_OF_MONTH).toString()
            this.day.text = itemView.resources.getString(
                    R.string.date_selector_day_in_week,
                    SimpleDateFormat("E", Locale.JAPAN).format(Date(date.timeInMillis))
            )
            setTextColor(date)
            itemView.setOnClickListener { listener.onDateSelected(date) }
            itemView.isSelected = isSelected
        }

        private fun setTextColor(date: Calendar) {
            when (date.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SATURDAY -> this.date.setTextColor(Color.BLUE)
                Calendar.SUNDAY -> this.date.setTextColor(Color.RED)
            }
        }
    }
}