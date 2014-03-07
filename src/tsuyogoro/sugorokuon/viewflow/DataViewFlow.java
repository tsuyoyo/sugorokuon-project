/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.viewflow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.impl.client.DefaultHttpClient;

import tsuyogoro.sugorokuon.datatype.Station;
import tsuyogoro.sugorokuon.model.StationListParser.LogoSize;
import tsuyogoro.sugorokuon.settings.UpdatedDateManager;
import tsuyogoro.sugorokuon.viewflow.ProgramDataManager.IUpdateProgressListener;
import android.content.Context;
import android.os.AsyncTask;

public class DataViewFlow extends ViewFlowBase {
	
	/**
	 *  onProgress�̑�1����(whatsRunning)�Ɏg����萔�B
	 *  invokeLoadData��call�����ォ��COMPLETE_DATA_UPDATECOMPLETE������܂ł̊ԁB
	 */
	public static final int PROGRESS_LOAD_DATA = 0;
	
	private static final LogoSize LOGO_SIZE = LogoSize.LARGE;
	
	private static DataViewFlow sInstance;

	// Station���B
	private StationDataManager mStationDataMgr;
	
	// Program���B
	private ProgramDataManager mProgramDataMgr;

	/*
	 * �����o��Station���AProgram����ǂݍ��܂��邽�߂�AsyncTask.
	 */
	private class LoadDataTask extends AsyncTask<Context, Integer, ViewFlowEvent> 
		implements IUpdateProgressListener {

		@Override
		protected ViewFlowEvent doInBackground(Context... params) {
			ViewFlowEvent res = ViewFlowEvent.COMPLETE_DATA_UPDATECOMPLETE;
			Context context = params[0];

			boolean shouldDataUpdated = shouldDataUpdated(context);
			
			// Station�f�[�^��mStationDataMgr�̒���load����B
			StationDataManager stationDataMgr = new StationDataManager(context);
			ViewFlowEvent stationLoadRes = stationDataMgr.loadData(context, 
					shouldDataUpdated, LOGO_SIZE, new DefaultHttpClient());
			
			// Station�f�[�^�̎擾�Ɏ��s������return����B
			if(!ViewFlowEvent.COMPLETE_STATION_UPDATE.equals(stationLoadRes)) {
				return stationLoadRes;
			}
			
			// Program�f�[�^��mProgramDataMgr�̒���load�B
			if(shouldDataUpdated) {
				DefaultHttpClient client = new DefaultHttpClient();
				List<Station> stations = stationDataMgr.getStationInfo();
				if(ViewFlowEvent.FAILED_DATA_UPDATE.equals(
						mProgramDataMgr.updateProgramDatabase(
								context, stations, client , this))) {
					return ViewFlowEvent.FAILED_DATA_UPDATE;
				}
			}
			
			// �����t�H�[�J�X�̓I�X�X���ԑg�i�ŁA�܂������J�n���ĂȂ����́j
			Calendar now = Calendar.getInstance(Locale.JAPAN);
			mProgramDataMgr.loadRecommendProgramsNotOnAirYet(context, now);
			
			// Network����f�[�^�̍X�V���s�����̂Ȃ�΁ALastUpdatedDate���X�V
			if(shouldDataUpdated) {
				UpdatedDateManager.getInstance(context).updateLastUpdate();
			}
			
			// �����̃^�C�~���O��StationDataMgr�����o���X�V�B
			// mStationDataMgr��null���ۂ���load���I��������̔���Ȃ̂ŁB
			mStationDataMgr = stationDataMgr;
			
			return res;
		}
		
		@Override
		protected void onPostExecute(ViewFlowEvent result) {
			super.onPostExecute(result);
			
			// onViewFlowEvent�̒���unregister���Ă΂�Ă���肪�����悤�ɁA�A
			// Listener�z����R�s�[���Ă���elistener�֒ʒm���s���B
			List<IViewFlowListener> listeners = new ArrayList<IViewFlowListener>();
			listeners.addAll(getListeners());
			for(IViewFlowListener listener : listeners) {
				listener.onViewFlowEvent(result);
			}
			
			// �����FAsyncTask��status���A������completed�ɐݒ肳��Ȃ����Ƃ�����B
			// �Ȃ̂ŁAmLoadTask��null���ǂ����ŁARunning���ǂ����𔻒肷��d�g�݂ɂ����B
			mLoadTask = null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			notifyProgress(PROGRESS_LOAD_DATA, values[0], values[1]);
		}

		@Override
		public void onProgressUpdateProgram(int prog, int max) {
			publishProgress(prog, max);
		}
	}
	
	private LoadDataTask mLoadTask;
	
	protected DataViewFlow() {
		super();
		mProgramDataMgr = new ProgramDataManager();
	}
	
	/**
	 * DataViewFlow�C���X�^���X��Ԃ��B
	 * 
	 * @return
	 */
	public static DataViewFlow getInstance() {
		if(null == sInstance) {
			sInstance = new DataViewFlow();
		}
		return sInstance;
	}
	
	/**
	 * �f�[�^��ViewFlow��load���ׂ����ǂ����B
	 * true���Ԃ��Ă�����AViewFlow���g���O��invokeLoadData���ĂԂ��ƁB
	 * 
	 * @return
	 */
	public boolean shouldLoadData() {
		return (null == mStationDataMgr);
	}
	
	/**
	 * Update�����ǂ����iupdate���s��AsyncTask�����s�����ǂ����j�B
	 * 
	 * @return
	 */
	public boolean isUpdating() {
		boolean res = false;
		if(null != mLoadTask) {
			res = true;
		}
		return res;
	}
		
	/**
	 * �����o�ϐ��ւ̃f�[�^��load���J�n����B
	 * ����Database�Ƀf�[�^���i�[����Ă���΂������ǂނ��A�������network�������Ă���B
	 * ��������ƁAViewFlowListener�֒ʒm�������B DB�Ƀf�[�^�����邩�ǂ����ŏ������Ԃ��傫���قȂ�̂Œ��ӁB
	 * 
	 * @param context
	 */
	public void invokeLoadData(Context context) {
		mLoadTask = new LoadDataTask();
		mLoadTask.execute(context);
	}
	
	/**
	 * StationData���Ǘ�����AStationDataManager�N���X�̃C���X�^���X��Ԃ��B
	 * 
	 * @return �f�[�^��load���I����Ă��Ȃ�����null���Ԃ�B
	 */
	public StationDataManager getStationDataMgr() {
		return mStationDataMgr;
	}
	
	/**
	 * ProgramData�ARecommendProgramData���Ǘ�����A
	 * ProgramDataManager�N���X�̃C���X�^���X��Ԃ��B
	 * 
	 * @return�@
	 */
	public ProgramDataManager getProgramDataMgr() {
		return mProgramDataMgr;
	}
	
	/**
	 * �ǂ��؂�ւ������ĂԁB
	 * ProgramDataManager��program���A���̋ǂ̍����̔ԑg���X�g�ɂȂ�B
	 * 
	 * @param context
	 * @param newIndex
	 */
	public void setStationFocusIndex(Context context, int newIndex) {
		if(null != mStationDataMgr && null != mProgramDataMgr) {
			mStationDataMgr.setFocusedIndex(context, newIndex);
			
			Calendar now = Calendar.getInstance(Locale.JAPAN);
			if(0 == newIndex) { // index��0��station�́A�u�I�X�X���ԑg�v
				mProgramDataMgr.loadRecommendProgramsNotOnAirYet(context, now);
			} else {
				int i = newIndex - 1;
				String focusedStation = mStationDataMgr.getStationInfo().get(i).id;
				
				// �ߑO0���`5���̊Ԃɔԑg�\��\�����悤�Ƃ����ꍇ�Aload���ׂ��ԑg�\�͑O���̓��t�B
				// ���j�́u�O���v�������̂ŁAfail safe�ŁB
				if(5 > now.get(Calendar.HOUR_OF_DAY) 
						&& Calendar.DAY_OF_WEEK != Calendar.MONDAY) {
					now.add(Calendar.DATE, -1);
				}
				mProgramDataMgr.loadOnedayTimetable(context, now, focusedStation);
			}
		} 
	}
	
	/*
	 * SharedPreference�ɕۑ����ꂽ�ŏI�X�V���������Ĥ
	 * DB��update�����ׂ����ǂ�����Ԃ��B
	 */
	private boolean shouldDataUpdated(Context context) {
		Calendar now = Calendar.getInstance(Locale.JAPAN);
		return UpdatedDateManager.getInstance(context).shouldUpdate(now);
	}
	
}
