/**
 * Copyright (c)
 * 2016 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network.radikoapi;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.Text;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.transform.RegistryMatcher;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import tsuyogoro.sugorokuon.utils.SugorokuonLog;

class FeedApiClient {

    // TODO : 載せるかどうか考える
//	http://radiko.jp/v3/feed/pc/sns/INT.xml

    private final FeedApiService mFeedApiService;

    public FeedApiClient(OkHttpClient client) {

        // 日付のフィールドを、Serializer内でparseしてCalendar型にしてしまうための設定
        RegistryMatcher registryMatcher = new RegistryMatcher();
        registryMatcher.bind(Calendar.class, new ApiDateConverter());
        Serializer serializer = new Persister(registryMatcher);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RadikoApiCommon.API_ROOT)
                .client(client)
                .addConverterFactory(SimpleXmlConverterFactory.create(serializer))
                .build();

        mFeedApiService = retrofit.create(FeedApiService.class);
    }

    public interface FeedApiService {
        @GET("v3/feed/pc/noa/{stationId}.xml")
        Call<NowOnAir> getNowOnAirSongs(@Path("stationId") String stationId);

        @GET("v3/feed/pc/cm/{stationId}.xml")
        Call<Cm> getCm(@Path("stationId") String stationId);
    }

    /**
     *
     * @param stationId e.g.) INTとかTFMとか
     * @return 失敗したらnull
     */
    public NowOnAir fetchNowOnAirSongs(String stationId) {

        try {
            return mFeedApiService.getNowOnAirSongs(stationId).execute().body();
        } catch (IOException e) {
            SugorokuonLog.e("Failed to fetch onAir songs list : " + e.getMessage());
        }

        return null;
    }

    /**
     *
     * @param stationId e.g.) INTとかTFMとか
     * @return 失敗したらnull
     */
    public Cm fetchCm(String stationId) {

        try {
            return mFeedApiService.getCm(stationId).execute().body();
        } catch (IOException e) {
            SugorokuonLog.e("Failed to fetch CM : " + e.getMessage());
        }

        return null;
    }

    @Root
    public static class Station {
        @Attribute(name = "id")
        public String id;

        @Text
        public String station;
    }

    @Root
    public static class NowOnAir {

        @Element(name = "station")
        public Station station;

        @ElementList(name = "noa")
        public List<Item> onAirSongs;

        @Root(name = "item")
        public static class Item {

            @Attribute
            public String artist;

            @Attribute
            public String title;

            @Attribute(required = false)
            public String evid;

            @Attribute
            public String img;

            @Attribute
            public String img_large;

            @Attribute
            public String itemid;

            @Attribute
            public String amazon;

            @Attribute
            public String itunes;

            @Attribute
            public String recochoku;

            @Attribute
            public String program_title;

            @Attribute
            public Calendar stamp;

        }
    }

    public static class Cm {
        @Element(name = "station")
        public Station station;

        @ElementList(name = "cm")
        public List<Item> items;

        @Element(name = "item")
        public static class Item {
            @Attribute(name = "desc")
            public String description;

            @Attribute
            public String evid;

            @Attribute
            public String href;

            @Attribute
            public String img;

            @Attribute
            public String itemid;

            @Attribute
            public Calendar stamp;
        }
    }

}
