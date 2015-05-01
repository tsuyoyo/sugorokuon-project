/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network.radikoadaptation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import tsuyogoro.sugorokuon.constants.StationLogoSize;
import tsuyogoro.sugorokuon.constants.SugorokuonConst;
import tsuyogoro.sugorokuon.models.entities.Station;
import android.util.Log;

class StationListResponseParser extends ResponseParserBase {
	
	private static class TagName {
		static final String STATIONS = "stations";
		static final String STATION  = "station";
		static final String STATION_ID = "id";
		static final String STATION_NAME = "name";
		static final String STATION_ASCII_NAME = "ascii_name";
		static final String SITE_LINK = "href";
		static final String LOGO_XSMALL = "logo_xsmall";
		static final String LOGO_SMALL  = "logo_small";
		static final String LOGO_MEDIUM = "logo_medium";
		static final String LOGO_LARGE  = "logo_large";
		static final String LOGO        = "logo";
		static final String FEED        = "feed";
		static final String BANNER      = "banner";
	}

	private final StationLogoSize mLogoSize;
	
	/**
	 * Constructor.
	 * 
	 * @param source Data source.
	 * @param logoSize Size of station logo to request server.
	 * @param inputEncoding Basically "UTF-8".
	 */
	public StationListResponseParser(InputStream source, StationLogoSize logoSize, String inputEncoding) {
		super(source, inputEncoding);
		mLogoSize = logoSize;
	}

    /**
     * Station一覧をrootからparseする。
     *
     * @return Stationのリスト。parseに失敗したらnull。
     */
	public List<Station> parse() {
		/*
		 * <?xml version="1.0" encoding="UTF-8" ?> 
		 * <stations area_id="JP13" area_name="TOKYO JAPAN">
		 *   ...
 		 * </stations>
		 */
		List<Station> stations = new ArrayList<Station>();
		
		if(null == getParser()) {
			Log.e(SugorokuonConst.LOGTAG, "Construct of StationListParser has been failed.");
			throw new IllegalStateException();
		}
		try {	
			for(int e = getParser().getEventType(); 
				e != XmlPullParser.END_DOCUMENT; e = getParser().next()){
				if(XmlPullParser.START_TAG == e) {
					String tagName = getParser().getName();
					// <stations>
					if(tagName.equals(TagName.STATIONS)) {
						stations.addAll(parseStations());
					}
				}
			}
		} catch(XmlPullParserException e) {
			Log.e(SugorokuonConst.LOGTAG, 
					"Failed parsing : XmlPullParserException : " 
					+ e.getMessage());
		} catch(IOException e) {
			Log.e(SugorokuonConst.LOGTAG,
					"Failed parsing : IOException : "
					+ e.getMessage());
		}
		
		return stations;
	}
	
	/*
	 * <stations area_id="JP13" area_name="TOKYO JAPAN">
	 *   <station>
	 *   	...
     *   </station>
	 *   <station>
	 *   	...
     *   </station>
	 *   <station>
	 *   	...
     *   </station>
     * <stations>
	 */
	private List<Station> parseStations() throws XmlPullParserException, IOException {
		// Next to <stations>
		getParser().next();
		
		List<Station> stations = new ArrayList<Station>();
		
		for(int e = getParser().getEventType();
			e != XmlPullParser.END_TAG; e = getParser().next()) {
			if(XmlPullParser.START_TAG == e) {
				String tagName = getParser().getName();
				// <station>
				if(tagName.equals(TagName.STATION)) {
					stations.add(parseStation());
				}
			}
		}
		return stations;
	}
	
	/*
	 *  <station>
     *     <id>...</id>
     *     <name>...</name>
     *     <ascii_name>...</ascii_name>
     *     <href>...</href>
     *     <logo_xsmall>...</logo_xsmall>
     *     <logo_small>...</logo_small>
     *     <logo_medium>...</logo_medium>
     *     <logo_large>...</logo_large>
     *     <logo width="124" height="40">...</logo>
     *     <logo width="344" height="80">...</logo>
     *     <logo width="688" height="160">...</logo>
     *     <logo width="172" height="40">...</logo>
     *     <logo width="224" height="100">...</logo>
     *     <logo width="448" height="200">...</logo>
     *     <logo width="112" height="50">...</logo>
     *     <logo width="168" height="75">...</logo>
     *     <logo width="258" height="60">...</logo>
     *     <feed>...</feed>
     *     <banner>...</banner>
     *  </station>
     *  
	 */
	private Station parseStation() throws XmlPullParserException, IOException {
		Station.Builder builder = new Station.Builder();
				
		// Next to <station>
		getParser().next();
		
		for(int e = getParser().getEventType();
				e != XmlPullParser.END_TAG; e = getParser().next()){
			if(XmlPullParser.START_TAG == e) {
				String tagName = getParser().getName();				
				if(tagName.equals(TagName.STATION_ID)) {
					builder.id = getText();
				}
				else if(tagName.equals(TagName.STATION_NAME)) {
					builder.name = getText();
				}
				else if(tagName.equals(TagName.STATION_ASCII_NAME)) {
					builder.ascii_name = getText();
				}
				else if(tagName.equals(TagName.SITE_LINK)) {
					builder.siteUrl = getText();
				}
				else if(tagName.equals(TagName.LOGO_XSMALL) ) {
					String logoLink = getText();
					if(StationLogoSize.XSMALL.equals(mLogoSize)) {
						builder.logoUrl = logoLink;
					}
				}
				else if(tagName.equals(TagName.LOGO_SMALL) ) {
					String logoLink = getText();
					if(StationLogoSize.SMALL.equals(mLogoSize)) {
						builder.logoUrl = logoLink;
					}
				}
				else if(tagName.equals(TagName.LOGO_MEDIUM) ) {
					String logoLink = getText();
					if(StationLogoSize.MEDIUM.equals(mLogoSize)) {
						builder.logoUrl = logoLink;
					}
				}
				else if(tagName.equals(TagName.LOGO_LARGE) ) {
					String logoLink = getText();
					if(StationLogoSize.LARGE.equals(mLogoSize)) {
						builder.logoUrl = logoLink;
					}
				}
				else if(tagName.equals(TagName.BANNER)) {
					builder.bannerUrl = getText();
				}
				else if(tagName.equals(TagName.LOGO) || tagName.equals(TagName.FEED)) {
					getText();// 使わないので捨てる。
				}
			}
		}
		
		return builder.create();
	}	
	
}
