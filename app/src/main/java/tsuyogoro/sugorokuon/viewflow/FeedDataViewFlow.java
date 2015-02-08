/**
 * Copyright (c) 
 * 2013 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.viewflow;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.impl.client.DefaultHttpClient;

import tsuyogoro.sugorokuon.constant.SugorokuonConst;
import tsuyogoro.sugorokuon.datatype.Feed;
import tsuyogoro.sugorokuon.radikoadaptation.FeedDownloader;
import android.content.Context;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.util.Log;

/**
 * Feed情報をdownloadするためのクラス。
 * 最近のonAir曲情報を取るために使う。
 *
 * @author Tsuyoyo
 *
 */
public class FeedDataViewFlow extends ViewFlowBase {

    // invokeDownloadFeedから返る値
    public static final int START_TASK_EXECUTION = 0;
    public static final int STATION_MANAGE_HAS_NOT_INITIALIZED = 1;
    public static final int NO_FEED_FOR_RECOMMEND_CATEGORY = 2;

    private static FeedDataViewFlow sInstance;

    public static FeedDataViewFlow getInstance() {
        if(null == sInstance) {
            sInstance = new FeedDataViewFlow();
        }
        return sInstance;
    }

    // KeyはStationID。
    private Map<String, Feed> mFeedMap = new HashMap<String, Feed>();

    private LoadDataTask mLoadTask;

    /*
     * 指定したstation（コンストラクタで指定）の、onAir情報を取得するためのtask。
     */
    private class LoadDataTask extends AsyncTask<Context, Void, ViewFlowEvent> {

        private final String stationId;

        LoadDataTask(String _stationId) {
            stationId = _stationId;
        }

        @Override
        protected ViewFlowEvent doInBackground(Context... arg0) {
            // focusedStationIdのfeedがキャッシュの中に無ければ、downloadを行う。
            if(!mFeedMap.containsKey(stationId)) {
                // feed取得開始。
                FeedDownloader downloader = new FeedDownloader();
                Feed feed = downloader.getFeed(stationId, new DefaultHttpClient());

                // getFeedがnullだったらdownloadに失敗している。
                if(null == feed) {
                    return ViewFlowEvent.FAILED_FEED_DONWLOAD;
                } else {
                    mFeedMap.put(stationId, feed);
                }
            }
            return ViewFlowEvent.COMPLETE_FEED_DOWNLOAD;
        }

        @Override
        protected void onPostExecute(ViewFlowEvent result) {
            super.onPostExecute(result);
            notifyEvent(result);
        }
    }

    /**
     * フォーカスが当たっている局のFeedの取得を開始する。
     * 完了した後、onViewFlowEventにCOMPLETE_FEED_DOWNLOAD
     * （失敗した場合はFAILED_FEED_DONWLOAD）が通知される。
     * 完了後はgetFeedで、downloadしたfeed情報が取得できる。
     * 既にTaskが走っている時は、そのTaskをキャンセルして新しいtaskを走らせる。
     *
     * @param context
     * @return taskを無事走らせることができたらSTART_TASK_EXECUTIONが返る。
     */
    public int invokeDownloadFeed(Context context) {
        // StationDataMgrが未setだったらFailとする。
        StationDataManager stationMgr = DataViewFlow.getInstance().getStationDataMgr();
        if(null == stationMgr) {
            return STATION_MANAGE_HAS_NOT_INITIALIZED;
        }

        // 現在focusの当たっているstationのIDを取得。
        int focusedStationIndex = stationMgr.getFocusedIndex();

        // オススメにfocusが当たっている時にfeed取りにいっちゃダメ。
        if(0 == focusedStationIndex) {
            Log.e(SugorokuonConst.LOGTAG, "No feed for recommend programs.");
            return NO_FEED_FOR_RECOMMEND_CATEGORY;
        }
        String focusedStationId = stationMgr.getStationInfo().get(focusedStationIndex - 1).id;

        // 既に走っているtaskがあるのなら、それをcancel。
        if(null != mLoadTask && mLoadTask.getStatus().equals(Status.RUNNING)) {
            mLoadTask.cancel(true);
        }

        mLoadTask = new LoadDataTask(focusedStationId);
        mLoadTask.execute(context);

        return START_TASK_EXECUTION;
    }

    /**
     * 現在focusが当たっているstationの、最近onAir曲のcacheを消す。
     * reloadする時に、invokeDownloadFeed()直前にcallすること。
     *
     */
    public void removeCurrentFocusedStationCache() {
        // StationDataMgrが未setだったらFailとする。
        StationDataManager stationMgr = DataViewFlow.getInstance().getStationDataMgr();
        if(null != stationMgr) {
            int focusedStationIndex = stationMgr.getFocusedIndex();
            String focusedStationId = stationMgr.getStationInfo().get(focusedStationIndex - 1).id;

            // 既にfocusedStationIdの要素がmapに登録されていたら一旦消す。
            if(mFeedMap.containsKey(focusedStationId)) {
                mFeedMap.remove(focusedStationId);
            }
        }
    }

    /**
     * 現在フォーカスが当たっている局のFeedを、キャッシュから取得する。
     * キャッシュから取れなかったら（まだ取得していなかったら）nullが返るので、
     * その場合はinvokeDownloadFeedでdownloadを行う事。
     *
     */
    public Feed getFocusedStationFeed() {
        String focusedStationId = getFocusedStationId();
        if(null != focusedStationId) {
            return mFeedMap.get(focusedStationId);
        }
        return null;
    }

    private String getFocusedStationId() {
        // StationDataMgrが未setだったらFailとする。
        StationDataManager stationMgr = DataViewFlow.getInstance().getStationDataMgr();
        if(null == stationMgr || 0 == stationMgr.getFocusedIndex()) {
            return null;
        }
        return stationMgr.getStationInfo().get(stationMgr.getFocusedIndex() - 1).id;
    }

}