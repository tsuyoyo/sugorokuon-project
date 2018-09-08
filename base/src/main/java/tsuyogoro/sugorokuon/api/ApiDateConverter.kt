/**
 * Copyright (c)
 * 2016 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.api

import org.simpleframework.xml.transform.Transform

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ApiDateConverter : Transform<Calendar> {

    private fun decideFormatPatten(dateStr: String): String? {
        return if (dateStr.length == FORMAT_yyyyMMdd.length) {
            FORMAT_yyyyMMdd
        } else if (dateStr.length == FORMAT_yyyyMMddHHmmss.length) {
            FORMAT_yyyyMMddHHmmss
        } else if (dateStr.length == FORMAT_yyyy_MM_dd_HH_mm_ss.length) {
            FORMAT_yyyy_MM_dd_HH_mm_ss
        } else {
            null
        }
    }

    private fun padCalendarFields(c: Calendar, formatPattern: String) {
        when (formatPattern) {
            FORMAT_yyyyMMdd -> {
                c.set(Calendar.HOUR_OF_DAY, 0)
                c.set(Calendar.MINUTE, 0)
                c.set(Calendar.SECOND, 0)
                c.set(Calendar.MILLISECOND, 0)
            }
            FORMAT_yyyyMMddHHmmss, FORMAT_yyyy_MM_dd_HH_mm_ss -> c.set(Calendar.MILLISECOND, 0)
            else -> {
            }
        }
    }

    @Throws(Exception::class)
    override fun read(value: String): Calendar? {

        val formatPattern = decideFormatPatten(value) ?: return null

        val formatter = SimpleDateFormat(formatPattern, Locale.JAPAN)

        var d: Date? = null
        try {
            d = formatter.parse(value)
        } catch (e: ParseException) {
        }

        var c: Calendar? = null
        if (null != d) {
            c = Calendar.getInstance()
            c!!.time = d
            padCalendarFields(c, formatPattern)
        }

        return c
    }

    @Throws(Exception::class)
    override fun write(value: Calendar): String? {
        // No use case to serialize
        return null
    }

    companion object {

        private val FORMAT_yyyyMMdd = "yyyyMMdd"

        private val FORMAT_yyyyMMddHHmmss = "yyyyMMddHHmmss"

        private val FORMAT_yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss"
    }
}
