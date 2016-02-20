/**
 * Copyright (c)
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network.radikoadaptation;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
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

        List<ResponseStations.Station> data = doFetch(areaId);
        if (data == null) {
            return null;
        }

        List<Station> result = new ArrayList<>();
        for (ResponseStations.Station s : data) {
            result.add(convertResponseToModel(s));
        }

        completeStationInfo(result);

        return result;
    }

    static private Station convertResponseToModel(ResponseStations.Station responseData) {
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

    public interface StationFetchService {
        @GET("v2/station/list/{areaId}.xml")
        Call<ResponseStations> getStations(@Path("areaId") String areaId);
    }

    private static List<ResponseStations.Station> doFetch(String areaId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://radiko.jp")
                .client(new OkHttpClient())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        Call<ResponseStations> stations =
                retrofit.create(StationFetchService.class).getStations(areaId);

        List<ResponseStations.Station> result = null;
        try {
            result = stations.execute().body().stationList;
        } catch (IOException e) {
            SugorokuonLog.e("Failed to fetch station list : " + e.getMessage());
        }

        return result;
    }

    //<stations area_id="JP13" area_name="TOKYO JAPAN">
    @Root
    public static class ResponseStations {

        @ElementList(inline = true)
        public List<Station> stationList;

        @Attribute
        public String area_id;

        @Attribute
        public String area_name;

        public static class Station {

            @Element
            public String id;

            @Element
            public String name;

            @Element
            public String ascii_name;

            @Element
            public String href;

            @Element
            public String logo_xsmall;

            @Element
            public String logo_small;

            @Element
            public String logo_medium;

            @Element
            public String logo_large;

            @ElementList(inline = true)
            public List<Logo> logos;

            public static class Logo {
                @Attribute
                public int width;

                @Attribute
                public int height;

                @Text
                public String value;
            }

            @Element
            public String feed;

            @Element
            public String banner;
        }

    }

}
