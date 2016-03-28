/**
 * Copyright (c)
 * 2016 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network.radikoapi;

import org.simpleframework.xml.transform.Transform;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import tsuyogoro.sugorokuon.utils.SugorokuonLog;

class ApiDateConverter implements Transform<Calendar> {

    private static final String FORMAT_yyyyMMdd = "yyyyMMdd";

    private static final String FORMAT_yyyyMMddHHmmss = "yyyyMMddHHmmss";

    private static final String FORMAT_yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";

    private String decideFormatPatten(String dateStr) {
        if (dateStr.length() == FORMAT_yyyyMMdd.length()) {
            return FORMAT_yyyyMMdd;
        } else if (dateStr.length() == FORMAT_yyyyMMddHHmmss.length()) {
            return FORMAT_yyyyMMddHHmmss;
        } else if (dateStr.length() == FORMAT_yyyy_MM_dd_HH_mm_ss.length()) {
            return FORMAT_yyyy_MM_dd_HH_mm_ss;
        } else {
            return null;
        }
    }

    private void padCalendarFields(Calendar c, String formatPattern) {
        switch (formatPattern) {
            case FORMAT_yyyyMMdd:
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                break;
            case FORMAT_yyyyMMddHHmmss:
            case FORMAT_yyyy_MM_dd_HH_mm_ss:
                c.set(Calendar.MILLISECOND, 0);
                break;
            default:
                break;
        }
    }

    @Override
    public Calendar read(String value) throws Exception {

        String formatPattern = decideFormatPatten(value);
        if (formatPattern == null) {
            SugorokuonLog.w("Format of date in program list is different from expected ones"
                    + value);
            return null;
        }

        SimpleDateFormat formatter = new SimpleDateFormat(formatPattern, Locale.JAPAN);

        Date d = null;
        try {
            d = formatter.parse(value);
        } catch (ParseException e) {
            SugorokuonLog.e("Failed to parse program date : " + e.getMessage());
        }

        Calendar c = null;
        if (null != d) {
            c = Calendar.getInstance();
            c.setTime(d);
            padCalendarFields(c, formatPattern);
        }

        return c;
    }

    @Override
    public String write(Calendar value) throws Exception {
        // No use case to serialize
        return null;
    }
}
