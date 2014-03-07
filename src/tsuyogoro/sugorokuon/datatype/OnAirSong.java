/**
 * Copyright (c) 
 * 2013 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.datatype;

import java.util.Calendar;

public class OnAirSong {
	
	public final String artist;
	
	public final String title;
	
	public final Calendar date;	
	
	public OnAirSong(String _artist, String _title, Calendar _date) {
		artist = _artist;
		title  = _title;
		date   = _date;
	}
}
