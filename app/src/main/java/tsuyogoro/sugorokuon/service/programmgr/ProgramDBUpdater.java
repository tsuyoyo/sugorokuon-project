/**
 * Copyright (c)
 * 2015 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.service.programmgr;

import android.content.Context;

import org.apache.http.impl.client.AbstractHttpClient;

import java.util.List;

import tsuyogoro.sugorokuon.datatype.OnedayTimetable;
import tsuyogoro.sugorokuon.datatype.Station;
import tsuyogoro.sugorokuon.feedback.googleanalytics.RecommendWordsSender;
import tsuyogoro.sugorokuon.database.ProgramDatabaseAccessor;
import tsuyogoro.sugorokuon.radikoadaptation.ProgramListDownloader;

class ProgramDBUpdater {

    /**
     * updateProgramDatabaseの進捗を受け取るためのInterface。
     **/
    static interface IUpdateProgressListener {
        public void onProgressUpdateProgram(int prog, int max);
    }

    private final Context mContext;

    private final AbstractHttpClient mHttpClient;

    private final ProgramDatabaseAccessor.IRecommender mRecommender;

    /**
     * コンストラクタ
     *
     * @param context
     * @param httpClient
     * @param recommender オススメ番組かどうかを決めるフィルタ
     */
    public ProgramDBUpdater(Context context, AbstractHttpClient httpClient,
                            ProgramDatabaseAccessor.IRecommender recommender) {
        mContext = context;
        mHttpClient = httpClient;
        mRecommender = recommender;
    }

    /**
     * Programの情報をネットワークから取りなおして、DBを更新する。
     *
     * @param targetStations
     * @param listener
     * @return 成功したらtrue、失敗したらfalse。
     */
    public boolean update(List<Station> targetStations, IUpdateProgressListener listener) {

        boolean res = true;

        // DBをクリア。
        ProgramDatabaseAccessor db = new ProgramDatabaseAccessor(mContext);
        db.clearAllProgramData();

        // 始める前に最初のprogress。
        listener.onProgressUpdateProgram(0, targetStations.size());

        // 各stationの１週間分の番組データをとり、DBへstoreしていく。
        ProgramListDownloader progDownloader = new ProgramListDownloader();

        for(int i=0; i<targetStations.size(); i++) {
            Station station = targetStations.get(i);

            List<OnedayTimetable> timeTable =
                    progDownloader.getWeeklyTimeTable(station.id, mHttpClient);

            if(null != timeTable) {
                // DownloadしたデータをDBへストアする。
                for(OnedayTimetable dayTimeTable : timeTable) {
                    db.insertOnedayTimetable(dayTimeTable, mRecommender);
                }

                // Progressを送る。
                listener.onProgressUpdateProgram(i+1, targetStations.size());
            } else {
                res = false;
                break;
            }
        }

        // 検索時のrecommend情報をGAに送る。
        RecommendWordsSender.send();

        return res;
    }

    public boolean update(Station targetStation) {

        boolean res = true;

        ProgramDatabaseAccessor db = new ProgramDatabaseAccessor(mContext);

        return res;
    }

}
