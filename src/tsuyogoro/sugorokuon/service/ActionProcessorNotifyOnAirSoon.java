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
 * {@link SugorokuonService}�N���X���A
 * �u�������������J�n�v��m�点��Notification���o��Action({@link SugorokuonService#ACTION_NOTIFY_ONAIR_SOON})
 * ���󂯎�����ۂɏ������s���N���X�B
 * 
 * @author Tsuyoyo
 */
public class ActionProcessorNotifyOnAirSoon implements IViewFlowListener {
	
	private Context mAppContext;
	private String mOnAirTime;
	
	/**
	 * �R���X�g���N�^�B
	 * 
	 * @param context
	 */
	public ActionProcessorNotifyOnAirSoon(Context context) {
		mAppContext = context.getApplicationContext();
	}
	
	/**
	 * ���낻��ԑg���n�܂�܂���ƌ�������Notification�Œʒm����B
	 * ��������Ƀf�[�^�������ꍇ��web����f�[�^��ǂݍ��ޕK�v������ꍇ�́A
	 * �񓯊�����������̂ŁANotification tray�ɏo�Ă���܂łɋ��Ɏ኱���Ԃ�������B
	 * 
	 * @param onAirTime
	 */
	public void invokeNotifyOnAirSoon(String onAirTime) {
		Log.d("Sugorokuon", "invokeNotifyOnAirSoon - S");
		
		mOnAirTime = onAirTime;
		DataViewFlow dataViewFlow = DataViewFlow.getInstance();
		
		// DataViewFlow�Ƀf�[�^��load����Ă��Ȃ�������A�܂��̓f�[�^��load���s���B
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
		// �������Ȃ��B
	}		
	
	private void notifyRecommendReminder(String onAirTime) {
		// Reminder��Notification���o���B
		RecommendReminderSubmitter publisher = new RecommendReminderSubmitter();
		publisher.submitNotification(mAppContext, onAirTime);
		
		// ���ɕ��������ԑg��notification timer���Z�b�g�B
		RecommendReminderReserver reserver = new RecommendReminderReserver();
		reserver.setNextNotification(mAppContext, onAirTime);
	}
}
