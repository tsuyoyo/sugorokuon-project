/**
 * Copyright (c)
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network.radikoapi;

import java.util.ArrayList;
import java.util.List;

import tsuyogoro.sugorokuon.constants.Area;
import tsuyogoro.sugorokuon.constants.StationLogoSize;
import tsuyogoro.sugorokuon.models.entities.Feed;
import tsuyogoro.sugorokuon.models.entities.Station;
import tsuyogoro.sugorokuon.network.OkHttpWrapper;
import tsuyogoro.sugorokuon.network.IStationFetcher;
import tsuyogoro.sugorokuon.network.StationLogoDownloader;
import tsuyogoro.sugorokuon.network.gtm.SugorokuonTagManagerWrapper;
import tsuyogoro.sugorokuon.utils.SugorokuonLog;

public class RadikoStationsFetcher implements IStationFetcher {

    public static final String RADIKO_STATION_TYPE = "radiko";

    private RadikoFeedFetcher mFeedFetcher;

    public RadikoStationsFetcher() {
        mFeedFetcher = new RadikoFeedFetcher();
    }

    @Override
    public List<Station> fetch(Area[] areas, StationLogoSize logoSize, String logoCacheDir) {

        List<Station> stations = new ArrayList<>();
        for (Area area : areas) {
            List<Station> areaStations = fetch(area.id, logoSize, logoCacheDir);
            if (null == areaStations) {
                stations = null;
                break;
            } else {
                addStationsWithoutDuplicate(stations, areaStations);
            }
        }

        return stations;
    }

    @Override
    public List<Station> fetch(String areaId, StationLogoSize logoSize, String logoCacheDir) {

        StationApiClient api = new StationApiClient(OkHttpWrapper.buildClient());
        StationApiClient.StationList data = api.fetchStationList(areaId);

        if (data == null) {
            return null;
        }

        List<Station> result = new ArrayList<>();
        for (StationApiClient.StationList.Station s : data.stationList) {
            result.add(convertResponseToModel(s));
        }

        completeStationInfo(result, logoCacheDir);

        return result;
    }

    private Station convertResponseToModel(StationApiClient.StationList.Station responseData) {
        Station.Builder builder = new Station.Builder();
        builder.ascii_name = responseData.ascii_name;
        builder.bannerUrl = responseData.banner;
        builder.id = responseData.id;
        builder.logoUrl = responseData.logo_large;
        builder.siteUrl = responseData.href;
        builder.name = responseData.name;
        builder.frequencyToListAd = SugorokuonTagManagerWrapper.getRadikoTimetableAdFrequency();
        return builder.create();
    }

    private void completeStationInfo(List<Station> stations, String logoCacheDir) {

        for (Station s : stations) {
            // OnAir曲情報を提供しているか
            Feed f = mFeedFetcher.fetch(s.id);
            if (f != null && 0 < f.onAirSongs.size()) {
                s.setOnAirSongsAvailable(true);
                SugorokuonLog.d(" - " + s.id + " : onAir info available");
            }

            // 局のlogoファイルを落としてしまっておく
            s.setLogoCachePath(StationLogoDownloader.download(s, logoCacheDir));

            s.type = RADIKO_STATION_TYPE;
        }
    }

    private void addStationsWithoutDuplicate(List<Station> list,
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
