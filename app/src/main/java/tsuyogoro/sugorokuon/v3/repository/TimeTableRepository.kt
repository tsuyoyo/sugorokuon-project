package tsuyogoro.sugorokuon.v3.repository

import io.reactivex.Maybe
import io.reactivex.processors.BehaviorProcessor
import tsuyogoro.sugorokuon.v3.api.TimeTableApi
import tsuyogoro.sugorokuon.v3.api.response.TimeTableResponse
import tsuyogoro.sugorokuon.v3.constant.Area
import java.util.*

class TimeTableRepository(
        private val timeTableApi: TimeTableApi,
        private val selectedDate: BehaviorProcessor<Calendar> = BehaviorProcessor.create()
) {

    init {
        selectedDate.onNext(Calendar.getInstance())
    }

    fun observeSelectedDate(): BehaviorProcessor<Calendar> = selectedDate

    fun selectDate(date: Calendar) {
        selectedDate.onNext(date)
    }

    fun fetchTimeTable(date: Calendar, area: Area): Maybe<TimeTableResponse> =
            timeTableApi.getTimeTable(
                    String.format(
                            "%04d%02d%02d",
                            date.get(Calendar.YEAR),
                            date.get(Calendar.MONTH) + 1,
                            date.get(Calendar.DAY_OF_MONTH)),
                    area.id
            )

}