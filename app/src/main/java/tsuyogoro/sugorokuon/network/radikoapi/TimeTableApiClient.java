package tsuyogoro.sugorokuon.network.radikoapi;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import tsuyogoro.sugorokuon.utils.SugorokuonLog;

class TimeTableApiClient {

    private final TimeTableApiService mTimeTableApiService;

    public interface TimeTableApiService {
        @GET("v2/api/program/station/weekly")
        Call<TimeTableRoot> getWeeklyTimeTable(@Query("station_id") String stationId);

        @GET("v2/api/program/station/today")
        Call<TimeTableRoot> getTimeTable(@Query("station_id") String stationId);
    }

    public TimeTableApiClient(OkHttpClient client) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RadikoApiCommon.API_ROOT)
                .client(client)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        mTimeTableApiService = retrofit.create(TimeTableApiService.class);
    }

    /**
     *
     * @param stationId
     * @return 失敗したらnull
     */
    public TimeTableRoot fetchWeeklyTimeTable(String stationId) {
        try {
            return mTimeTableApiService.getWeeklyTimeTable(stationId).execute().body();
        } catch (IOException e) {
            SugorokuonLog.e("Failed to fetch Weekly timetable : " + e.getMessage());
        }
        return null;
    }

    /**
     *
     * @param stationId
     * @return 失敗したらnull
     */
    public TimeTableRoot fetchTodaysTimeTable(String stationId) {
        try {
            return mTimeTableApiService.getTimeTable(stationId).execute().body();
        } catch (IOException e) {
            SugorokuonLog.e("Failed to fetch Today's timetable : " + e.getMessage());
        }
        return null;
    }

    @Root
    public static class TimeTableRoot {

        @Element
        public int ttl;

        // 応答を返してきたサーバの時刻?
        @Element
        public long srvtime;

        @ElementList
        public List<Station> stations;

        @Root(name = "station")
        public static class Station {

            @Attribute
            public String id;

            @Element
            public String name;

            @ElementList(name = "scd")
            public List<OnedayTimetable> timetables;

            @Root(name = "progs")
            public static class OnedayTimetable {
                @Element
                public String date;

                @ElementList(inline = true)
                public List<Program> programs;

                @Root(name = "prog")
                public static class Program {

                    // Formatted yyyyMMddhhmmss
                    @Attribute(name = "ft")
                    public String startTime;

                    // Formatted yyyyMMddhhmmss
                    @Attribute(name = "to")
                    public String endTime;

                    @Attribute(name = "ftl")
                    public String startTimeHHmm;

                    @Attribute(name = "tol")
                    public String endTimeHHmm;

                    @Attribute(name = "dur")
                    public String durationInSec;

                    @Element
                    public String title;

                    @Element(required = false)
                    public String sub_title;

                    @Element(name = "pfm", required = false)
                    public String personality;

                    @Element(required = false)
                    public String desc;

                    @Element(required = false)
                    public String info;

                    @Element(required = false)
                    public String url;

                    @ElementList(required = false)
                    public List<Meta> metas;

                    @Root(name = "meta")
                    public static class Meta {
                        @Attribute
                        public String name;

                        @Attribute
                        public String value;
                    }
                }
            }
        }
    }
}