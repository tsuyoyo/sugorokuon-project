/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network.radikoadaptation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AbstractHttpClient;

import tsuyogoro.sugorokuon.models.entities.OnedayTimetable;
import tsuyogoro.sugorokuon.models.entities.Station;
import tsuyogoro.sugorokuon.utils.SugorokuonLog;

import android.net.Uri;
import android.util.Log;

/**
 * Reference
 * http://www.dcc-jpl.com/foltia/wiki/radikomemo
 *
 * @author Tsuyoyo
 *
 */
public class TimeTableFetcher {

	private final static String UTF_8 = "UTF-8";

    private final static String HOST = "radiko.jp";

	private final static String API_WEEKLY_PROGRAM = "weekly";

    private final static String API_TODAY_PROGRAM = "today";
	
	private final static String QUERY_STATION_ID = "station_id";

	private TimeTableFetcher() {
		
	}

    /**
     * Weekly TimeTableの進捗を受け取るlistener (時間がかかるので)
     *
     */
    public static interface IWeeklyFetchProgressListener {
        /**
         *
         * @param fetched 取得が完了した分
         * @param requested fetchWeeklyTableメソッドに要求した分
         */
        public void onProgress(List<Station> fetched, List<Station> requested);
    }

    /**
     * 指定したStation（複数局分）の一週間分のProgram情報をdownload
     *
     * @param stations Downloadするstation IDリスト。
     * @param client HttpClientインスタンス。
     * @return stationの一週間分のProgramTableのリスト。
     */
    public static List<OnedayTimetable> fetchWeeklyTable(
            List<Station> stations, AbstractHttpClient client) {

        return fetchWeeklyTable(stations, client, null);
    }

    /**
     * 指定したStation（複数局分）の一週間分のProgram情報をdownload
     * listenerを渡して、進捗を受け取ることができる
     *
     * @param stations Downloadするstation IDリスト。
     * @param client HttpClientインスタンス。
     * @param progressListener 進捗を受け取る
     * @return
     */
	public static List<OnedayTimetable> fetchWeeklyTable(
            List<Station> stations, AbstractHttpClient client, IWeeklyFetchProgressListener progressListener) {

        ArrayList<OnedayTimetable> programs = new ArrayList<OnedayTimetable>();
        ArrayList<Station> fetchedStations = new ArrayList<Station>();

		for(Station station : stations) {
			programs.addAll(fetchWeeklyTable(station.id, client));

            if (null != progressListener) {
                fetchedStations.add(station);
                progressListener.onProgress(fetchedStations, stations);
            }
		}
		return programs;		
	}

    /**
     * 指定したStation（1局分）の一週間分のProgram情報をdownload
     *
     * @param stationId DownloadするstationのID。
     * @param client HttpClientインスタンス。
     * @return stationの一週間分のProgramTableのリスト。失敗したらnull。
     */
	public static List<OnedayTimetable> fetchWeeklyTable(String stationId,
                                                         AbstractHttpClient client) {

        return doFetchTimeTable(stationId, API_WEEKLY_PROGRAM, client);
	}

    /**
     * 指定したStation（1局分）の今日のProgram情報をdownload
     *
     * @param station Downloadするstation
     * @param client HttpClientインスタンス。
     * @return stationの今日のProgramTable。失敗したらnull。
     */
    public static OnedayTimetable fetchTodaysTable(Station station,
                                                   AbstractHttpClient client) {

        List<OnedayTimetable> tables = doFetchTimeTable(station.id, API_TODAY_PROGRAM, client);

        if (null != tables) {
            return tables.get(0);
        } else {
            SugorokuonLog.w("Failed to get today's time table : " + station.ascii_name);
            return null;
        }
    }

    /**
     * 指定したStation（複数局分）の今日のProgram情報をdownload
     * 取得できたstationのTimeTableが返るが、(失敗したものがあった場合は
     * stationIdsの数より少ないリストが返る
     *
     * @param stations Downloadするstationのリスト
     * @param client HttpClientインスタンス。
     * @return listインスタンス (nullは返らない)
     */
    public static List<OnedayTimetable> fetchTodaysTable(List<Station> stations,
                                                         AbstractHttpClient client) {
        List<OnedayTimetable> tables = new ArrayList<OnedayTimetable>();

        for (Station station : stations) {
            OnedayTimetable timeTable = fetchTodaysTable(station, client);
            if (null != timeTable) {
                tables.add(timeTable);
            }
        }
        return tables;
    }


    private static List<OnedayTimetable> doFetchTimeTable(
            String stationId, String api, AbstractHttpClient client) {

        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("http").authority(HOST)
                .appendPath("v2")
                .appendPath("api")
                .appendPath("program")
                .appendPath("station")
                .appendPath(api).appendQueryParameter(QUERY_STATION_ID, stationId);
//        String url = api + "?" + QUERY_STATION_ID + "=" + stationId;

        HttpGet httpGet = new HttpGet(uriBuilder.build().toString());

        // Download program list(xml) and parse it.
        HttpResponse httpRes;
        try {
            // Download program XML data.
            httpRes = client.execute(httpGet);

            // Parse the response.
            InputStream programData = httpRes.getEntity().getContent();
            ProgramResponseParser parser = new ProgramResponseParser(programData, UTF_8);

            return parser.parse();

        } catch(IOException e) {
            Log.e("SugoRokuon", "IOException at getProgramList:" + e.getMessage());
        }

        return null;
    }
	
}
