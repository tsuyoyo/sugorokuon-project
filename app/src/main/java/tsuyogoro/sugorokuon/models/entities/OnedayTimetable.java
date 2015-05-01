/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.models.entities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * １日分・１局分の番組表データ
 */
public class OnedayTimetable {

    public List<Program> programs;

    public Calendar date;

    public String stationId;

    public OnedayTimetable(int year, int month, int day, String stationId) {

        this.date = Calendar.getInstance(Locale.JAPAN);
        this.date.set(year, month, day, 0, 0, 0);
        this.date.set(Calendar.MILLISECOND, 0);

        this.stationId = stationId;
        this.programs = new ArrayList<Program>();
    }

}
