package tsuyogoro.sugorokuon.network.nhk;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tsuyogoro.sugorokuon.models.entities.OnedayTimetable;
import tsuyogoro.sugorokuon.models.entities.Program;
import tsuyogoro.sugorokuon.models.entities.Station;
import tsuyogoro.sugorokuon.network.ITimeTableFetcher;
import tsuyogoro.sugorokuon.utils.SugorokuonLog;

public class NhkTimeTableFetcher {

    public static final int FETCH_TODAY = 0;

    public static final int FETCH_TOMORROW = 1;

    // TODO : ちゃんと実装する
    public List<OnedayTimetable> fetchToday(String areaId, List<Station> stations) {

        List<OnedayTimetable> timetables = new ArrayList<>();

        for (Station station : stations) {
            if (!station.type.equals(NhkStationsFetcher.STATION_TYPE_NHK)) {
                continue;
            }

            Calendar today = Calendar.getInstance();
            if (today.get(Calendar.HOUR_OF_DAY) < 5) {
                today.add(Calendar.DATE, -1);
            }

            timetables.add(fetch(today, FETCH_TODAY, areaId, station.id));
        }

        return timetables;
    }

    /**
     * 今週のNHKの番組表を可能な限り取得 (今日と明日のみ、今のところ取得可能)
     *
     * @param areaId
     * @param targetStations
     * @param fetchedStationsNum
     * @param progressListener
     * @return
     */
    public List<OnedayTimetable> fetchThisWeek(
            String areaId, List<Station> targetStations, int fetchedStationsNum,
            ITimeTableFetcher.IWeeklyFetchProgressListener progressListener) {

        List<OnedayTimetable> timetables = new ArrayList<>();

        int fetchedInThisMethod = 0;
        for (Station station : targetStations) {
            if (!station.type.equals(NhkStationsFetcher.STATION_TYPE_NHK)) {
                continue;
            }

            // 今日の番組表
            Calendar today = Calendar.getInstance();
            if (today.get(Calendar.HOUR_OF_DAY) < 5) {
                today.add(Calendar.DATE, -1);
            }
            timetables.add(fetch(today, FETCH_TODAY, areaId, station.id));

            // 明日の番組表
            today.add(Calendar.DATE, 1);
            timetables.add(fetch(today, FETCH_TOMORROW, areaId, station.id));

            // 進捗を送る
            fetchedInThisMethod++;
            progressListener.onProgress(
                    fetchedStationsNum + fetchedInThisMethod, targetStations.size());
        }

        return timetables;
    }

    private OnedayTimetable fetch(Calendar date, int whenToFetch, String areaId, String stationId) {

        String apiUrl = NhkConfigs.getServerUrl() + "/program/nhk/" +
                ((whenToFetch == FETCH_TODAY) ? "today/" : "tomorrow/") +
                areaId + "/" + stationId + "/";

        Request request = new Request.Builder().url(apiUrl).get().build();
        OkHttpClient client = new OkHttpClient();

        List<Program> programs = new ArrayList<>();
        try {
            Response response = client.newCall(request).execute();

            if (response.code() != 200) {
                SugorokuonLog.w("NhkTimeTableFetcher : Failed to fetch timetable, NHK cache server is unavailable");
                return null;
            }

            JSONArray programsInJson = new JSONArray(response.body().string());

            for (int i = 0; i < programsInJson.length(); i++) {
                JSONObject programInJson = programsInJson.getJSONObject(i);

                // NHKの番組表は限られた情報しか入っていないので、手動でparse。
                long startTime = programInJson.getLong("startTime");
                long endTime = programInJson.getLong("endTime");
                String title = programInJson.getString("title");
                String description = programInJson.getString("description");

                Calendar startTimeCal = Calendar.getInstance();
                startTimeCal.setTimeInMillis(startTime);

                Calendar endTimeCal = Calendar.getInstance();
                endTimeCal.setTimeInMillis(endTime);

                Program.Builder builder = new Program.Builder();
                builder.stationId = stationId;
                builder.startTime = startTimeCal;
                builder.endTime = endTimeCal;
                builder.title = title;
                builder.description = description;

                programs.add(builder.create());
            }

        } catch (IOException e) {
            SugorokuonLog.e("IOException at fetching programs data : " + e.getMessage());
        } catch (JSONException e) {
            SugorokuonLog.e("JSONException at parsing programs data : " + e.getMessage());
        }

        OnedayTimetable timetable = new OnedayTimetable(date, stationId);
        timetable.programs = programs;
        timetable.isShowAd = true; // サーバ代かかってるし、NHKは広告たくさん出す

        return timetable;
    }

}
