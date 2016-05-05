package tsuyogoro.sugorokuon.network.nhk;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tsuyogoro.sugorokuon.models.entities.OnedayTimetable;
import tsuyogoro.sugorokuon.models.entities.Program;
import tsuyogoro.sugorokuon.models.entities.Station;
import tsuyogoro.sugorokuon.utils.SugorokuonLog;

public class NhkTimeTableFetcher {

    public static final int FETCH_TODAY = 0;

    public static final int FETCH_TOMORROW = 1;

    public OnedayTimetable fetchToday() {
        return null;
    }

    public List<OnedayTimetable> fetchThisWeek() {
        return null;
    }

    // TODO: サーバ落ちてても大丈夫なようにする
    public OnedayTimetable fetch(Calendar date, int whenToFetch, String areaId, String stationId) {

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

            for (int i=0; i < programsInJson.length(); i++) {
                JSONObject programInJson = programsInJson.getJSONObject(i);

                // NHKの番組表は限られた情報しか入っていないので、手動でparse。
                long startTime = programInJson.getLong("startTime");
                long endTime = programInJson.getLong("endTime");
                String title = programInJson.getString("title");
                String description = programInJson.getString("description");

                Calendar startTimeCal = Calendar.getInstance();
                startTimeCal.setTimeInMillis(startTime);

                Calendar endTimeCal = Calendar.getInstance();
                startTimeCal.setTimeInMillis(endTime);

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

        return timetable;
    }

}
