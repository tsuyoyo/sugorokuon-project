/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.viewflow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.impl.client.DefaultHttpClient;

import tsuyogoro.sugorokuon.datatype.Station;
import tsuyogoro.sugorokuon.radikoadaptation.StationListParser.LogoSize;
import tsuyogoro.sugorokuon.settings.UpdatedDateManager;
import tsuyogoro.sugorokuon.viewflow.ProgramDataManager.IUpdateProgressListener;
import android.content.Context;
import android.os.AsyncTask;

public class DataViewFlow extends ViewFlowBase {

    /**
     *  onProgressの第1引数(whatsRunning)に使われる定数。
     *  invokeLoadDataをcallした後からCOMPLETE_DATA_UPDATECOMPLETEが来るまでの間。
     */
    public static final int PROGRESS_LOAD_DATA = 0;

    private static final LogoSize LOGO_SIZE = LogoSize.LARGE;

    private static DataViewFlow sInstance;

    // Station情報。
    private StationDataManager mStationDataMgr;

    // Program情報。
    private ProgramDataManager mProgramDataMgr;

    /*
     * メンバにStation情報、Program情報を読み込ませるためのAsyncTask.
     */
    private class LoadDataTask extends AsyncTask<Context, Integer, ViewFlowEvent>
            implements IUpdateProgressListener {

        @Override
        protected ViewFlowEvent doInBackground(Context... params) {
            ViewFlowEvent res = ViewFlowEvent.COMPLETE_DATA_UPDATECOMPLETE;
            Context context = params[0];

            boolean shouldDataUpdated = shouldDataUpdated(context);

            // StationデータをmStationDataMgrの中にloadする。
            StationDataManager stationDataMgr = new StationDataManager(context);
            ViewFlowEvent stationLoadRes = stationDataMgr.loadData(context,
                    shouldDataUpdated, LOGO_SIZE, new DefaultHttpClient());

            // Stationデータの取得に失敗したらreturnする。
            if(!ViewFlowEvent.COMPLETE_STATION_UPDATE.equals(stationLoadRes)) {
                return stationLoadRes;
            }

            // ProgramデータをmProgramDataMgrの中にload。
            if(shouldDataUpdated) {
                DefaultHttpClient client = new DefaultHttpClient();
                List<Station> stations = stationDataMgr.getStationInfo();
                if(ViewFlowEvent.FAILED_DATA_UPDATE.equals(
                        mProgramDataMgr.updateProgramDatabase(
                                context, stations, client , this))) {
                    return ViewFlowEvent.FAILED_DATA_UPDATE;
                }
            }

            // 初期フォーカスはオススメ番組（で、まだ放送開始してないもの）
            Calendar now = Calendar.getInstance(Locale.JAPAN);
            mProgramDataMgr.loadRecommendProgramsNotOnAirYet(context, now);

            // Networkからデータの更新を行ったのならば、LastUpdatedDateを更新
            if(shouldDataUpdated) {
                UpdatedDateManager.getInstance(context).updateLastUpdate();
            }

            // 完了のタイミングでStationDataMgrメンバを更新。
            // mStationDataMgrがnullか否かでloadが終わったかの判定なので。
            mStationDataMgr = stationDataMgr;

            return res;
        }

        @Override
        protected void onPostExecute(ViewFlowEvent result) {
            super.onPostExecute(result);

            // onViewFlowEventの中でunregisterが呼ばれても問題が無いように、、
            // Listener配列をコピーしてから各listenerへ通知を行う。
            List<IViewFlowListener> listeners = new ArrayList<IViewFlowListener>();
            listeners.addAll(getListeners());
            for(IViewFlowListener listener : listeners) {
                listener.onViewFlowEvent(result);
            }

            // メモ：AsyncTaskのstatusが、ちゃんとcompletedに設定されないことがある。
            // なので、mLoadTaskがnullかどうかで、Runningかどうかを判定する仕組みにした。
            mLoadTask = null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            notifyProgress(PROGRESS_LOAD_DATA, values[0], values[1]);
        }

        @Override
        public void onProgressUpdateProgram(int prog, int max) {
            publishProgress(prog, max);
        }
    }

    private LoadDataTask mLoadTask;

    protected DataViewFlow() {
        super();
        mProgramDataMgr = new ProgramDataManager();
    }

    /**
     * DataViewFlowインスタンスを返す。
     *
     * @return
     */
    public static DataViewFlow getInstance() {
        if(null == sInstance) {
            sInstance = new DataViewFlow();
        }
        return sInstance;
    }

    /**
     * データをViewFlowにloadすべきかどうか。
     * trueが返ってきたら、ViewFlowを使う前にinvokeLoadDataを呼ぶこと。
     *
     * @return
     */
    public boolean shouldLoadData() {
        return (null == mStationDataMgr);
    }

    /**
     * Update中かどうか（updateを行うAsyncTaskが実行中かどうか）。
     *
     * @return
     */
    public boolean isUpdating() {
        boolean res = false;
        if(null != mLoadTask) {
            res = true;
        }
        return res;
    }

    /**
     * メンバ変数へのデータのloadを開始する。
     * 既にDatabaseにデータが格納されていればそちらを読むし、無ければnetworkから取ってくる。
     * 完了すると、ViewFlowListenerへ通知がいく。 DBにデータがあるかどうかで処理時間が大きく異なるので注意。
     *
     * @param context
     */
    public void invokeLoadData(Context context) {
        mLoadTask = new LoadDataTask();
        mLoadTask.execute(context);
    }

    /**
     * StationDataを管理する、StationDataManagerクラスのインスタンスを返す。
     *
     * @return データのloadが終わっていない時はnullが返る。
     */
    public StationDataManager getStationDataMgr() {
        return mStationDataMgr;
    }

    /**
     * ProgramData、RecommendProgramDataを管理する、
     * ProgramDataManagerクラスのインスタンスを返す。
     *
     * @return　
     */
    public ProgramDataManager getProgramDataMgr() {
        return mProgramDataMgr;
    }

    /**
     * 局が切り替わったら呼ぶ。
     * ProgramDataManagerのprogramが、その局の今日の番組リストになる。
     *
     * @param context
     * @param newIndex
     */
    public void setStationFocusIndex(Context context, int newIndex) {
        if(null != mStationDataMgr && null != mProgramDataMgr) {
            mStationDataMgr.setFocusedIndex(context, newIndex);

            Calendar now = Calendar.getInstance(Locale.JAPAN);
            if(0 == newIndex) { // indexが0のstationは、「オススメ番組」
                mProgramDataMgr.loadRecommendProgramsNotOnAirYet(context, now);
            } else {
                int i = newIndex - 1;
                String focusedStation = mStationDataMgr.getStationInfo().get(i).id;

                // 午前0時～5時の間に番組表を表示しようとした場合、loadすべき番組表は前日の日付。
                // 月曜は「前日」が無いので、fail safeで。
                if(5 > now.get(Calendar.HOUR_OF_DAY)
                        && Calendar.DAY_OF_WEEK != Calendar.MONDAY) {
                    now.add(Calendar.DATE, -1);
                }
                mProgramDataMgr.loadOnedayTimetable(context, now, focusedStation);
            }
        }
    }

    /*
     * SharedPreferenceに保存された最終更新日時を見て､
     * DBのupdateをすべきかどうかを返す。
     */
    private boolean shouldDataUpdated(Context context) {
        Calendar now = Calendar.getInstance(Locale.JAPAN);
        return UpdatedDateManager.getInstance(context).shouldUpdate(now);
    }

}