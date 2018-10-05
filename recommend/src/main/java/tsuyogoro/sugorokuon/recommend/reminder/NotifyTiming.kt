package tsuyogoro.sugorokuon.recommend.reminder

import android.support.annotation.StringRes
import tsuyogoro.sugorokuon.recommend.R
import java.util.*

enum class NotifyTiming (@StringRes var optionStrId: Int) {
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

    private fun calculateNotifyTime(input: Calendar?): Calendar? {
        var input = input
        var res: Calendar? = null
        when (this) {
            NOT_SET -> input = null
            BEFORE_10_MIN -> input!!.add(Calendar.MINUTE, -10)
            BEFORE_30_MIN -> input!!.add(Calendar.MINUTE, -30)
            BEFORE_1_HOUR -> input!!.add(Calendar.HOUR_OF_DAY, -1)
            BEFORE_2_HOUR -> input!!.add(Calendar.HOUR_OF_DAY, -2)
            BEFORE_5_HOUR -> input!!.add(Calendar.HOUR_OF_DAY, -5)
        }
        res = input
        return res
    }

//    fun calculateNotifyTime(input: List<Program>): Calendar? {
//
//        val programs = ArrayList<Program>(input)
//
//        if (this == NOT_SET) {
//            return null
//        }
//
//        Collections.sort(input, object : Comparator<Program>() {
//            fun compare(lhs: Program, rhs: Program): Int {
//                return lhs.startTime.compareTo(rhs.startTime)
//            }
//        })
//
//        // 今から通知時間後までに放送を開始しちゃう番組をフィルタ
//        // (例えば、設定が30分で、今から30分以内に番組を開始しちゃう)
//        val justAfterSetTime = Calendar.getInstance(Locale.JAPAN)
//        when (this) {
//            BEFORE_10_MIN -> justAfterSetTime.add(Calendar.MINUTE, 10)
//            BEFORE_30_MIN -> justAfterSetTime.add(Calendar.MINUTE, 30)
//            BEFORE_1_HOUR -> justAfterSetTime.add(Calendar.HOUR_OF_DAY, 1)
//            BEFORE_2_HOUR -> justAfterSetTime.add(Calendar.HOUR_OF_DAY, 2)
//            BEFORE_5_HOUR -> justAfterSetTime.add(Calendar.HOUR_OF_DAY, 5)
//        }
//        while (!programs.isEmpty() && 0 > programs[0].startTime.compareTo(justAfterSetTime)) {
//            programs.removeAt(0)
//        }
//
//        return if (!programs.isEmpty()) {
//            calculateNotifyTime(programs[0].startTime)
//        } else {
//            null
//        }
//    }
}