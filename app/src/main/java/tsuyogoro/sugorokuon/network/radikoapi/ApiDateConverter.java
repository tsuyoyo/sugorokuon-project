/**
 * Copyright (c)
 * 2016 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network.radikoapi;

import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import tsuyogoro.sugorokuon.utils.SugorokuonLog;

class ApiDateConverter implements Converter<Calendar> {

    private static final String FORMAT_yyyyMMdd = "yyyyMMdd";

    private static final String FORMAT_yyyyMMddhhmmss = "yyyyMMddhhmmss";

    private static final String FORMAT_yyyy_MM_dd_hh_mm_ss = "yyyy-MM-dd hh:mm:ss";

    private String decideFormatPatten(String dateStr) {
        if (dateStr.length() == FORMAT_yyyyMMdd.length()) {
            return FORMAT_yyyyMMdd;
        } else if (dateStr.length() == FORMAT_yyyyMMddhhmmss.length()) {
            return FORMAT_yyyyMMddhhmmss;
        } else if (dateStr.length() == FORMAT_yyyy_MM_dd_hh_mm_ss.length()) {
            return FORMAT_yyyy_MM_dd_hh_mm_ss;
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
            case FORMAT_yyyyMMddhhmmss:
            case FORMAT_yyyy_MM_dd_hh_mm_ss:
                c.set(Calendar.MILLISECOND, 0);
                break;
            default:
                break;
        }
    }

    @Override
    public Calendar read(InputNode node) throws Exception {

        // メモ : node.getValue()で値が取れるのは1回限りで、2回呼ぶとnullが返ってくる
        String value = node.getValue();
        if (value == null) {
            SugorokuonLog.d("node.getValue() returns null though DateConverter is called");
            return null;
        }

        String formatPattern = decideFormatPatten(value);
        if (formatPattern == null) {
            SugorokuonLog.w("Format of date in program list is different from expected ones"
                    + node.getValue());
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
    public void write(OutputNode node, Calendar value) throws Exception {
        // No use case to serialize
    }
}
