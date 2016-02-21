/**
 * Copyright (c)
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network.radikoapi;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import tsuyogoro.sugorokuon.constants.Area;
import tsuyogoro.sugorokuon.constants.StationLogoSize;
import tsuyogoro.sugorokuon.models.entities.Feed;
import tsuyogoro.sugorokuon.models.entities.Station;
import tsuyogoro.sugorokuon.utils.SugorokuonLog;

public class StationsFetcher {

    public StationsFetcher() {
    }

    /**
     * areaIdのリストに入っているareaに属する全てのラジオ局情報をdownloadする。
     * stationIdに重複はないよう、listを作成する。
     *
     * @param areas
     * @param logoSize
     * @return　downloadに失敗したらnullが返る。
     */
    static public List<Station> fetch(Area[] areas, StationLogoSize logoSize) {

        List<Station> stations = new ArrayList<>();
        for (Area area : areas) {
            List<Station> areaStations = fetch(area.id, logoSize);
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
     * @return downloadに失敗した場合はnullが返る。
     */
    static public List<Station> fetch(String areaId, StationLogoSize logoSize) {

        StationApiClient api = new StationApiClient(new OkHttpClient());
        StationApiClient.StationList data = api.fetchStationList(areaId);

        if (data == null) {
            return null;
        }

        List<Station> result = new ArrayList<>();
        for (StationApiClient.StationList.Station s : data.stationList) {
            result.add(convertResponseToModel(s));
        }

        completeStationInfo(result);

        return result;
    }

    static private Station convertResponseToModel(StationApiClient.StationList.Station responseData) {
        Station.Builder builder = new Station.Builder();
        builder.ascii_name = responseData.ascii_name;
        builder.bannerUrl = responseData.banner;
        builder.id = responseData.id;
        builder.logoUrl = responseData.logo_large;
        builder.siteUrl = responseData.href;
        builder.name = responseData.name;
        return builder.create();
    }

    static private void completeStationInfo(List<Station> stations) {
        for (Station s : stations) {
            // OnAir曲情報を提供しているか
            Feed f = FeedFetcher.fetch(s.id);
            if (0 < f.onAirSongs.size()) {
                s.setOnAirSongsAvailable(true);
                SugorokuonLog.d(" - " + s.id + " : onAir info available");
            }

            // 局のlogoファイルを落としてしまっておく
            s.setLogoCachePath(StationLogoDownloader.download(s));
        }
    }

    static private void addStationsWithoutDuplicate(List<Station> list,
                                                    List<Station> toAdd) {
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

}
