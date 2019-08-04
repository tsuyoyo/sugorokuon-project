package tsuyogoro.sugorokuon.recommend.reminder

import androidx.annotation.StringRes
import tsuyogoro.sugorokuon.recommend.R
import java.util.*

enum class ReminderTiming (@StringRes var optionStrId: Int) {
    /** 通知しない  */
    NOT_SET(R.string.settings_remindtiming_not_set),
    /** 10分前  */
    BEFORE_10_MIN(R.string.settings_remindtiming_ten_min_before),
    /** 30分前  */
    BEFORE_30_MIN(R.string.settings_remindtiming_thirty_min_before),
    /** 1時間前  */
    BEFORE_1_HOUR(R.string.settings_remindtiming_one_hour_before),
    /** 2時間前  */
    BEFORE_2_HOUR(R.string.settings_remindtiming_two_hour_before),
    /** 5時間前  */
    BEFORE_5_HOUR(R.string.settings_remindtiming_five_hour_before);

    fun calculateNotifyTime(onAirTimeInSec: Long): Calendar? =
        Calendar.getInstance()
            .apply { time = Date(onAirTimeInSec) }
            .let {
                when (this) {
                    NOT_SET -> return@let null
                    BEFORE_10_MIN -> it.add(Calendar.MINUTE, -10)
                    BEFORE_30_MIN -> it.add(Calendar.MINUTE, -30)
                    BEFORE_1_HOUR -> it.add(Calendar.HOUR_OF_DAY, -1)
                    BEFORE_2_HOUR -> it.add(Calendar.HOUR_OF_DAY, -2)
                    BEFORE_5_HOUR -> it.add(Calendar.HOUR_OF_DAY, -5)
                }
                return@let it
            }

    fun inMilliSec(): Long = when (this) {
        NOT_SET -> 0
        BEFORE_10_MIN -> 10 * 60 * 1000
        BEFORE_30_MIN -> 30 * 60 * 1000
        BEFORE_1_HOUR -> 60 * 60 * 1000
        BEFORE_2_HOUR -> 120 * 60 * 1000
        BEFORE_5_HOUR -> 300 * 60 * 1000
    }
}