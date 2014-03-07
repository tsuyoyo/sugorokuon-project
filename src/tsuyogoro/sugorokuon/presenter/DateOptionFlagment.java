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
		
		// ボタンはアプリの状態によって色々変わるので、メンバに持たせる。
		View body = inflater.inflate(R.layout.date_options_fragment, null);
		mLeftBtn 	= (ImageButton) body.findViewById(R.id.date_options_left_btn);
		mDate 		= (TextView) body.findViewById(R.id.date_options_center_text);
		mRightBtn 	= (ImageButton) body.findViewById(R.id.date_options_right_btn);
		
		// listenerの登録など。
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
		
		// 曜日によってはボタンをdisableに。
		setUiParts();

		// 自分が画面に表示されている時は、Dataの変化を受け取るようにする。
		registerToProgramDataViewFlow();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		// 自分が画面から消える時はlistener解除。
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
		// 番組表更新前のonResumeではprogramDataViewFlowが未生成で
		// listener登録が出来ていないので、番組表更新完了の通知をもらって、listener登録。
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
		
		// 押されたボタンで、翌日だったり前日だったり。
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
		
		// データがloadされてない場合、および、Recommendにフォーカスがある場合。
		if(null == dataMgr || null == dataMgr.getFocusedCalendar()) {			
			// 「～から」
			String strFrom = getActivity().getString(R.string.date_switcher_recommend_begin);
			SimpleDateFormat sdfFrom = new SimpleDateFormat(strFrom, Locale.JAPANESE);
			String from = sdfFrom.format(new Date(Calendar.getInstance(Locale.JAPAN).getTimeInMillis()));
			
			//　「～までのオススメ」
			Calendar nextSunday = Calendar.getInstance(Locale.JAPAN);
			while(Calendar.SUNDAY != nextSunday.get(Calendar.DAY_OF_WEEK)) {
				nextSunday.add(Calendar.DATE, 1);
			}
			String strTo = getActivity().getString(R.string.date_switcher_recommend_end);
			SimpleDateFormat sdfTo = new SimpleDateFormat(strTo, Locale.JAPANESE);
			String to = sdfTo.format(new Date(nextSunday.getTimeInMillis()));
			
			mDate.setText(from + to);
			
			// Recommendの時はボタンを隠す。
			mLeftBtn.setVisibility(View.GONE);
			mRightBtn.setVisibility(View.GONE);
		} 
		else {
			// 今フォーカスの当たっている日付。
			Calendar date = dataMgr.getFocusedCalendar();
			
			// 真ん中の日付を変える。
			int stringId = R.string.date_mmddeee;
			String str = getActivity().getString(stringId);
			SimpleDateFormat sdf = new SimpleDateFormat(str, Locale.JAPANESE);
			mDate.setText(sdf.format(new Date(date.getTimeInMillis())));
			
			// 月曜は「前日」が無くて、日曜は「翌日」が無い。
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
