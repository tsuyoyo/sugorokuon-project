/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.service;

import tsuyogoro.sugorokuon.viewflow.DataViewFlow;
import tsuyogoro.sugorokuon.viewflow.IViewFlowListener;
import tsuyogoro.sugorokuon.viewflow.ViewFlowEvent;
import android.content.Context;
import android.util.Log;

/**
 * {@link SugorokuonService}クラスが、
 * 「もうすぐ放送開始」を知らせるNotificationを出すAction({@link SugorokuonService#ACTION_NOTIFY_ONAIR_SOON})
 * を受け取った際に処理を行うクラス。
 *
 * @author Tsuyoyo
 */
public class ActionProcessorNotifyOnAirSoon implements IViewFlowListener {

    private Context mAppContext;
    private String mOnAirTime;

    /**
     * コンストラクタ。
     *
     * @param context
     */
    public ActionProcessorNotifyOnAirSoon(Context context) {
        mAppContext = context.getApplicationContext();
    }

    /**
     * そろそろ番組が始まりますよと言う事をNotificationで通知する。
     * メモリ上にデータが無い場合やwebからデータを読み込む必要がある場合は、
     * 非同期処理が走るので、Notification trayに出てくるまでに共に若干時間がかかる。
     *
     * @param onAirTime
     */
    public void invokeNotifyOnAirSoon(String onAirTime) {
        Log.d("Sugorokuon", "invokeNotifyOnAirSoon - S");

        mOnAirTime = onAirTime;
        DataViewFlow dataViewFlow = DataViewFlow.getInstance();

        // DataViewFlowにデータがloadされていなかったら、まずはデータのloadを行う。
        if(dataViewFlow.shouldLoadData()) {
            Log.d("Sugorokuon", "invokeNotifyOnAirSoon (shouleLoadData)");
            dataViewFlow.register(this);
            dataViewFlow.invokeLoadData(mAppContext);
        } else {
            notifyRecommendReminder(onAirTime);
        }

        Log.d("Sugorokuon", "invokeNotifyOnAirSoon - S");
    }

    @Override
    public void onViewFlowEvent(ViewFlowEvent event) {
        switch(event) {
            case COMPLETE_DATA_UPDATECOMPLETE:
                Log.d("Sugorokuon", "onViewFlowEvent - COMPLETE_DATA_UPDATECOMPLETE");
                DataViewFlow.getInstance().unregister(this);
                notifyRecommendReminder(mOnAirTime);
                break;
            default:
                // Nothing to do.
                break;
        }
    }

    @Override
    public void onProgress(int whatsRunning, int progress, int max) {
        // 何もしない。
    }

    private void notifyRecommendReminder(String onAirTime) {
        // ReminderのNotificationを出す。
        RecommendReminderSubmitter publisher = new RecommendReminderSubmitter();
        publisher.submitNotification(mAppContext, onAirTime);

        // 次に放送される番組のnotification timerをセット。
        RecommendReminderReserver reserver = new RecommendReminderReserver();
        reserver.setNextNotification(mAppContext, onAirTime);
    }
}