/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.presenter;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.util.GATrackingUtil;
import tsuyogoro.sugorokuon.util.SugorokuonUtils;
import tsuyogoro.sugorokuon.viewflow.DataViewFlow;
import tsuyogoro.sugorokuon.viewflow.ProgramDataManager;
import tsuyogoro.sugorokuon.viewflow.StationDataManager;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * �ԑg�\��Fragment�ɂ����āAflick�����handle����N���X�B
 * Flick����ɂ���āA�ԑg�\�𗂓��O���ɐ؂�ւ���B
 * 
 * @author Tsuyoyo
 *
 */
class ProgramListFragmentFlickHandler implements View.OnTouchListener {

	private static final float FLICK_WIDTH = 70; // 100dp.
	private static final float FLICK_THRESHOLD_HEIGHT = 70; //70dp.
	
	// ���̂Ƃ���A500�~���b���Z������Ƃ�����݂����ɂȂ��Ă��܂��̂ŁA���̂��炢�B
	private static final long FLICK_ANIMATION_DURATION = 500;
	
	private float mTappedPosX;
	private float mTappedPosY;
	private final Context mContext;
	private final View mTimeTable;
	
	/**
	 * �R���X�g���N�^�B ���̓s�x�������邱�ƁB
	 * 
	 * @param tappedPosX
	 * @param tappedPosY
	 */
	public ProgramListFragmentFlickHandler(Context context, View timeTable) {
		mContext = context;
		mTimeTable = timeTable;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		boolean isConsumed = false;
		switch(event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mTappedPosX = event.getX();
			mTappedPosY = event.getY();
			break;
		case MotionEvent.ACTION_UP:
			handleFlickEvent(event.getX(), event.getY());
			break;
		case MotionEvent.ACTION_CANCEL:
			handleFlickEvent(event.getX(), event.getY());
			break;
		}
		return isConsumed;
	}
		
	private void handleFlickEvent(float posX, float posY) {
		// �������������Ă���focus���I�X�X���ԑg��������flick�͎󂯕t���Ȃ��B
		StationDataManager stationMgr = DataViewFlow.getInstance().getStationDataMgr();
		int currentStation = stationMgr.getFocusedIndex();
		if(0 == currentStation) {
			return;
		}
		
		// �ԑg�\�𗂓�/�O���ɐ؂�ւ���B
		String stationId = stationMgr.getStationInfo().get(currentStation - 1).id;
		ProgramDataManager progMgr = DataViewFlow.getInstance().getProgramDataMgr();
		
		// FLICK_WIDTH(dp)�����Ă�����AFlick�Ƃ݂Ȃ��ď������s���B
		float movedX = posX - mTappedPosX;
		float movedY = posY - mTappedPosY;
		if(FLICK_WIDTH <= SugorokuonUtils.calculateDpfromPx(mContext, Math.abs(movedX)) && 
				FLICK_THRESHOLD_HEIGHT > SugorokuonUtils.calculateDpfromPx(mContext, Math.abs(movedY))) {			
			// �����̔ԑg�\��
			if(0 > movedX) {
				if(progMgr.loadNextdayTimetable(mContext, stationId)) {
					invokeChanegeAnimation(R.anim.slide_in_right);
					
					// Flick event��tracking�B
					SugorokuonActivityEventTracker.submitGAEvent(
							R.string.ga_event_action_flick_nextday, 
							mContext, GATrackingUtil.getModelAndProductName());
				}
			}
			// �����̔ԑg�\��
			else {
				if(progMgr.loadPreviousdayTimetable(mContext, stationId)) {
					invokeChanegeAnimation(R.anim.slide_out_right);
					
					// Flick event��tracking�B
					SugorokuonActivityEventTracker.submitGAEvent(
							R.string.ga_event_action_flick_previousday, 
							mContext, GATrackingUtil.getModelAndProductName());
				}
			}
		}
	}
	
	private void invokeChanegeAnimation(int animationResId) {
		Animation animation = AnimationUtils.loadAnimation(mContext, animationResId);
		animation.setDuration(FLICK_ANIMATION_DURATION);
	    if(null != mTimeTable) {
	    	mTimeTable.startAnimation(animation);
	    }
	}
		
}
