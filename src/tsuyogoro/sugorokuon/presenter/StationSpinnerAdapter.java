/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.presenter;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.datatype.Station;
import tsuyogoro.sugorokuon.viewflow.DataViewFlow;
import tsuyogoro.sugorokuon.viewflow.StationDataManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StationSpinnerAdapter extends BaseAdapter {

	private Context mContext;
	
	public StationSpinnerAdapter(Context context) {
		mContext = context;
	}
	
	public int getCount() {
		StationDataManager mgr = DataViewFlow.getInstance().getStationDataMgr();
		if(null == mgr) {
			return 2;
		} else {
			return mgr.getStationInfo().size() + 2; // �������� + AppC�L���̕�
		}
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		super.getDropDownView(position, convertView, parent);
		return getView(position, convertView, parent);
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {					
		// Spinner item��view�𐶐��B
		LayoutInflater inflater = 
			(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View view = (0 == position) 
				? inflater.inflate(R.layout.stationlist_spinner_recommend_item, null)
				: inflater.inflate(R.layout.stationlist_spinner_item, null);
		
		TextView name = (TextView) view.findViewById(R.id.station_list_name);
		ImageView logo = (ImageView) view.findViewById(R.id.station_list_logo);
		
		// ��������
		if(0 == position) {
			name.setText(R.string.dropdown_list_recommend);
			logo.setImageResource(R.drawable.ic_launcher);
		}
		// app-C�L��
		else if(getCount() - 1 == position) {
			name.setText(R.string.dropdown_list_app_c_advertisement);
			logo.setImageResource(R.drawable.app_c_logo);
		}
		else {
			// Station list�̒��ł́Aposition-1�Ԗځi-1�̓I�X�X���ԑg�̕��j�B
			int posionInList = position - 1;

			// �ǂ̖��O��logo���Z�b�g�B
			StationDataManager mgr = DataViewFlow.getInstance().getStationDataMgr();
			Station station = mgr.getStationInfo().get(posionInList);
			name.setText(station.name);
			logo.setImageBitmap(mgr.getStationLogo(station.id));
		}
		
		return view;
	}

}
