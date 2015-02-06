/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.datatype;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * １日分・１局分の番組表データ
 *
 */
public class OnedayTimetable {

	public List<Program> programs;
	public Calendar date;
	public String stationId;
	
	public OnedayTimetable(int _year, int _month, int _day, String _stationId) {
		date = Calendar.getInstance(Locale.JAPAN);
		date.set(Calendar.YEAR, _year);
		date.set(Calendar.MONTH, _month);
		date.set(Calendar.DATE, _day);
		stationId = _stationId;
		programs = new ArrayList<Program>();
	}
	
	public OnedayTimetable(Calendar calendar, String _stationId) {
		date = calendar;
		stationId = _stationId;
		programs = new ArrayList<Program>();
	}
	
}
	