/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network.radikoadaptation;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AbstractHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import tsuyogoro.sugorokuon.constants.Area;
import tsuyogoro.sugorokuon.constants.StationLogoSize;
import tsuyogoro.sugorokuon.models.entities.Feed;
import tsuyogoro.sugorokuon.models.entities.Station;
import tsuyogoro.sugorokuon.utils.SugorokuonLog;

public class StationsFetcher {

    private final static String UTF_8 = "UTF-8";

    private final static String XML_EXTENSION = ".xml";

    private final static String STATION_LIST_URL = "http://radiko.jp/v2/station/list/";

    private StationsFetcher() {
        // インスタンス作らない
    }

    /**
     * areaIdのリストに入っているareaに属する全てのラジオ局情報をdownloadする。
     * stationIdに重複はないよう、listを作成する。
     *
     * @param areas
     * @param logoSize
     * @param client
     * @return　downloadに失敗したらnullが返る。
     */
    static public List<Station> fetch(Area[] areas, StationLogoSize logoSize,
                                      AbstractHttpClient client) {

        List<Station> stations = new ArrayList<Station>();
        for (Area area : areas) {
            List<Station> areaStations = fetch(area.id, logoSize, client);
            if (null == areaStations) {
                stations = null;
                break;
            } else {
                addStationsWithoutDuplicate(stations, areaStations);
            }
        }

        return stations;
    }

    /**
     * areaIdで特定されるAreaのstation listをdownloadする。
     *
     * @param areaId
     * @param logoSize
     * @param client
     * @return downloadに失敗した場合はnullが返る。
     */
    static public List<Station> fetch(String areaId, StationLogoSize logoSize,
                                      AbstractHttpClient client) {
        List<Station> res = null;

        // URL to get station list of the area.
        String url = STATION_LIST_URL + areaId + XML_EXTENSION;
        HttpGet httpGet = new HttpGet(url);

        // Download station list(xml) and parse it.
        try {
            HttpResponse httpRes = client.execute(httpGet);

            int statusCode = httpRes.getStatusLine().getStatusCode();
            if (400 <= statusCode) {
                SugorokuonLog.e("Failed to download station info : status code " + statusCode);
            } else {
                InputStream stationData = httpRes.getEntity().getContent();

                StationListResponseParser parser =
                        new StationListResponseParser(stationData, logoSize, UTF_8);
                res = parser.parse();

                stationData.close();
            }

            completeStationInfo(res, client);

        } catch (IOException e) {
            SugorokuonLog.e("IOException at fetching station :" + e.getMessage());
        }

        return res;
    }

    static private void completeStationInfo(List<Station> stations, AbstractHttpClient client) {
        for (Station s : stations) {
            // OnAir曲情報を提供しているか
            Feed f = FeedFetcher.fetch(s.id, client);
            if (0 < f.onAirSongs.size()) {
                s.setOnAirSongsAvailable(true);
                SugorokuonLog.d(" - " + s.id + " : onAir info available");
            }

            // 局のlogoファイルを落としてしまっておく
            s.setLogoCachePath(StationLogoDownloader.download(s, client));
        }
    }

    static private void addStationsWithoutDuplicate(List<Station> list, List<Station> toAdd) {
        for (Station addCand : toAdd) {
            boolean isNew = true;

            for (Station s : list) {
                if (s.id.equals(addCand.id)) {
                    isNew = false;
                    continue;
                }
            }

            if (isNew) {
                list.add(addCand);
            }
        }
    }

    public interface IOnGetStationListener {

        public void onGet(List<Station> stations);

    }

    /**
     * 非同期で指定したエリアのstationリストを取得
     * <p/>
     * Memo 2/22 :
     * Volley使って速くなると思ったら、同期版より遅くなったので使うかどうか保留。
     * UStationDownloaderTestの中で時間計測するコードを入れた。
     *
     * @param areaId
     * @param logoSize
     * @param listener
     * @param requestQueue
     */
    static public void fetchAsync(final String areaId, final StationLogoSize logoSize,
                                  final IOnGetStationListener listener, RequestQueue requestQueue) {

        // URL to get station list of the area.
        String url = STATION_LIST_URL + areaId + XML_EXTENSION;

        Response.Listener<InputStream> successListener = new Response.Listener<InputStream>() {
            @Override
            public void onResponse(InputStream response) {
                StationListResponseParser parser = new StationListResponseParser(response, logoSize, UTF_8);
                listener.onGet(parser.parse());
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                SugorokuonLog.e("Failed to get station (area : " + areaId + ")");
                listener.onGet(null);
            }
        };

        InputStreamRequest request = new InputStreamRequest(url, successListener, errorListener);

        requestQueue.add(request);
    }

}
