/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.presenter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.constant.SugorokuonConst;
import tsuyogoro.sugorokuon.util.GATrackingUtil;
import tsuyogoro.sugorokuon.viewflow.DataViewFlow;
import tsuyogoro.sugorokuon.viewflow.IViewFlowListener;
import tsuyogoro.sugorokuon.viewflow.ProgramDataManager;
import tsuyogoro.sugorokuon.viewflow.ProgramDataManager.IEventListener;
import tsuyogoro.sugorokuon.viewflow.StationDataManager;
import tsuyogoro.sugorokuon.viewflow.ViewFlowEvent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class DateOptionFlagment extends Fragment 
	implements IEventListener, IViewFlowListener {

	private ImageButton mLeftBtn;
	private TextView mDate;
	private ImageButton mRightBtn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(SugorokuonConst.LOGTAG, "+ DateOptionFlagment:onCreateView");			
		
		super.onCreateView(inflater, container, savedInstanceState);
		
		// �{�^���̓A�v���̏�Ԃɂ���ĐF�X�ς��̂ŁA�����o�Ɏ�������B
		View body = inflater.inflate(R.layout.date_options_fragment, null);
		mLeftBtn 	= (ImageButton) body.findViewById(R.id.date_options_left_btn);
		mDate 		= (TextView) body.findViewById(R.id.date_options_center_text);
		mRightBtn 	= (ImageButton) body.findViewById(R.id.date_options_right_btn);
		
		// listener�̓o�^�ȂǁB
		setupButtons();
		
		Log.d(SugorokuonConst.LOGTAG, "- DateOptionFlagment:onCreateView");			
		
		return body;
	}

	private void setupButtons() {
		View.OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				doOnClickButton(v.getId());
			}
		};
		mLeftBtn.setOnClickListener(listener);
		mRightBtn.setOnClickListener(listener);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		// �j���ɂ���Ă̓{�^����disable�ɁB
		setUiParts();

		// ��������ʂɕ\������Ă��鎞�́AData�̕ω����󂯎��悤�ɂ���B
		registerToProgramDataViewFlow();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		// ��������ʂ�������鎞��listener�����B
		ProgramDataManager mgr = DataViewFlow.getInstance().getProgramDataMgr();
		if(null != mgr) {
			mgr.listeners.remove(this);
		}
	}

	@Override
	public void onFocusedProgramDateChanged(Calendar newDate) {
		setUiParts();
	}
	
	@Override
	public void onFocusedProgramIndexChanged(int newIndex) {
		// Nothing to do...		
	}
	
	@Override
	public void onViewFlowEvent(ViewFlowEvent event) {
		switch(event) {
		// �ԑg�\�X�V�O��onResume�ł�programDataViewFlow����������
		// listener�o�^���o���Ă��Ȃ��̂ŁA�ԑg�\�X�V�����̒ʒm��������āAlistener�o�^�B
		case COMPLETE_DATA_UPDATECOMPLETE:
			setUiParts();
			registerToProgramDataViewFlow();			
			break;
		default:
			// Nothing to do.
			break;
		}
	}

	@Override
	public void onProgress(int whatsRunning, int progress, int max) {
		// Nothing to do.
	}
	
	private void doOnClickButton(int id) {
		StationDataManager stationMgr = DataViewFlow.getInstance().getStationDataMgr();
		int currentStation = stationMgr.getFocusedIndex();
		String stationId = stationMgr.getStationInfo().get(currentStation - 1).id;
		
		// �����ꂽ�{�^���ŁA������������O����������B
		ProgramDataManager progMgr = DataViewFlow.getInstance().getProgramDataMgr();
		switch(id) {
		case R.id.date_options_left_btn:
			trackButtonClicked(id);
			progMgr.loadPreviousdayTimetable(getActivity(), stationId);
			break;
		case R.id.date_options_right_btn:
			trackButtonClicked(id);
			progMgr.loadNextdayTimetable(getActivity(), stationId);
			break;
		}
	}
	
	private void trackButtonClicked(int id) {
		int actionStrId;
		switch(id) {
		case R.id.date_options_left_btn:
			actionStrId = R.string.ga_event_action_tap_previous_day;
			break;
		case R.id.date_options_right_btn:
			actionStrId = R.string.ga_event_action_tap_next_day;
			break;
		default:
			return;
		}
		SugorokuonActivityEventTracker.submitGAEvent(
				actionStrId, getActivity(), GATrackingUtil.getModelAndProductName());		
	}
	
	private void setUiParts() {
		ProgramDataManager dataMgr = DataViewFlow.getInstance().getProgramDataMgr();
		
		// �f�[�^��load����ĂȂ��ꍇ�A����сARecommend�Ƀt�H�[�J�X������ꍇ�B
		if(null == dataMgr || null == dataMgr.getFocusedCalendar()) {			
			// �u�`����v
			String strFrom = getActivity().getString(R.string.date_switcher_recommend_begin);
			SimpleDateFormat sdfFrom = new SimpleDateFormat(strFrom, Locale.JAPANESE);
			String from = sdfFrom.format(new Date(Calendar.getInstance(Locale.JAPAN).getTimeInMillis()));
			
			//�@�u�`�܂ł̃I�X�X���v
			Calendar nextSunday = Calendar.getInstance(Locale.JAPAN);
			while(Calendar.SUNDAY != nextSunday.get(Calendar.DAY_OF_WEEK)) {
				nextSunday.add(Calendar.DATE, 1);
			}
			String strTo = getActivity().getString(R.string.date_switcher_recommend_end);
			SimpleDateFormat sdfTo = new SimpleDateFormat(strTo, Locale.JAPANESE);
			String to = sdfTo.format(new Date(nextSunday.getTimeInMillis()));
			
			mDate.setText(from + to);
			
			// Recommend�̎��̓{�^�����B���B
			mLeftBtn.setVisibility(View.GONE);
			mRightBtn.setVisibility(View.GONE);
		} 
		else {
			// ���t�H�[�J�X�̓������Ă�����t�B
			Calendar date = dataMgr.getFocusedCalendar();
			
			// �^�񒆂̓��t��ς���B
			int stringId = R.string.date_mmddeee;
			String str = getActivity().getString(stringId);
			SimpleDateFormat sdf = new SimpleDateFormat(str, Locale.JAPANESE);
			mDate.setText(sdf.format(new Date(date.getTimeInMillis())));
			
			// ���j�́u�O���v�������āA���j�́u�����v�������B
			int day_of_week = date.get(Calendar.DAY_OF_WEEK);
			mLeftBtn.setVisibility(!(Calendar.MONDAY == day_of_week) 
					? View.VISIBLE : View.INVISIBLE);
			mRightBtn.setVisibility((!(Calendar.SUNDAY == day_of_week) 
					? View.VISIBLE : View.INVISIBLE));
		}
	}

	private void registerToProgramDataViewFlow() {
		ProgramDataManager mgr = DataViewFlow.getInstance().getProgramDataMgr();
		if(null != mgr) {
			mgr.listeners.add(this);
		}
	}

}
