/**
 * Copyright (c)
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network.radikoapi;

import java.util.ArrayList;
import java.util.List;

import tsuyogoro.sugorokuon.models.entities.OnedayTimetable;
import tsuyogoro.sugorokuon.models.entities.Program;
import tsuyogoro.sugorokuon.models.entities.Station;
import tsuyogoro.sugorokuon.network.ITimeTableFetcher;
import tsuyogoro.sugorokuon.network.OkHttpWrapper;
import tsuyogoro.sugorokuon.utils.SugorokuonLog;

/**
 * Reference
 * http://www.dcc-jpl.com/foltia/wiki/radikomemo
 *
 * @author Tsuyoyo
 *
 */
public class RadikoTimeTableFetcher implements ITimeTableFetcher {

    private final static String API_WEEKLY_PROGRAM = "weekly";

    private final static String API_TODAY_PROGRAM = "today";

    public RadikoTimeTableFetcher() {
    }

    @Override
    public List<OnedayTimetable> fetchWeeklyTable(List<Station> stations) {

        return fetchWeeklyTable(stations, null);
    }

    @Override
    public List<OnedayTimetable> fetchWeeklyTable(
            List<Station> stations, ITimeTableFetcher.IWeeklyFetchProgressListener progressListener) {

        ArrayList<OnedayTimetable> programs = new ArrayList<OnedayTimetable>();
        ArrayList<Station> fetchedStations = new ArrayList<Station>();

        for (Station station : stations) {

            if (!station.type.equals(RadikoStationsFetcher.RADIKO_STATION_TYPE)) {
                continue;
            }

            programs.addAll(fetchWeeklyTable(station.id));

            if (null != progressListener) {
                fetchedStations.add(station);
                progressListener.onProgress(fetchedStations.size(), stations.size());
            }
        }
        return programs;
    }

    @Override
    public List<OnedayTimetable> fetchWeeklyTable(String stationId) {

        return doFetchTimeTable(stationId, API_WEEKLY_PROGRAM);
    }

    @Override
    public OnedayTimetable fetchTodaysTable(Station station) {

        List<OnedayTimetable> tables = doFetchTimeTable(station.id, API_TODAY_PROGRAM);

        if (null != tables) {
            return tables.get(0);
        } else {
            SugorokuonLog.w("Failed to get today's time table : " + station.ascii_name);
            return null;
        }
    }

    @Override
    public List<OnedayTimetable> fetchTodaysTable(List<Station> stations) {
        List<OnedayTimetable> tables = new ArrayList<OnedayTimetable>();

        for (Station station : stations) {

            if (!station.type.equals(RadikoStationsFetcher.RADIKO_STATION_TYPE)) {
                continue;
            }

            OnedayTimetable timeTable = fetchTodaysTable(station);
            if (null != timeTable) {
                tables.add(timeTable);
            }
        }
        return tables;
    }

    private List<OnedayTimetable> doFetchTimeTable(String stationId, String apiName) {

        TimeTableApiClient api = new TimeTableApiClient(OkHttpWrapper.buildClient());
        TimeTableApiClient.TimeTableRoot dataFromRadiko = null;
        switch (apiName) {
            case API_TODAY_PROGRAM:
                dataFromRadiko = api.fetchTodaysTimeTable(stationId);
                break;
            case API_WEEKLY_PROGRAM:
                dataFromRadiko = api.fetchWeeklyTimeTable(stationId);
                break;
        }

        if (dataFromRadiko == null) {
            SugorokuonLog.w("Failed to fetch : " + apiName + " programs - station = " + stationId);
            return null;
        }

        return convertAppModel(dataFromRadiko);
    }

    private List<OnedayTimetable> convertAppModel(TimeTableApiClient.TimeTableRoot apiResponse) {
        List<OnedayTimetable> res = new ArrayList<>();

        for (TimeTableApiClient.TimeTableRoot.Station s : apiResponse.stations) {

            for (TimeTableApiClient.TimeTableRoot.Station.TimeTable t : s.timetables) {
                OnedayTimetable onedayTimetable = new OnedayTimetable(t.date, s.id);
                onedayTimetable.programs = new ArrayList<>();

                for (TimeTableApiClient.TimeTableRoot.Station.TimeTable.Program p : t.programs) {
                    Program.Builder builder = new Program.Builder();

                    builder.startTime = p.startTime;
                    builder.endTime = p.endTime;
                    builder.url = p.url;
                    builder.stationId = s.id;

                    if (p.desc != null) {
                        builder.description = AlphabetNormalizer.zenkakuToHankaku(p.desc);
                    }
                    if (p.info != null) {
                        builder.info = AlphabetNormalizer.zenkakuToHankaku(p.info);
                    }
                    if (p.personality != null) {
                        builder.personalities = AlphabetNormalizer.zenkakuToHankaku(p.personality);
                    }
                    if (p.sub_title != null) {
                        builder.subtitle = AlphabetNormalizer.zenkakuToHankaku(p.sub_title);
                    }
                    if (p.title != null) {
                        builder.title = AlphabetNormalizer.zenkakuToHankaku(p.title);
                    }

                    onedayTimetable.programs.add(builder.create());
                }

                res.add(onedayTimetable);
            }
        }

        return res;
    }

}
