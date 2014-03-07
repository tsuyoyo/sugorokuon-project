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
	 * �w�肵��Station�i�����Ǖ��j��
	 * ��T�ԕ���Program����donwload����i���ʂ�filter�͂����Ȃ��j�B
	 * 
	 * @param stations Download����station ID���X�g�B
	 * @param client HttpClient�C���X�^���X�B
	 * @return stations�S�Ă̈�T�ԕ���Program�B
	 */
	public List<OnedayTimetable> getWeeklyTimeTable(List<Station> stations, 
			AbstractHttpClient client) {
		return getWeeklyTimeTable(stations, client, null);
	}
	
	/**
	 * �w�肵��Station�i�����Ǖ��j��
	 * ��T�ԕ���Program����donwload���A���ʂ�filter��������B
	 * 
	 * @param stations Download����station ID���X�g�B
	 * @param client HttpClient�C���X�^���X�B
	 * @param filter download�������ʂ�filter�������邽�߂�instance�B
	 * @return stations�S�Ă̈�T�ԕ���Program�ŁAfilter�������́B
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
	 * �w�肵��Station�i1�Ǖ��j��
	 * ��T�ԕ���Program����donwload�ifilter�͂����Ȃ��j�B
	 * 
	 * @param stationId Download����station��ID�B
	 * @param client HttpClient�C���X�^���X�B
	 * @return station �̈�T�ԕ���Program�B
	 */
	public List<OnedayTimetable> getWeeklyTimeTable(String stationId, 
			AbstractHttpClient client) {
		return getWeeklyTimeTable(stationId, client, null);
	}
	
	/**
	 * �w�肵��Station�i1�Ǖ��j��
	 * ��T�ԕ���Program����donwload����filter��������B
	 * 
	 * @param stations Download����station��ID�B
	 * @param client HttpClient�C���X�^���X�B
	 * @param filter ���ʂɂ�����filter
	 * @return station�̈�T�ԕ���Program��filter�����������́B���s������null�B
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
