/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.presenter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.constant.SugorokuonConst;
import tsuyogoro.sugorokuon.datatype.Program;
import tsuyogoro.sugorokuon.util.SugorokuonUtils;
import tsuyogoro.sugorokuon.viewflow.DataViewFlow;
import tsuyogoro.sugorokuon.viewflow.IViewFlowListener;
import tsuyogoro.sugorokuon.viewflow.ProgramDataManager;
import tsuyogoro.sugorokuon.viewflow.StationDataManager;
import tsuyogoro.sugorokuon.viewflow.ViewFlowEvent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * �ԑg����list�ŕ\������ӏ���fragment�B
 * 
 * @author Tsuyoyo
 *
 */
public class ProgramListFragment extends Fragment
	implements StationDataManager.IEventListener, 
			   ProgramDataManager.IEventListener, IViewFlowListener {

	private DataViewFlow mDataViewFlow;
	private ProgramListAdapter mAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDataViewFlow = DataViewFlow.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(SugorokuonConst.LOGTAG, "+ ProgramListFragment:onCreateView");
		
		super.onCreateView(inflater, container, savedInstanceState);
		
		View body = inflater.inflate(R.layout.program_list_fragment, null);
		
		// �\�����邽�߂̃f�[�^��ViewFlow�̒��ɂ��邩�ǂ����ŕ\����؂�ւ���B
		if(0 >= mDataViewFlow.getProgramDataMgr().getFocusedProgramList().size()) {
			body.findViewById(R.id.program_list_no_data).setVisibility(View.VISIBLE);
			body.findViewById(R.id.program_list).setVisibility(View.GONE);
		} else {
			body.findViewById(R.id.program_list_no_data).setVisibility(View.GONE);
			body.findViewById(R.id.program_list).setVisibility(View.VISIBLE);
		}
		
		// Adapter�̐ݒ�
		ListView progList = (ListView) body.findViewById(R.id.program_list);
		mAdapter = new ProgramListAdapter();
		inflateListItems();
		progList.setAdapter(mAdapter);
		
		// list��click�����Ƃ��̓���
		progList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
				DataViewFlow.getInstance().getProgramDataMgr().setFocusedIndex(index);
			}
		});
		
		// Flick����ɂ���āA�ԑg�\��O��/�����ɐ؂�ւ���B
		progList.setOnTouchListener(
				new ProgramListFragmentFlickHandler(getActivity(), progList));
		
		Log.d(SugorokuonConst.LOGTAG, "- ProgramListFragment:onCreateView");
		
		return body;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		// ��������ʂ�������鎞��listener�����B
		DataViewFlow.getInstance().getProgramDataMgr().listeners.remove(this);
		StationDataManager stationMgr = DataViewFlow.getInstance().getStationDataMgr();
		if(null != stationMgr) {
			DataViewFlow.getInstance().getStationDataMgr().listeners.remove(this);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		
		// ��������ʂɕ\�������ۂ�listener��set�B
		DataViewFlow.getInstance().getProgramDataMgr().listeners.add(this);
		StationDataManager stationMgr = DataViewFlow.getInstance().getStationDataMgr();
		if(null != stationMgr) {
			stationMgr.listeners.add(this);
		} else {
			// ����stationMgr��null��������A
			// DataViewFlow�ł�data���[�h�������listener�o�^���s���B
			// onViewFlowEvent��COMPLETE�C�x���g���󂯎�邽�߂ɁADataViewFlow�֓o�^���s���B
			DataViewFlow.getInstance().register(this);
		}
		
		// Focus���A�O�񓖂����Ă������֖̂߂��B
		setFocus();
	}
	
	@Override
	public void onStationIndexChanged(int newIndex) {
		// UI�X���b�h��View����������邱�Ƃ�ۏႷ�邽�߂ɁA������ProgramList��View�𐶐�����B
		// �Q�l�Fhttp://blogs.yahoo.co.jp/hiro5_188/20237842.html
		inflateListItems();
		
		// List�֕ύX�ʒm�𑗂�B
		mAdapter.notifyDataSetChanged();

		// ���̎��Ԃ̂Ƃ����focus�������Ă����B
		Handler handler = new Handler();
		handler.post(new Runnable() {			
			@Override
			public void run() {
				setFocusOnCurrentTime((ListView)(getView().findViewById(R.id.program_list)));
			}
		});
		
		// (����) 
		// version1.5.5�ɂāA
		// ���������̔ԑg��focus���s���悤�ɂ������A(GB��)�c���؂�ւ��̋������ς���Ă��܂����B
		// �i����܂ł́A�؂�ւ��O�̔ԑg��focus���ړ� �� �������̔ԑg�ֈړ��B�ɂȂ����j
		// �c���؂�ւ��iconfiguration change�j�ł��邱�Ƃ�m��p�͂���͂��Ȃ̂ŁA
		// ���ꂪ����������A�����Ăԃt���[��t��������B
//		setFocus();
	}

	@Override
	public void onFocusedProgramIndexChanged(int newIndex) {
	}

	@Override
	public void onFocusedProgramDateChanged(Calendar newDate) {
		// list�ɕ\������View���X�V���Aadapter�֍X�V�ʒm�B
		inflateListItems();
		mAdapter.notifyDataSetChanged();
		setFocus();
		
		getView().findViewById(R.id.program_list_no_data).setVisibility(View.GONE);
		getView().findViewById(R.id.program_list).setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onViewFlowEvent(ViewFlowEvent event) {
		switch(event) {
		// �ԑg�\�X�V�O��onResume�ł�StationDataViewFlow����������
		// listener�o�^���o���Ă��Ȃ��̂ŁA�ԑg�\�X�V�����̒ʒm��������āAlistener�o�^�B
		case COMPLETE_DATA_UPDATECOMPLETE:
			StationDataManager stationMgr = DataViewFlow.getInstance().getStationDataMgr();
			stationMgr.listeners.add(this);
			
			 // �����ʒm�󂯎��K�v�������̂ŁA�O���B
			DataViewFlow.getInstance().unregister(this);
			
			// ����focus��
			onStationIndexChanged(stationMgr.getFocusedIndex());
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
	
	// ���̎��Ԃ̂Ƃ����focus�����킹��B
	private void setFocusOnCurrentTime(ListView list) {
		ProgramDataManager progDataMgr = mDataViewFlow.getProgramDataMgr();
		if(0 < progDataMgr.getFocusedProgramList().size()) {
			Calendar now = Calendar.getInstance(Locale.JAPAN);
			
			ProgramDataManager progMgr = mDataViewFlow.getProgramDataMgr();
			List<Program> progs = progMgr.getFocusedProgramList();
			for(int i = 0; i < progs.size(); i++) {
				Program prog = progs.get(i);
				Calendar startTime = SugorokuonUtils.changeOnAirTimeToCalendar(prog.start);
				Calendar endTime = SugorokuonUtils.changeOnAirTimeToCalendar(prog.end);
				
				if(now.getTimeInMillis() >= startTime.getTimeInMillis() 
						&& now.getTimeInMillis() <= endTime.getTimeInMillis()) {
					list.invalidate();
					list.setSelection(i);
					progDataMgr.setFocusedIndex(i);
				}
			}
		}
	}
	
	private void setFocus() {
		if(0 < mDataViewFlow.getProgramDataMgr().getFocusedProgramList().size()) {
			ListView progList = (ListView) getView().findViewById(R.id.program_list);
			progList.invalidate();
			progList.setSelection(mDataViewFlow.getProgramDataMgr().getFocusedIndex());
		}
	}
	
	private void inflateListItems() {
		mAdapter.listItems.clear();
		
		StationDataManager stationMgr = DataViewFlow.getInstance().getStationDataMgr();
		if(null != stationMgr) {
			ProgramDataManager progMgr = DataViewFlow.getInstance().getProgramDataMgr();
			List<Program> progs = progMgr.getFocusedProgramList();
			for(Program prog : progs) {
				mAdapter.listItems.add(getView(prog, stationMgr.getFocusedIndex()));
			}			
		}
		
	}

	public View getView(Program prog, int stationIndex) {
		View item = null;
		
		// �I�X�X�����ǂ����ŁA�ǂݍ���layout���Ⴄ�B
		if(0 == stationIndex) {
			item = createItemForRecommendProgram(prog, R.layout.program_list_item_recommend);
		} else {
			item = createItemForTimeTable(prog, R.layout.program_list_item);
		}
		
		return item;
	}
	
	private View createItemForTimeTable(Program program, int layoutId) {
		// Item��inflate
		LayoutInflater inflater = getLayoutInflater(getArguments());
		View itemView = inflater.inflate(layoutId, null);
		
		// OnAir�̎��Ԃ𖄂߂�
		SimpleDateFormat formatStart = new SimpleDateFormat(
				getString(R.string.onair_start_time), Locale.US);
		SimpleDateFormat formatEnd = new SimpleDateFormat(
				getString(R.string.onair_end_time), Locale.US);
		
		long startTimeInMillis = 
			SugorokuonUtils.changeOnAirTimeToCalendar(program.start).getTimeInMillis();
		long endTimeInMillis = 
			SugorokuonUtils.changeOnAirTimeToCalendar(program.end).getTimeInMillis();

		TextView startTime = (TextView) itemView.findViewById(R.id.program_list_item_starttime);
		TextView endTime = (TextView) itemView.findViewById(R.id.program_list_item_endtime);

		startTime.setText(formatStart.format(new Date(startTimeInMillis)));
		endTime.setText(formatEnd.format(new Date(endTimeInMillis)));
		
		// Title
		TextView title = (TextView) itemView.findViewById(R.id.program_list_item_title);
		title.setText(program.title);
		
		// �p�[�\�i���e�B
		TextView per = (TextView) itemView.findViewById(R.id.program_list_item_personality);
		per.setText(program.personalities);
		
		return itemView;
	}
	
	private View createItemForRecommendProgram(Program program, int layoutId) {
		// ��{�I�ɂ�time table��item�Ƌ��ʁB
		View itemView = createItemForTimeTable(program, layoutId);
		
		// onAir�̓��t
		SimpleDateFormat formatDate = new SimpleDateFormat(
				getString(R.string.onair_date), Locale.US);
		long dateTimeInMillis = 
			SugorokuonUtils.changeOnAirTimeToCalendar(program.start).getTimeInMillis();
		TextView date = (TextView) itemView.findViewById(R.id.program_list_item_date);
		date.setText(formatDate.format(new Date(dateTimeInMillis)));
		
		// �ǂ̃��S
		StationDataManager stationMgr = DataViewFlow.getInstance().getStationDataMgr();
		ImageView logo = (ImageView) itemView.findViewById(R.id.program_list_item_station_logo);
		logo.setImageBitmap(stationMgr.getStationLogo(program.stationId));
		
		return itemView;
	}
	
	/*
	 * Program��������Ă��邽�߂�adapter�B
	 */
	private class ProgramListAdapter extends BaseAdapter {
		
		public List<View> listItems = new ArrayList<View>();
		
		public int getCount() {
			return listItems.size();
		}

		public Object getItem(int position) {
			return listItems.get(position);
		}

		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return listItems.get(position);
		}


		
	}
	
}
