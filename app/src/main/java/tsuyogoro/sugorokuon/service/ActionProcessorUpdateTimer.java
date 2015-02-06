package tsuyogoro.sugorokuon.service;

import tsuyogoro.sugorokuon.viewflow.DataViewFlow;
import tsuyogoro.sugorokuon.viewflow.IViewFlowListener;
import tsuyogoro.sugorokuon.viewflow.ViewFlowEvent;
import android.content.Context;

/**
 * {@link SugorokuonService}クラスが、
 * TimerのUpdate Action({@link SugorokuonService#ACTION_UPDATE_TIMER})
 * を受け取った際に処理を行うクラス。
 *
 * @author Tsuyoyo
 */
class ActionProcessorUpdateTimer implements IViewFlowListener {

    private Context mAppContext;

    private RecommendReminderReserver mRemindReserver;
    private ProgramUpdateReserver mUpdateReserver;

    /**
     * コンストラクタ。
     * このクラスは、オススメ番組のreminderとUpdateのtimerを張りかえるので、
     * {@link RecommendReminderReserver}と{@link ProgramUpdateReserver}の
     * インスタンスを渡して使う。
     *
     * @param context
     * @param remindReserver
     * @param updateReserver
     */
    public ActionProcessorUpdateTimer(Context context,
                                      RecommendReminderReserver remindReserver, ProgramUpdateReserver updateReserver) {
        mAppContext = context.getApplicationContext();
        mRemindReserver = remindReserver;
        mUpdateReserver = updateReserver;
    }

    @Override
    public void onViewFlowEvent(ViewFlowEvent event) {
        switch(event) {
            case COMPLETE_DATA_UPDATECOMPLETE:
                DataViewFlow.getInstance().unregister(this);
                processTimerUpdate();
                break;
            case FAILED_DATA_UPDATE:
            case FAILED_STATION_UPDATE:
                DataViewFlow.getInstance().unregister(this);
                break;
            default:
                // Nothing to do.
                break;
        }
    }

    @Override
    public void onProgress(int whatsRunning, int progress, int max) {
    }

    public void processTimerUpdate() {
        // DataViewFlowにデータがloadされていなかったら、まずはデータのloadを行う。
        DataViewFlow dataViewFlow = DataViewFlow.getInstance();
        if(dataViewFlow.shouldLoadData()) {
            dataViewFlow.register(this);
            dataViewFlow.invokeLoadData(mAppContext);
        } else {
            updateTimers();
        }
    }

    private void updateTimers() {
        cancelTimers();

        // 次のupdateのtimerをセット。
        mUpdateReserver.setNextNotification(mAppContext);

        // 次に放送される番組のnotification timerをセット。
        // Reminderの最初の一発目は、lastNotifyTimeはnull。
        mRemindReserver.setNextNotification(mAppContext, null);
    }

    private void cancelTimers() {
        mRemindReserver.cancelNextNotification(mAppContext);
        mUpdateReserver.cancelNextNotification(mAppContext);
    }

}