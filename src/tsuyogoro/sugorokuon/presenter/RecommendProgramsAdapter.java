/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.presenter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.datatype.Program;
import tsuyogoro.sugorokuon.util.SugorokuonUtils;
import tsuyogoro.sugorokuon.viewflow.DataViewFlow;
import tsuyogoro.sugorokuon.viewflow.ProgramDataManager;
import tsuyogoro.sugorokuon.viewflow.StationDataManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RecommendProgramsAdapter extends BaseAdapter {

	private Context mContext;
	
	public RecommendProgramsAdapter(Context context) {
		mContext = context;
	}
	
	public int getCount() {
		ProgramDataManager dataMgr = DataViewFlow.getInstance().getProgramDataMgr();
		int size = 0;
		if(null != dataMgr) {
			size = dataMgr.getFocusedProgramList().size();
		}
		return size;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View item = null;
		
		// �ԑg�����擾
		ProgramDataManager progDataMgr = DataViewFlow.getInstance().getProgramDataMgr();
		Program prog = progDataMgr.getFocusedProgramList().get(position);
		
		// �I�X�X�����ǂ����ŁA�ǂݍ���layout���Ⴄ�B
		StationDataManager stationMgr = DataViewFlow.getInstance().getStationDataMgr();
		if(null != stationMgr) {
			if(0 == stationMgr.getFocusedIndex()) {
				item = createItemForRecommendProgram(prog, R.layout.program_list_item_recommend);
			} else {
				item = createItemForTimeTable(prog, R.layout.program_list_item);
			}
		}
		
		return item;
	}
	
	private View createItemForTimeTable(Program program, int layoutId) {
		// Item��inflate
		LayoutInflater inflater = 
			(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(layoutId, null);
		
		// OnAir�̎��Ԃ𖄂߂�
		SimpleDateFormat formatStart = new SimpleDateFormat(
				mContext.getString(R.string.onair_start_time), Locale.US);
		SimpleDateFormat formatEnd = new SimpleDateFormat(
				mContext.getString(R.string.onair_end_time), Locale.US);
		
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
		View itemView = createItemForTimeTable(program, layoutId);
		
		// onAir�̓��t
		SimpleDateFormat formatDate = new SimpleDateFormat(
				mContext.getString(R.string.onair_date), Locale.US);
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
	
}
