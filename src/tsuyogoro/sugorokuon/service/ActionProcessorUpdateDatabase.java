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
 * {@link SugorokuonService}�N���X���A
 * �ԑg/��DataBase��update���s��Action({@link SugorokuonService#ACTION_LOAD_PROGRAM_DATA})
 * ���󂯎�����ۂɏ������s���N���X�B
 * 
 * @author Tsuyoyo
 */
class ActionProcessorUpdateDatabase implements IViewFlowListener {

	private Context mAppContext;
	
	private LoadingProgressSubmitter mProgressSubmitter;	
	private ProgramUpdateReserver mUpdateReserver;
	private RecommendReminderReserver mRemindReserver;
	
	/**
	 * �R���X�g���N�^�B
	 * ��{�I�ɂP��Action�ɑ΂��āA�P�x�݂̂�invokeUpdateProgramDatabase()�̑z��B
	 * ���̃N���X�́A�I�X�X���ԑg��reminder��Update��timer�𒣂肩����̂ŁA
	 * {@link RecommendReminderReserver}��{@link ProgramUpdateReserver}��
	 * �C���X�^���X��n���Ďg���B
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
	 * DataViewFlow�ɑ΂��āA�f�[�^��load�J�n�v�����o���B
	 * 
	 */
	public void invokeUpdateProgramDatabase() {
		// DataViewFlow�ւ�load�J�n�v�����o���B
		DataViewFlow.getInstance().register(this);
		DataViewFlow.getInstance().invokeLoadData(mAppContext);
		
		// �l�b�g���[�N����̍X�V���K�v�ȏꍇ�A
		// ����܂ŃZ�b�g���Ă���Timer��S��cancel���āA�X�V�J�n��notification���o���B
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
			// �z��O�̃t���[�����Alistener�o�^�����͉����B
			DataViewFlow.getInstance().unregister(this);
			break;
		}
	}
	
	@Override
	public void onProgress(int whatsRunning, int progress, int max) {
		// Notification��progress��\�����Ă����B
		if(null != mProgressSubmitter) {
			mProgressSubmitter.updateNotification(mAppContext, max, progress);
		}
	}		

	private void onRecommendUpdateCompleted() {
		// DataViewFlow����̌��ʂ͎󂯎��ς݂Ȃ̂ŁAunregister�B
		DataViewFlow.getInstance().unregister(this);
		
		// mProgressSubmitter��null�Ŗ������́Anetwork����f�[�^������Ă������B
		// ���̎��̂݁A���̎����X�V��timer����уI�X�X���ԑg��Timer�̃Z�b�g���s���B
		if(null != mProgressSubmitter) {
			// Recommend��update���I��������Ƃ�notification�ŕ\������B
			mProgressSubmitter.completeNotifiation(mAppContext);
			mProgressSubmitter = null;
			
			// ����update��timer���Z�b�g�B
			mUpdateReserver.setNextNotification(mAppContext);
			
			// ���ɕ��������ԑg��notification timer���Z�b�g�B
			// Reminder�̍ŏ��̈ꔭ�ڂ́AlastNotifyTime��null�B
			mRemindReserver.setNextNotification(mAppContext, null);
		}
	}
	
	private void onRecommendUpdateError() {
		// DataViewFlow����̌��ʂ͎󂯎��ς݂Ȃ̂ŁAunregister�B
		DataViewFlow.getInstance().unregister(this);
		
		// Error�ʒm��Notification�֏o���B
		if(null != mProgressSubmitter) {
			mProgressSubmitter.errorNotification(mAppContext, 0);
		}
	}
	
	private void cancelTimers() {
		mRemindReserver.cancelNextNotification(mAppContext);
		mUpdateReserver.cancelNextNotification(mAppContext);
	}
	
}
