/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.service;

import java.util.Calendar;
import java.util.Locale;

import tsuyogoro.sugorokuon.settings.UpdatedDateManager;
import tsuyogoro.sugorokuon.viewflow.DataViewFlow;
import tsuyogoro.sugorokuon.viewflow.IViewFlowListener;
import tsuyogoro.sugorokuon.viewflow.ViewFlowEvent;
import android.content.Context;

/**
 * {@link SugorokuonService}クラスが、
 * 番組/局DataBaseのupdateを行うAction({@link SugorokuonService#ACTION_LOAD_PROGRAM_DATA})
 * を受け取った際に処理を行うクラス。
 *
 * @author Tsuyoyo
 */
class ActionProcessorUpdateDatabase implements IViewFlowListener {

    private Context mAppContext;

    private LoadingProgressSubmitter mProgressSubmitter;
    private ProgramUpdateReserver mUpdateReserver;
    private RecommendReminderReserver mRemindReserver;

    /**
     * コンストラクタ。
     * 基本的に１つのActionに対して、１度のみのinvokeUpdateProgramDatabase()の想定。
     * このクラスは、オススメ番組のreminderとUpdateのtimerを張りかえるので、
     * {@link RecommendReminderReserver}と{@link ProgramUpdateReserver}の
     * インスタンスを渡して使う。
     *
     * @param context
     * @param remindReserver
     * @param updateReserver
     */
    public ActionProcessorUpdateDatabase(Context context,
                                         RecommendReminderReserver reminderReserver, ProgramUpdateReserver updateReserver) {
        mAppContext = context.getApplicationContext();
        mRemindReserver = reminderReserver;
        mUpdateReserver = updateReserver;
    }

    /**
     * DataViewFlowに対して、データのload開始要求を出す。
     *
     */
    public void invokeUpdateProgramDatabase() {
        // DataViewFlowへのload開始要求を出す。
        DataViewFlow.getInstance().register(this);
        DataViewFlow.getInstance().invokeLoadData(mAppContext);

        // ネットワークからの更新が必要な場合、
        // これまでセットしていたTimerを全てcancelして、更新開始のnotificationを出す。
        Calendar now = Calendar.getInstance(Locale.JAPAN);
        if(UpdatedDateManager.getInstance(mAppContext).shouldUpdate(now)) {
            cancelTimers();
            mProgressSubmitter = new LoadingProgressSubmitter(now);
            mProgressSubmitter.submitNotification(mAppContext);
        }
    }

    @Override
    public void onViewFlowEvent(ViewFlowEvent event) {
        switch(event) {
            case COMPLETE_DATA_UPDATECOMPLETE:
                onRecommendUpdateCompleted();
                break;
            case FAILED_DATA_UPDATE:
            case FAILED_STATION_UPDATE:
                onRecommendUpdateError();
                break;
            default:
                // 想定外のフローだが、listener登録だけは解除。
                DataViewFlow.getInstance().unregister(this);
                break;
        }
    }

    @Override
    public void onProgress(int whatsRunning, int progress, int max) {
        // Notificationにprogressを表示していく。
        if(null != mProgressSubmitter) {
            mProgressSubmitter.updateNotification(mAppContext, max, progress);
        }
    }

    private void onRecommendUpdateCompleted() {
        // DataViewFlowからの結果は受け取り済みなので、unregister。
        DataViewFlow.getInstance().unregister(this);

        // mProgressSubmitterがnullで無い時は、networkからデータを取ってきた時。
        // この時のみ、次の自動更新のtimerおよびオススメ番組のTimerのセットを行う。
        if(null != mProgressSubmitter) {
            // Recommendのupdateが終わったことをnotificationで表示する。
            mProgressSubmitter.completeNotifiation(mAppContext);
            mProgressSubmitter = null;

            // 次のupdateのtimerをセット。
            mUpdateReserver.setNextNotification(mAppContext);

            // 次に放送される番組のnotification timerをセット。
            // Reminderの最初の一発目は、lastNotifyTimeはnull。
            mRemindReserver.setNextNotification(mAppContext, null);
        }
    }

    private void onRecommendUpdateError() {
        // DataViewFlowからの結果は受け取り済みなので、unregister。
        DataViewFlow.getInstance().unregister(this);

        // Error通知をNotificationへ出す。
        if(null != mProgressSubmitter) {
            mProgressSubmitter.errorNotification(mAppContext, 0);
        }
    }

    private void cancelTimers() {
        mRemindReserver.cancelNextNotification(mAppContext);
        mUpdateReserver.cancelNextNotification(mAppContext);
    }

}