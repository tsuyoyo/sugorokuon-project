/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.radikoadaptation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AbstractHttpClient;

import tsuyogoro.sugorokuon.constant.Area;
import tsuyogoro.sugorokuon.constant.SugorokuonConst;
import tsuyogoro.sugorokuon.datatype.Station;

import android.util.Log;

public class StationListDownloader {

	private final static String UTF_8 = "UTF-8";
	
	private final static String XML_EXTENSION = ".xml";
	
	private final static String STATION_LIST_URL = 
		"http://radiko.jp/v2/station/list/";
	
	public StationListDownloader() {
		
	}

    /**
     * areaIdのリストに入っているareaに属する全てのラジオ局情報をdownloadする。
     * stationIdに重複はないよう、listを作成する。
     *
     * @param areaIds
     * @param logoSize
     * @param client
     * @return　downloadに失敗したらnullが返る。
     */
	public List<Station> getStationList(List<Area> areas, StationListParser.LogoSize logoSize,
			AbstractHttpClient client) {
		List<Station> stations = new ArrayList<Station>();		
		for(Area area : areas) {
			List<Station> areaStations = getStationList(area.id, logoSize, client);
			// Download�Ɏ��s�B
			if(null == areaStations) {
				stations = null;
				break;
			} else {
				addStationsWithoutDuplicate(stations, areaStations);				
			}
		}
		return stations;
	}

    /*
     * 重複が無いように、listに対してtoAddを足す。
     *
     */
	private void addStationsWithoutDuplicate(List<Station> list, List<Station> toAdd) {
		for(Station addCand : toAdd) {
			boolean isNew = true;
			for(Station s : list) {
				if(s.id.equals(addCand.id)) {
					isNew = false;
					continue;
				}
			}
			if(isNew) {
				list.add(addCand);
			}
		}
	}

    /**
     * areaIdで特定されるAreaのstation listをdownloadする。
     *
     * @param areaId
     * @param logoSize
     * @param client
     * @return downloadに失敗した場合はnullが返る。
     */
	public List<Station> getStationList(String areaId, StationListParser.LogoSize logoSize,
			AbstractHttpClient client) {
		List<Station> res = null;
		
		// URL to get station list of the area.
		String url = STATION_LIST_URL + areaId + XML_EXTENSION;
		HttpGet httpGet = new HttpGet(url);
		
		// Download station list(xml) and parse it.
		HttpResponse httpRes;
		try {
			// Download station XML data.
			httpRes = client.execute(httpGet);

            // Status codeが400以上ならばエラー。
            // 参考 ： http://www.studyinghttp.net/status_code
			int statusCode = httpRes.getStatusLine().getStatusCode();
			if(400 <= statusCode) {
				Log.e(SugorokuonConst.LOGTAG, "Failed to download station info : status code " + statusCode);
			} else {
                // サーバからのresponseをparse。
				InputStream stationData = httpRes.getEntity().getContent();
				StationListParser parser = new StationListParser(stationData, logoSize, UTF_8);				
				res = parser.parse();				
			}
		} catch(IOException e) {
			Log.e("SugoRokuon", "IOException at getStationList:" + e.getMessage());
		}
		
		return res;
	}
	
}
