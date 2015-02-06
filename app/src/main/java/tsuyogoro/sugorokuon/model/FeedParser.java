/**
 * Copyright (c) 
 * 2013 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import tsuyogoro.sugorokuon.constant.SugorokuonConst;
import tsuyogoro.sugorokuon.datatype.Feed;
import tsuyogoro.sugorokuon.datatype.OnAirSong;
import android.util.Log;

public class FeedParser extends ParserBase {
	
	private static class TagName {
		static String STATION 	= "station";
		static String ITEMS 	= "items";
		static String ITEM  	= "item";
		static String CM		= "cm";
		static String BANNER	= "banner";
		static String EXTRA		= "extra";
	}
	
	private static class AttributeName {
		static String TYPE   = "type";
		static String STAMP  = "stamp";
		static String ARTIST = "artist";
		static String TITLE  = "title";
	}
	
	/**
	 * Constructor.
	 * 
	 * @param source Data source.
	 * @param inputEncoding Basically "UTF-8".
	 */
	public FeedParser(InputStream source, String inputEncoding) {
		super(source, inputEncoding);
	}

    /**
     * FeedのxmlデータをparseしてFeedインスタンスを作る。
     *
     * @return 失敗したらnullが返る。
     */
	public Feed parse() {
		/* <feed>
		 * <station id="xxx">
		 * ...
		 * </station>
		 * </feed>
		 */
		List<OnAirSong> onAirSongs = null;
		
		if(null == getParser()) {
			Log.e(SugorokuonConst.LOGTAG, "FeedParser constructor has been failed.");
			throw new IllegalStateException();
		}
		try {	
			for(int e = getParser().getEventType(); 
				e != XmlPullParser.END_DOCUMENT; e = getParser().next()){
				if(XmlPullParser.START_TAG == e) {
					String tagName = getParser().getName();
					if(tagName.equals(TagName.STATION)) {
						onAirSongs = parseStation();
					}
				}
			}
		} catch(XmlPullParserException e) {
			Log.e(SugorokuonConst.LOGTAG, 
					"Failed parsing : XmlPullParserException : " + e.getMessage());
		} catch(IOException e) {
			Log.e(SugorokuonConst.LOGTAG,
					"Failed parsing : IOException : " + e.getMessage());
		}
		
		if(null != onAirSongs) {
			return new Feed(onAirSongs);
		} else {
			return null;
		}
	}

	/*
	 * <station id="FMT">
	 *   <items>...</items>
	 *   <cm>...</cm>
	 *   <banner>...</banner>
	 *   <extra>...</extra>
	 * </station>
	 */ 
	private List<OnAirSong> parseStation() throws XmlPullParserException, IOException {
        // <station>の次へ進める
		getParser().next();
	
		List<OnAirSong> items = null;
		
		for(int e = getParser().getEventType();
			e != XmlPullParser.END_TAG; e = getParser().next()) {	
			if(XmlPullParser.START_TAG == e) {
				String tagName = getParser().getName();

                // OnAir曲情報をparse。
				if(tagName.equals(TagName.ITEMS)) {
					items = parseItems();
				}
				else if(tagName.equals(TagName.CM)) {
					parseCM();
				}
				else if(tagName.equals(TagName.BANNER)) {
					parseBanner();
				}
				else if(tagName.equals(TagName.EXTRA)) {
                    // 何もしなくてよいはず。
				}
			}
		}
		
		return items;
	}	
	
	/*
	 * <items>
	 *   <item artist="bent Fabric"
	 *   	   evid="" 
	 *   	   href=""
	 *   	   img_210x170="http://radiko.jp/v2/static/feed-icon/210x170/music.png" 
	 *   	   itemid="50f3b79fbbb11f52" 
	 *   	   stamp="2013-01-14T16:44:32"
	 *   	   title="Shake"
	 *         type="music"/>
	 *  </item>
	 */
	private List<OnAirSong> parseItems() throws XmlPullParserException, IOException {
        // <items>の次へ進める
		getParser().next();
	
		List<OnAirSong> items = new ArrayList<OnAirSong>();
		
		for(int e = getParser().getEventType();
			e != XmlPullParser.END_TAG; e = getParser().next()) {	
			if(XmlPullParser.START_TAG == e) {
				String tagName = getParser().getName();
				if(tagName.equals(TagName.ITEM)) {
                    // musicのtypeが、最近のonAir曲のitem。
					if(getParser().getAttributeValue(null, AttributeName.TYPE)
							.equals("music")) {
						String artist = getParser().getAttributeValue(null, AttributeName.ARTIST);
						String title  = getParser().getAttributeValue(null, AttributeName.TITLE);
						String stamp  = getParser().getAttributeValue(null, AttributeName.STAMP);
						items.add(new OnAirSong(artist, title, parseStamp(stamp)));
					}
					getParser().next();
				}
			}
		}
		
		return items;
	}

    /*
     * "stamp="2013-01-14T16:44:32""
     * このフォーマットをparseする。
     */
	private Calendar parseStamp(String stamp) {
		
		String year = stamp.substring(0, 4);
		String month = stamp.substring(5, 7);
		String day = stamp.substring(8, 10);
		String hour = stamp.substring(11, 13);
		String min = stamp.substring(14, 16);		

		Calendar cal = Calendar.getInstance();
		cal.set(Integer.valueOf(year),
				Integer.valueOf(month),
				Integer.valueOf(day),
				Integer.valueOf(hour),
				Integer.valueOf(min));
		return cal;
	}
	
	/*
	 * <cm>
	 *   <item .../>
	 *   <item .../>
  	 *   <item .../>
  	 * </cm>
	 */
	private void parseCM() throws XmlPullParserException, IOException {
		for(int e = getParser().getEventType();
			e != XmlPullParser.END_TAG; e = getParser().next()) {
            // ひたすら読み飛ばす。
		}
	}		


	/* 
	 * <banner>
	 *   <item .../>
	 * </banner>
	 */
	private void parseBanner() throws XmlPullParserException, IOException {
		for(int e = getParser().getEventType();
			e != XmlPullParser.END_TAG; e = getParser().next()) {
            // ひたすら読み飛ばす。
		}
	}
	
}
