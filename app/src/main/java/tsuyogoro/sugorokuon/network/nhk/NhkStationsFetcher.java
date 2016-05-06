package tsuyogoro.sugorokuon.network.nhk;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tsuyogoro.sugorokuon.constants.Area;
import tsuyogoro.sugorokuon.constants.StationLogoSize;
import tsuyogoro.sugorokuon.models.entities.Station;
import tsuyogoro.sugorokuon.network.IStationFetcher;
import tsuyogoro.sugorokuon.network.StationLogoDownloader;
import tsuyogoro.sugorokuon.utils.SugorokuonLog;

public class NhkStationsFetcher implements IStationFetcher {

    public static final String STATION_TYPE_NHK = "NHK";

    @Override
    public List<Station> fetch(Area[] areas, StationLogoSize logoSize, String logoCacheDir) {
        return doFetch(logoCacheDir);
    }

    @Override
    public List<Station> fetch(String areaId, StationLogoSize logoSize, String logoCacheDir) {
        return doFetch(logoCacheDir);
    }

    private List<Station> doFetch(String logoCacheDir) {
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

        completeStationInfo(stations, logoCacheDir);

        return stations;
    }

    private void completeStationInfo(List<Station> stations, String logoCacheDir) {
        for (Station s : stations) {
            s.setOnAirSongsAvailable(false);
            s.setLogoCachePath(StationLogoDownloader.download(s, logoCacheDir));
            s.type = STATION_TYPE_NHK;
        }
    }

}
