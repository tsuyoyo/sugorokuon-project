/**
 * Copyright (c)
 * 2016 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network.radikoapi;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import tsuyogoro.sugorokuon.utils.SugorokuonLog;

class StationApiClient {

    private final StationApiService mStationApiService;

    public StationApiClient(OkHttpClient client) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RadikoApiCommon.API_ROOT)
                .client(new OkHttpClient())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        mStationApiService = retrofit.create(StationApiService.class);
    }

    public interface StationApiService {
        @GET("v2/station/list/{areaId}.xml")
        Call<StationList> getStations(@Path("areaId") String areaId);
    }

    public StationList fetchStationList(String areaId) {

        try {
            return mStationApiService.getStations(areaId).execute().body();
        } catch (IOException e) {
            SugorokuonLog.e("Failed to fetch station list : " + e.getMessage());
        }

        return null;
    }

    //<stations area_id="JP13" area_name="TOKYO JAPAN">
    @Root(strict = false)
    public static class StationList {

        @ElementList(inline = true)
        public List<Station> stationList;

        @Attribute
        public String area_id;

        @Attribute
        public String area_name;

        @Root(name = "station", strict = false)
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

            @Root(name = "logo", strict = false)
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
