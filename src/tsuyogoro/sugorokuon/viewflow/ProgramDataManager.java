/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.viewflow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.impl.client.AbstractHttpClient;

import tsuyogoro.sugorokuon.R;
import tsuyogoro.sugorokuon.datatype.OnedayTimetable;
import tsuyogoro.sugorokuon.datatype.Program;
import tsuyogoro.sugorokuon.datatype.Station;
import tsuyogoro.sugorokuon.model.ProgramDatabaseAccessor;
import tsuyogoro.sugorokuon.model.ProgramListDownloader;
import tsuyogoro.sugorokuon.settings.preference.RecommendWordPreference;
import tsuyogoro.sugorokuon.util.SugorokuonUtils;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.analytics.tracking.android.EasyTracker;

public class ProgramDataManager {

	/*
	 * updateProgramDatabase�̐i�����󂯎�邽�߂�Interface�B
	 */
	static interface IUpdateProgressListener {
		public void onProgressUpdateProgram(int prog, int max);
	}
	
	/**
	 * ProgramDataManager�����Event���󂯎�邽�߂�listener�B
	 * 
	 * @author Tsuyoyo
	 *
	 */
	public static interface IEventListener {
		/**
		 * focus����Ă���index���ς�������ɒʒm���󂯂�B
		 * �ʒm��UI�X���b�h�ɓ͂��B
		 * 
		 * @param newIndex
		 */
		public void onFocusedProgramIndexChanged(int newIndex);
		
		/**
		 * focus����Ă�����t���ς�������ɒʒm���󂯂�B
		 * �ʒm��UI�X���b�h�ɓ͂��B
		 * 
		 * @param newIndex
		 */
		public void onFocusedProgramDateChanged(Calendar newDate);
	}
	
	// ����focus�̓������Ă���ԑglist�B
	private List<Program> mPrograms = new ArrayList<Program>();
	
	/**
	 * ProgramDataManager����̒ʒm���󂯂�l������List�B
	 */
	public List<IEventListener> listeners = new ArrayList<IEventListener>();
	
	/**
	 *  ����focus�̓������Ă���index
	 */
	private int mFocusedIndex = 0;
	
	/**
	 *  ����focus�̓������Ă�����t �iRecommend��focused���ƁA������null�ɂȂ�j
	 */
	private Calendar mFocusedDate;
	
	/**
	 * ���݃t�H�[�J�X�̓������Ă���ԑg���X�g��ԋp�B
	 * ViewFlow��load���Ă���g�����ƁB
	 * 
	 * @return
	 */
	public List<Program> getFocusedProgramList() {
		return mPrograms;
	}
	
	/**
	 * DB��store����Ă���A���ׂẴI�X�X���ԑg����DB���烁���o�ϐ���load����B
	 * ���̌�AgetPrograms()�ł��̏����擾�\�B
	 * 
	 * @param context
	 * @return
	 */
	public void loadRecommendPrograms(Context context) {
		mPrograms = loadRecommendProgramsFromDB(context);
		setFocusedIndex(0);
		setFocusedDate(null);
	}

	/**
	 * ��Focus���������Ă��鎟�̓��̔ԑg�����ADB���烁���o�ϐ���load����B�B
	 * ���̌�AgetPrograms()�ł��̏����擾�\�B
	 * ��focus�̓������Ă���j�������j�Ȃ�i�����͖����̂Łj�������Ȃ��i���̏ꍇ��false���Ԃ�j�B
	 * 
	 * @param context
	 * @param stationId
	 * @return �؂�ւ��������s��ꂽ��true�B
	 */	
	public boolean loadNextdayTimetable(Context context, String stationId) {
		boolean res = false;
		Calendar focusedDate = getFocusedCalendar();
		if(Calendar.SUNDAY != focusedDate.get(Calendar.DAY_OF_WEEK)) {
			focusedDate.add(Calendar.DATE, 1);
			loadOnedayTimetable(context, focusedDate, stationId);
			res = true;
		}
		return res;
	}
	
	/**
	 * ��Focus���������Ă���O�̓��̔ԑg�����ADB���烁���o�ϐ���load����B�B
	 * ���̌�AgetPrograms()�ł��̏����擾�\�B
	 * ��focus�̓������Ă���j�������j�Ȃ�i�O���͖����̂Łj�������Ȃ��i���̏ꍇ��false���Ԃ�j�B
	 * 
	 * @param context
	 * @param stationId
	 * @return �؂�ւ��������s��ꂽ��true�B
	 */		
	public boolean loadPreviousdayTimetable(Context context, String stationId) {
		boolean res = false;
		Calendar focusedDate = getFocusedCalendar();
		if(Calendar.MONDAY != focusedDate.get(Calendar.DAY_OF_WEEK)) {
			focusedDate.add(Calendar.DATE, -1);
			loadOnedayTimetable(context, focusedDate, stationId);
			res = true;
		}
		return res;
	}
	
	/**
	 * �w�肵�����́A�w�肵���ǂ̂P�����̔ԑg�����ADB���烁���o�ϐ���load����B�B
	 * ���̌�AgetPrograms()�ł��̏����擾�\�B
	 * 
	 * @param context
	 * @param date 		YY/MM/DD�܂Ŏg���B
	 * @param stationId
	 */
	public void loadOnedayTimetable(Context context,
			Calendar date, String stationId) {
		ProgramDatabaseAccessor db = new ProgramDatabaseAccessor(context);
		OnedayTimetable onedayData = db.getTimetable(date, stationId);
		mPrograms = onedayData.programs;
		setFocusedIndex(0);
		setFocusedDate(date);
	}
	
	/**
	 * DB��Store����Ă���I�X�X���ԑg�ŁAnow���_�ł܂���������Ă��Ȃ����̂�load�B
	 * 
	 * @param now 	YY/MM/DD/HH/MM �̏��܂ł��g���B
	 * @return �������������ۂ̃��X�g���Ԃ�B
	 */
	public void loadRecommendProgramsNotOnAirYet(Context context, 
			Calendar now) {
		// �b�����낦�ĕ����ɁB
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		
		mPrograms.clear();
		List<Program> recommends = loadRecommendProgramsFromDB(context);		
		for(Program p : recommends) {
			Calendar onAirTime = SugorokuonUtils.changeOnAirTimeToCalendar(p.start);
			if(now.getTimeInMillis() < onAirTime.getTimeInMillis()) {
				mPrograms.add(p);
			}
		}
		setFocusedIndex(0);
		setFocusedDate(null);
	}
	
	/**
	 * DB��store����Ă���I�X�X���ԑg����A�w�肵���������Ԃ̂��̂��擾�B
	 * ���낻�����or�����J�n��Notification���������Ɏg���z��B
	 * 
	 * @param date	YY/MM/DD/HH/MM �܂ŗ��p�B
	 * @return start�������Adate�Ɉ�v������̂̃��X�g�B�����������̃��X�g�B
	 */
	public List<Program> getRecommendPrograms(Context context, Calendar date) {
		List<Program> results = new ArrayList<Program>();
		List<Program> recommends = loadRecommendProgramsFromDB(context);
		
		// ��v�����Ȃ��Ƃ����Ȃ��̂ŁAYY/MM/DD/HH/MM�����ꂼ���r�B
		for(Program p : recommends) {
			Calendar onAirTime = SugorokuonUtils.changeOnAirTimeToCalendar(p.start);
			if((date.get(Calendar.YEAR)   == onAirTime.get(Calendar.YEAR))
			&& (date.get(Calendar.MONTH)  == onAirTime.get(Calendar.MONTH))
			&& (date.get(Calendar.DATE)   == onAirTime.get(Calendar.DATE))
			&& (date.get(Calendar.HOUR_OF_DAY) == onAirTime.get(Calendar.HOUR_OF_DAY))
			&& (date.get(Calendar.MINUTE) == onAirTime.get(Calendar.MINUTE))) {
				results.add(p);
			}
		}
		return results;
	}
	
	/**
	 * DB��store����Ă���I�X�X���ԑg�ŁA�܂�on air���Ă��Ȃ��ԑg��list���擾�B
	 * on air���Ƀ\�[�g����Č��ʂ��Ԃ����B
	 * 
	 * @param context
	 * @return
	 */
	public List<Program> getRecommendProgramsBaforeOnAir(Context context) {
		// recommends�͍ŏ����玞�ԂŃ\�[�g����Ă���B
		List<Program> recommends = loadRecommendProgramsFromDB(context);

		Calendar now = Calendar.getInstance(Locale.JAPAN);
		while(recommends.size() > 0) {
			Program p = recommends.get(0);
			
			// now���onAirTime���O��������Arecommends����remove���Ă����B
			// onAirTime��now�����ɂȂ����Ƃ����break�B
			Calendar onAirTime = SugorokuonUtils.changeOnAirTimeToCalendar(p.start);
			if(onAirTime.getTimeInMillis() < now.getTimeInMillis()) {
				recommends.remove(p);
			} else {
				break;
			}
		}
		return recommends;
	}
	
	/**
	 * DB��recommend flag���X�V����B �I�X�X�����[�h�̐ݒ肪�ς�������Ɏg�����ƁB
	 * 
	 * @param context
	 */
	public void updateRecommendPrograms(Context context) {
		ProgramDatabaseAccessor db = new ProgramDatabaseAccessor(context);
		db.updateRecommendPrograms(new ProgramRecommender(context));
	}
	
	/**
	 * List�ŁA���[�U�[�^�b�v�ɂ����focus���ς������ĂԁB
	 * 
	 * @param newIndex
	 */
	public void setFocusedIndex(int newIndex) {
		// TODO : List������ۂ̎��͒ʒm���Ȃ�
		
		mFocusedIndex = newIndex;
		
		// Main�X���b�h�ցA���̕ύX�ʒm�𑗂�B
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {
			@Override
			public void run() {
				for(IEventListener listener : listeners) {
					listener.onFocusedProgramIndexChanged(mFocusedIndex);
				}
			}
		});
		
	}
	
	/**
	 * ���ݔԑgList��ŁA�ǂ���focus���������Ă��邩��Ԃ��B
	 * 
	 * @return
	 */
	public int getFocusedIndex() {
		return mFocusedIndex;
	}
	
	/**
	 * ���݁A�����̔ԑg��focus���������Ă��邩��Ԃ��B
	 * Recommend��focused���ƁA������null�ɂȂ�B
	 * ���t�؂�ւ��̕��i�ɗp������z��B
	 * 
	 * @return 
	 */
	public Calendar getFocusedCalendar() {
		return mFocusedDate;
	}
	
	/*
	 * Date�̍X�V�́A��{�I��loadXXX���Ă񂾂Ƃ��ɍs����B
	 */
	private void setFocusedDate(Calendar newDate) {
		mFocusedDate = newDate;
		
		// Main�X���b�h�ցA���̕ύX�ʒm�𑗂�B
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {
			@Override
			public void run() {
				for(IEventListener listener : listeners) {
					listener.onFocusedProgramDateChanged(mFocusedDate);
				}
			}
		});
	}
	
	/**
	 * Program�̏����l�b�g���[�N������Ȃ����āADB���X�V����B
	 *  
	 * @param context
	 * @param targetStations
	 * @param httpClient
	 * @return ����������COMPLETE_RECOMMEND_UPDATE�A���s������FAILED_DATA_UPDATE���Ԃ�B
	 */
	ViewFlowEvent updateProgramDatabase(Context context, 
			List<Station> targetStations, AbstractHttpClient httpClient, 
			IUpdateProgressListener listener) {

		ViewFlowEvent res = ViewFlowEvent.COMPLETE_RECOMMEND_UPDATE;
		
		// DB���N���A�B
		ProgramDatabaseAccessor db = new ProgramDatabaseAccessor(context);
		db.clearAllProgramData();
		
		// �n�߂�O�ɍŏ���progress�B
		listener.onProgressUpdateProgram(0, targetStations.size());
		
		// �estation�̂P�T�ԕ��̔ԑg�f�[�^���Ƃ�ADB��store���Ă����B
		ProgramListDownloader progDownloader = new ProgramListDownloader();		
		ProgramRecommender recommender = new ProgramRecommender(context);
		
		for(int i=0; i<targetStations.size(); i++) {
			Station station = targetStations.get(i);
			
			List<OnedayTimetable> timeTable = 
					progDownloader.getWeeklyTimeTable(station.id, httpClient);
			
			if(null != timeTable) {
				// Download�����f�[�^��DB�փX�g�A����B
				for(OnedayTimetable dayTimeTable : timeTable) {
					db.insertOnedayTimetable(dayTimeTable, recommender);
				}
			
				// Progress�𑗂�B
				listener.onProgressUpdateProgram(i+1, targetStations.size());
			} else {
				res = ViewFlowEvent.FAILED_DATA_UPDATE;
				break;
			}
		}
		
		// ��������recommend����GA�ɑ���B
		List<String> recommends = RecommendWordPreference.getKeyWord(context);
		String label = "";
		for(String l : recommends) {
			if(l!=null && 0 < l.length()) {
				label += l + " , ";
			}
		}
		EasyTracker.getInstance().setContext(context);
		EasyTracker.getTracker().trackEvent(
				context.getText(R.string.ga_event_category_program_update).toString(),
				context.getText(R.string.ga_event_action_download_from_web).toString(), 
				label, null);
		
		return res;
	}
	
	private List<Program> loadRecommendProgramsFromDB(Context context) {
		ProgramDatabaseAccessor db = new ProgramDatabaseAccessor(context);
		return db.getAllRecommendPrograms();
	}
}
