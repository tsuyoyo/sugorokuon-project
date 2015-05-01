/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network.radikoadaptation;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import tsuyogoro.sugorokuon.constants.SugorokuonConst;
import tsuyogoro.sugorokuon.models.entities.OnedayTimetable;
import tsuyogoro.sugorokuon.models.entities.Program;
import android.util.Log;

/**
 * 番組情報のparser。
 *
 * (daily ex)  http://radiko.jp/v2/api/program/today?area_id=JP13
 * (weekly ex) http://radiko.jp/v2/api/program/station/weekly?station_id=QRR
 *             --> Weeklyの場合はstation_id指定で、1局分しか取れないっぽい。
 *
 */
class ProgramResponseParser extends ResponseParserBase {

	private static class TagName {
		static String STATIONS  = "stations";
		static String STATION   = "station";
		static String STATION_NAME = "name";
		static String SCD 	    = "scd";
		static String PROGS	    = "progs";
		static String PROGS_DATE= "date";
		static String PROG	    = "prog";
		static String TITLE	    = "title";
		static String SUB_TITLE = "sub_title";
		static String PFM		= "pfm";
		static String DESC		= "desc";
		static String INFO		= "info";
		static String METAS		= "metas";
		static String META		= "meta";
		static String URL		= "url";
	}

    /**
     * Constructor.
     * 結果にfilterはかけず、無条件でparse結果を取得。
     *
     * @param source Data source.
     * @param inputEncoding Basically "UTF-8".
     */
	public ProgramResponseParser(InputStream source, String inputEncoding) {
		super(source, inputEncoding);
	}

    /**
     * １週間・１局分の番組データをparseし、
     * 7日分のTimeTableインスタンスを生成する。
     *
     * @return 失敗したらnullが返る。
     */
	public List<OnedayTimetable> parse() {
		/*
		 * <radiko>
		 * <ttl>...</ttl>
		 * <srvtime>......</srvtime>
		 * <stations>
		 * 		...
		 * </stations>
		 * </radiko>
		 */
		List<OnedayTimetable> timeTable = null;
		
		if(null == getParser()) {
			Log.e("SugoRokuon", "Construct has been failed.");
			throw new IllegalStateException();
		}
		try {	
			for(int e = getParser().getEventType(); 
				e != XmlPullParser.END_DOCUMENT; e = getParser().next()){
				if(XmlPullParser.START_TAG == e) {
					String tagName = getParser().getName();
					if(tagName.equals(TagName.STATIONS)) {
						timeTable = parseStations();
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
		return timeTable;
	}
	
	/*
	 * <stations>
	 * 		<station id=...>
	 * 		...
	 * 		</station>
	 * 	    <station id=...>
	 * 		...
	 * 		</station>
 	 * 		<station id=...>
	 * 		...
	 * 		</station>
	 * </stations>
	 * 
	 */
	private List<OnedayTimetable> parseStations() 
		throws XmlPullParserException, IOException {
		
		// Next to <stations>
		getParser().next();
		
		ArrayList<OnedayTimetable> timeTables = new ArrayList<OnedayTimetable>();
		
		for(int e = getParser().getEventType();
			e != XmlPullParser.END_TAG; e = getParser().next()) {
			
			if(XmlPullParser.START_TAG == e) {
				String tagName = getParser().getName();
				// <station>��parse
				if(tagName.equals(TagName.STATION)) {
					List<OnedayTimetable> stationInfo = parserStation();
					if(null != stationInfo) {
						timeTables.addAll(stationInfo);
					}
				}
			}
		}
		
		return timeTables;
	}

    /*
     * <station id=...> <-- IDは各programインスタンスに持たせる
     * 		<name>...</name>
     *		<scd>
     *			...
     *		</scd>
     *		<scd>
     *			...
     * 		</scd>
     * 	</station>
     *
     */
	private List<OnedayTimetable> parserStation() 
		throws XmlPullParserException, IOException {
		
		String stationId = getParser().getAttributeValue(0);

		// Next to <station>
		getParser().next();
				
		List<OnedayTimetable> timeTables = null;
		for(int e = getParser().getEventType();
				e != XmlPullParser.END_TAG; e = getParser().next()) {
			if(XmlPullParser.START_TAG == e) {
				String tagName = getParser().getName();
                // 1日分のprogramを読む
				if(tagName.equals(TagName.SCD)) {
					timeTables = parseWeelkyTimetable(stationId);
				}
                // 特に使わないので捨てる。
				else {
					getText();
				}
			}
		}
		
		return timeTables;
	}

    /*
     * <scd> <- 曜日ごとに1つのscd。
     * 	 <progs>
     *   	...
     * 	 </progs>
     * </scd>
     */
	private List<OnedayTimetable> parseWeelkyTimetable(String stationId) 
		throws XmlPullParserException, IOException {

		// Next to <sdc>
		getParser().next();
		
		List<OnedayTimetable> timeTables = new ArrayList<OnedayTimetable>();
		for(int e = getParser().getEventType();
			e != XmlPullParser.END_TAG; e = getParser().next()){		
			if(XmlPullParser.START_TAG == e) {
				String tagName = getParser().getName();
				if(tagName.equals(TagName.PROGS)) {
					OnedayTimetable onedayTable = parseOnedayTimetable(stationId);
					if(null != onedayTable) {
						timeTables.add(onedayTable);
					} else {
						Log.e(SugorokuonConst.LOGTAG, 
								"Failed to parse parseWeelkyTimetable : " + stationId);
					}
				}
			}
		}
		return timeTables;
	}

    /*
     * <progs>
     * 	  <date>yyyymmdd</date> <- 日付
     * 	  <prog> ... </prog>    <- 1番組分のデータ
     *	  ...
     *    <prog> ... </prog>
     * </progs>
     *
     */
	private OnedayTimetable parseOnedayTimetable(String stationId) 
		throws XmlPullParserException, IOException {

        // TimeTableインスタンス。
		OnedayTimetable timeTable = null;
		
		// Next to <progs>
		getParser().next();
		
		for(int e = getParser().getEventType();
			e != XmlPullParser.END_TAG; e = getParser().next()){
			if(XmlPullParser.START_TAG == e) {
				String tagName = getParser().getName();

                // Dateを読み込んだタイミングでTimeTableインスタンスを生成。
				if(tagName.equals(TagName.PROGS_DATE)) {
					String date = getText();
					int year  = Integer.valueOf(date.subSequence(0, 4).toString());
					int month = Integer.valueOf(date.subSequence(4, 6).toString()) - 1;
					int day   = Integer.valueOf(date.subSequence(6, 8).toString());
					timeTable = new OnedayTimetable(year, month, day, stationId);
				}
				else if(tagName.equals(TagName.PROG)) {
					Program p = parseProgram(stationId);
					timeTable.programs.add(p);
				}
			}
		}
		
		return timeTable;
	}

	/*
	 * <prog ft="20121022050000" to="20121022085000" ftl="0500" tol="0850" dur="13800">
	 *		<title>...</title>
	 *		<sub_title/>
	 *		<pfm>...</pfm>
	 *		<desc>...</desc>
	 *		<info>...</info>
	 *		<metas>
	 *		   ...
	 *		</metas>
	 *		<url>...</url>
	 *	</prog>
	 *
	 */
	private Program parseProgram(String stationId) throws XmlPullParserException, IOException {
		Program.Builder builder = new Program.Builder();

        // 既に読み込んであるstation IDをprogramにset
		builder.stationId = stationId;

        // StartとEndの時間をとる
        String startTime = getParser().getAttributeValue(null, "ft");
        String endTime = getParser().getAttributeValue(null, "to");
		builder.startTime = parseTime(startTime);
		builder.endTime   = parseTime(endTime);
		
		// Next to <prog>
		getParser().next();
		
		for(int e = getParser().getEventType();
				e != XmlPullParser.END_TAG; e = getParser().next()){
			if(XmlPullParser.START_TAG == e) {
				String tagName = getParser().getName();
				
				if(tagName.equals(TagName.TITLE)) {
					builder.title = AlphabetNormalizer.zenkakuToHankaku(getText());
				}
				else if(tagName.equals(TagName.SUB_TITLE)) {
					builder.subtitle = AlphabetNormalizer.zenkakuToHankaku(getText());
				}
				else if(tagName.equals(TagName.PFM)) {
					builder.personalities = AlphabetNormalizer.zenkakuToHankaku(getText());
				}
				else if(tagName.equals(TagName.DESC)) {
					builder.description = AlphabetNormalizer.zenkakuToHankaku(getText());
				}
				else if(tagName.equals(TagName.INFO)) {
					builder.info = AlphabetNormalizer.zenkakuToHankaku(getText());
				}
				else if(tagName.equals(TagName.URL)) {
					builder.url = getText();
				}
				else if(tagName.equals(TagName.METAS)) {
					parseMetas(builder);
				}
			}
		}
		return builder.create();
	}
	
	/*
	 * <metas>
	 *	  <meta name="twitter" value="#radiko"/>
	 *	  <meta name="twitter-hash" value="#radiko"/>
	 *	  <meta name="facebook-fanpage" value="http://www.facebook.com/radiko.jp"/>
	 * </metas>
	 * 
	 */
	private void parseMetas(Program.Builder builder) throws XmlPullParserException, IOException {
		// Next to <metas>
		getParser().next();
		for(int e = getParser().getEventType();	
				XmlPullParser.END_TAG != e; e = getParser().next()) {
			if(XmlPullParser.START_TAG == e) {
				String tagName = getParser().getName();

                // metaを読み捨てていく。いまのところ特に情報は取らない
				if(tagName.equals(TagName.META)) {
					getParser().next();
				}
			}
		}	
	}

    /*
     * ft="20121022050000"
     * このフォーマットをparseする。
     */
    private Calendar parseTime(String time) {

        SimpleDateFormat formatter = new SimpleDateFormat();
        formatter.applyPattern("yyyyMMddHHmmss");

        Date d = null;
        try {
            d = formatter.parse(time);
        } catch (ParseException e) {
            Log.e("Sugorokuon", "Failed to parse program date : " + e.getMessage());
        }

        Calendar c = null;
        if (null != d) {
            c = Calendar.getInstance();
            c.setTime(d);
            c.set(Calendar.MILLISECOND, 0);
        }

        return c;
    }
	
}