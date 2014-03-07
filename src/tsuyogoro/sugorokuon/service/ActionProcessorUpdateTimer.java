package tsuyogoro.sugorokuon.service;

import tsuyogoro.sugorokuon.viewflow.DataViewFlow;
import tsuyogoro.sugorokuon.viewflow.IViewFlowListener;
import tsuyogoro.sugorokuon.viewflow.ViewFlowEvent;
import android.content.Context;

/**
 * {@link SugorokuonService}�N���X���A
 * Timer��Update Action({@link SugorokuonService#ACTION_UPDATE_TIMER})
 * ���󂯎�����ۂɏ������s���N���X�B
 * 
 * @author Tsuyoyo
 */
class ActionProcessorUpdateTimer implements IViewFlowListener {

	private Context mAppContext;
	
	private RecommendReminderReserver mRemindReserver;
	private ProgramUpdateReserver mUpdateReserver;
	
	/**
	 * �R���X�g���N�^�B
	 * ���̃N���X�́A�I�X�X���ԑg��reminder��Update��timer�𒣂肩����̂ŁA
	 * {@link RecommendReminderReserver}��{@link ProgramUpdateReserver}��
	 * �C���X�^���X��n���Ďg���B
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
		// DataViewFlow�Ƀf�[�^��load����Ă��Ȃ�������A�܂��̓f�[�^��load���s���B
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
		
		// ����update��timer���Z�b�g�B
		mUpdateReserver.setNextNotification(mAppContext);
		
		// ���ɕ��������ԑg��notification timer���Z�b�g�B
		// Reminder�̍ŏ��̈ꔭ�ڂ́AlastNotifyTime��null�B
		mRemindReserver.setNextNotification(mAppContext, null);		
	}
	
	private void cancelTimers() {
		mRemindReserver.cancelNextNotification(mAppContext);
		mUpdateReserver.cancelNextNotification(mAppContext);
	}
	
}
