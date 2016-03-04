package tsuyogoro.sugorokuon.network;

import java.util.List;

import tsuyogoro.sugorokuon.models.entities.OnedayTimetable;
import tsuyogoro.sugorokuon.models.entities.Station;

public interface TimeTableFetcher {

    /**
     * Weekly TimeTableの進捗を受け取るlistener (時間がかかるので)
     *
     */
    interface IWeeklyFetchProgressListener {
        /**
         *
         * @param fetched 取得が完了した分
         * @param requested fetchWeeklyTableメソッドに要求した分
         */
        void onProgress(List<Station> fetched, List<Station> requested);
    }

    /**
     * 指定したStation（複数局分）の一週間分のProgram情報をdownload
     * listenerを渡して、進捗を受け取ることができる
     *
     * @param stations Downloadするstation IDリスト。
     * @param progressListener 進捗を受け取る
     * @return
     */
    List<OnedayTimetable> fetchWeeklyTable(
            List<Station> stations, TimeTableFetcher.IWeeklyFetchProgressListener progressListener);

    /**
     * 指定したStation（複数局分）の一週間分のProgram情報をdownload
     *
     * @param stations Downloadするstation IDリスト。
     * @return stationの一週間分のProgramTableのリスト。
     */
    List<OnedayTimetable> fetchWeeklyTable(List<Station> stations);

    /**
     * 指定したStation（1局分）の一週間分のProgram情報をdownload
     *
     * @param stationId DownloadするstationのID。
     * @return stationの一週間分のProgramTableのリスト。失敗したらnull。
     */
    List<OnedayTimetable> fetchWeeklyTable(String stationId);

    /**
     * 指定したStation（1局分）の今日のProgram情報をdownload
     *
     * @param station Downloadするstation
     * @return stationの今日のProgramTable。失敗したらnull。
     */
    OnedayTimetable fetchTodaysTable(Station station);

    /**
     * 指定したStation（複数局分）の今日のProgram情報をdownload
     * 取得できたstationのTimeTableが返るが、(失敗したものがあった場合は
     * stationIdsの数より少ないリストが返る
     *
     * @param stations Downloadするstationのリスト
     * @return listインスタンス (nullは返らない)
     */
    List<OnedayTimetable> fetchTodaysTable(List<Station> stations);
}
