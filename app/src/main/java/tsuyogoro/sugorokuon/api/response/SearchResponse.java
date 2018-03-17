package tsuyogoro.sugorokuon.api.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class SearchResponse {

    @SerializedName("data")
    public List<Program> programs;

    static public class Program {
        @SerializedName("status")
        public String status;

        @SerializedName("title")
        public String title;

        @SerializedName("performer")
        public String personality;

        @SerializedName("station_id")
        public String stationId;

        @SerializedName("img")
        public String image;

        @SerializedName("start_time")
        public Date start;

        @SerializedName("end_time")
        public Date end;

        @SerializedName("program_url")
        public String url;

        @SerializedName("description")
        public String description;

        @SerializedName("info")
        public String info;
    }

}