package tsuyogoro.sugorokuon.network.nhk;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.simpleframework.xml.core.Complete;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dagger.Component;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tsuyogoro.sugorokuon.models.entities.Station;
import tsuyogoro.sugorokuon.utils.SugorokuonLog;

public class NhkStationsFetcher {

    public static final String STATION_TYPE_NHK = "NHK";

    public List<Station> fetch() {
        Request request = new Request.Builder()
                .url(NhkConfigs.getServerUrl() + "/station/nhk")
                .get()
                .build();
        OkHttpClient client = new OkHttpClient();

        List<Station> stations = null;
        try {
            Response response = client.newCall(request).execute();

            if (response.code() != 200) {
                SugorokuonLog.w("NhkTimeTableFetcher : Failed to fetch station list, NHK cache server is unavailable");
                return null;
            }

            Gson gson = new Gson();
            stations = gson.fromJson(response.body().string(),
                    new TypeToken<List<Station>>() {}.getType());

            for (Station station : stations) {
                station.type = STATION_TYPE_NHK;
            }

        } catch (IOException e) {
            SugorokuonLog.e("IOException at fetching station data : " + e.getMessage());
        }

        if (stations == null) {
            stations = new ArrayList<>();
        }

        return stations;
    }

}
