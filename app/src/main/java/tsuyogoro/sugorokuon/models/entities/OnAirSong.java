/**
 * Copyright (c) 
 * 2013 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.models.entities;

import java.util.Calendar;

public class OnAirSong {

    public final String stationId;

	public final String artist;
	
	public final String title;
	
	public final Calendar date;

    public final String itemId;
	
	public OnAirSong(String stationId, String artist, String title,
                     Calendar date, String itemId) {
        this.stationId = stationId;
		this.artist = artist;
		this.title  = title;
        this.itemId = itemId;

        // radikoからミリ秒まで細かいデータが来ることは無いので
        // 余計なズレが起きないようここでリセットしておく
		this.date   = date;
        this.date.set(Calendar.MILLISECOND, 0);
	}
}
