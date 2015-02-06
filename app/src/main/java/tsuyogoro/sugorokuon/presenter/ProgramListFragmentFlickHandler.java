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
 * 番組表のFragmentにおいて、flick操作をhandleするクラス。
 * Flick操作によって、番組表を翌日前日に切り替える。
 *
 * @author Tsuyoyo
 *
 */
class ProgramListFragmentFlickHandler implements View.OnTouchListener {

    private static final float FLICK_WIDTH = 70; // 100dp.
    private static final float FLICK_THRESHOLD_HEIGHT = 70; //70dp.

    // 今のところ、500ミリ秒より短くするとちらつきみたいになってしまうので、このくらい。
    private static final long FLICK_ANIMATION_DURATION = 500;

    private float mTappedPosX;
    private float mTappedPosY;
    private final Context mContext;
    private final View mTimeTable;

    /**
     * コンストラクタ。 その都度生成すること。
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
        // もし今当たっているfocusがオススメ番組だったらflickは受け付けない。
        StationDataManager stationMgr = DataViewFlow.getInstance().getStationDataMgr();
        int currentStation = stationMgr.getFocusedIndex();
        if(0 == currentStation) {
            return;
        }

        // 番組表を翌日/前日に切り替える。
        String stationId = stationMgr.getStationInfo().get(currentStation - 1).id;
        ProgramDataManager progMgr = DataViewFlow.getInstance().getProgramDataMgr();

        // FLICK_WIDTH(dp)動いていたら、Flickとみなして処理を行う。
        float movedX = posX - mTappedPosX;
        float movedY = posY - mTappedPosY;
        if(FLICK_WIDTH <= SugorokuonUtils.calculateDpfromPx(mContext, Math.abs(movedX)) &&
                FLICK_THRESHOLD_HEIGHT > SugorokuonUtils.calculateDpfromPx(mContext, Math.abs(movedY))) {
            // 翌日の番組表へ
            if(0 > movedX) {
                if(progMgr.loadNextdayTimetable(mContext, stationId)) {
                    invokeChanegeAnimation(R.anim.slide_in_right);

                    // Flick eventのtracking。
                    SugorokuonActivityEventTracker.submitGAEvent(
                            R.string.ga_event_action_flick_nextday,
                            mContext, GATrackingUtil.getModelAndProductName());
                }
            }
            // 翌日の番組表へ
            else {
                if(progMgr.loadPreviousdayTimetable(mContext, stationId)) {
                    invokeChanegeAnimation(R.anim.slide_out_right);

                    // Flick eventのtracking。
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