/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AbstractHttpClient;

import tsuyogoro.sugorokuon.datatype.OnedayTimetable;
import tsuyogoro.sugorokuon.datatype.Station;
import android.util.Log;

/**
 * Reference
 * http://www.dcc-jpl.com/foltia/wiki/radikomemo
 *
 * @author Tsuyoyo
 *
 */
public class ProgramListDownloader {

	private final static String UTF_8 = "UTF-8";
	
	private final static String WEEKLY_PROGRAM = 
		"http://radiko.jp/v2/api/program/station/weekly";
	
	private final static String QUERY_STATION_ID = "station_id";

	public ProgramListDownloader() {
		
	}

    /**
     * 指定したStation（複数局分）の
     * 一週間分のProgram情報をdonwloadする（結果にfilterはかけない）。
     *
     * @param stations Downloadするstation IDリスト。
     * @param client HttpClientインスタンス。
     * @return stations全ての一週間分のProgram。
     */
	public List<OnedayTimetable> getWeeklyTimeTable(List<Station> stations, 
			AbstractHttpClient client) {
		return getWeeklyTimeTable(stations, client, null);
	}

    /**
     * 指定したStation（複数局分）の
     * 一週間分のProgram情報をdonwloadし、結果にfilterをかける。
     *
     * @param stations Downloadするstation IDリスト。
     * @param client HttpClientインスタンス。
     * @param filter downloadした結果にfilterをかけるためのinstance。
     * @return stations全ての一週間分のProgramで、filterしたもの。
     */
	public List<OnedayTimetable> getWeeklyTimeTable(List<Station> stations, 
			AbstractHttpClient client, IProgramListParserFilter filter) {
		ArrayList<OnedayTimetable> programs = new ArrayList<OnedayTimetable>();
		for(Station station : stations) {
			programs.addAll(getWeeklyTimeTable(station.id, client, filter));
		}
		return programs;		
	}

    /**
     * 指定したStation（1局分）の
     * 一週間分のProgram情報をdonwload（filterはかけない）。
     *
     * @param stationId DownloadするstationのID。
     * @param client HttpClientインスタンス。
     * @return station の一週間分のProgram。
     */
	public List<OnedayTimetable> getWeeklyTimeTable(String stationId, 
			AbstractHttpClient client) {
		return getWeeklyTimeTable(stationId, client, null);
	}

    /**
     * 指定したStation（1局分）の
     * 一週間分のProgram情報をdonwloadしてfilterをかける。
     *
     * @param stations DownloadするstationのID。
     * @param client HttpClientインスタンス。
     * @param filter 結果にかけるfilter
     * @return stationの一週間分のProgramにfilterをかけたもの。失敗したらnull。
     */
	public List<OnedayTimetable> getWeeklyTimeTable(String stationId, 
			AbstractHttpClient client, IProgramListParserFilter filter) {
		
		// URL to get weekly program of the radio station.
		String url = WEEKLY_PROGRAM + "?" + QUERY_STATION_ID + "=" + stationId;
		HttpGet httpGet = new HttpGet(url);
		
		// Download program list(xml) and parse it.
		HttpResponse httpRes;
		try {
			// Download program XML data.
			httpRes = client.execute(httpGet);
			
			// Parse the response.
			InputStream programData = httpRes.getEntity().getContent();
			ProgramListParser parser = new ProgramListParser(programData, UTF_8, filter);
			return parser.parse();
			
		} catch(IOException e) {
			Log.e("SugoRokuon", "IOException at getProgramList:" + e.getMessage());
		}
		
		return null;
	}
	
}
