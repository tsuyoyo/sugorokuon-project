/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.viewflow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AbstractHttpClient;

import tsuyogoro.sugorokuon.constant.Area;
import tsuyogoro.sugorokuon.constant.SugorokuonConst;
import tsuyogoro.sugorokuon.datatype.Station;
import tsuyogoro.sugorokuon.model.StationDatabaseAccessor;
import tsuyogoro.sugorokuon.model.StationListDownloader;
import tsuyogoro.sugorokuon.model.StationListParser.LogoSize;
import tsuyogoro.sugorokuon.settings.preference.AreaSettingPreference;
import tsuyogoro.sugorokuon.util.FileHandleUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class StationDataManager {
	
	/**
	 * StationDataManager�����Event���󂯎�邽�߂�listener�B
	 * 
	 * @author Tsuyoyo
	 *
	 */
	public static interface IEventListener {
		/**
		 * Focus�̓������Ă���station��index���ς�����ꍇ�A�ʒm���s���iUI�X���b�h�֒ʒm���͂��j�B
    	 * 0�̓I�X�X���ԑg�B1��̏ꍇ�AgetStationInfo()��index�́A1�Y����̂Œ��ӁB
    	 * 
		 * @param newIndex
		 */
		public void onStationIndexChanged(int newIndex);
	}
	
	// Logo�t�@�C����cache��u���f�B���N�g���̃p�X�BgetExternalStorage()�Ŏ���p�X�ɍ��B
	// �B���t�H���_�ɍ쐬���āAmedia scanner�Ɉ����|����Ȃ��悤�ɂ���B
	private static final String LOGO_CACHE_DIR = 
		"radiconcierge" + File.separator + ".stationlogo" + File.separator;
	
	private List<Station> mStationData = new ArrayList<Station>();
	
	private Map<String, Bitmap> mLogoData = new HashMap<String, Bitmap>();
	
	private int mFocusedIndex;
	
	/**
	 * StationDataManager����ʒm���󂯂����ꍇ�́A����list��add���邱�ƁB
	 * �K�v�����Ȃ�����K��remove���邱�ƁB
	 */
	public List<IEventListener> listeners = new ArrayList<IEventListener>();
	
	/**
	 * �R���X�g���N�^�B
	 * Preference����ۑ������l�����o���āAmFocusedIndex���������B
	 */
	public StationDataManager(Context context) {
		mFocusedIndex = 0;
		
		// TODO : focus�̕ۑ������܂������悤�ɂȂ����炱�����������B
		//mFocusedIndex = LastStationFocusPreference.lastFocusedIndex(context);
	}
	
	/**
	 * ����focus�̓������Ă���Station��index��Ԃ��B
	 * 0�̓I�X�X���ԑg�B1��̏ꍇ�AgetStationInfo()��index�́A1�Y����̂Œ��ӁB
	 * 
	 * @return
	 */
	public int getFocusedIndex() {
		// TODO : focus�̕ۑ������܂������悤�ɂȂ����炱�����������B
//		// ����focus���Ă���index��mStationData��size�𒴂���ꍇ��0��Ԃ��Ă����B
//		if(mFocusedIndex > mStationData.size()) {
//			return 0;
//		}
		return mFocusedIndex;
	}
	
	/**
	 * Station��focus���ς������A���̃��\�b�h�ŕύX�������邱�ƁB
	 * ���̃��\�b�h���Ă񂾂��ƁA�ʃR���e�L�X�g�ŊeListener�ɒʒm���͂��B
	 * 
	 * @param context
	 * @param newIndex
	 */
	public void setFocusedIndex(Context context, int newIndex) {

		setFocusedIndexAndSaveIfNeeded(context, newIndex);
		
		Handler h = new Handler(Looper.getMainLooper());
		h.post(new Runnable() {
			@Override
			public void run() {
				for(IEventListener listener : listeners) {
					listener.onStationIndexChanged(mFocusedIndex);
				}
			}
		});
	}
	
	private void setFocusedIndexAndSaveIfNeeded(Context context, int newIndex) {
		mFocusedIndex = newIndex;
		
		// TODO : focus�̕ۑ������܂������悤�ɂȂ����炱�����������B
		// �u�Ō�ɂ݂�Station���L������v��enable�ɂȂ��Ă�����Apreference��newIndex�����܂��B
//		if(KeepStationFocusPreference.getKeepStationFocusSettings(context)) {
//			LastStationFocusPreference.saveLastFocusedIndex(context, mFocusedIndex);
//		}
	}
	
	/**
	 * DB���烁�������load�ς݂́A�SStation����Ԃ��B
	 * 
	 * @return
	 */
	public List<Station> getStationInfo() {
		return mStationData;
	}
	
	/**
	 * ���������load�ς݂�bitmap����A�w�肵��station�̂��̂�Ԃ��B
	 * 
	 * @return
	 */
	public Bitmap getStationLogo(String stationId) {
		return mLogoData.get(stationId);
	}

	/**
	 * �ǂ̏����������i���̃N���X�̃����o�j�ɓǂݍ��ށB
	 * shouldUpdate��true�Ȃ�΁Anetwork����f�[�^��download�B
	 * �����łȂ����Database����download�ς݂̃f�[�^��ǂݍ��ށB
	 * 
	 * @param context
	 * @param shouldUpdate
	 * @param logoSize
	 * @param httpClient
	 * @return ����������COMPLETE_STATION_UPDATE�A���s������FAILED_STATION_UPDATE�B
	 */
	ViewFlowEvent loadData(Context context, boolean shouldUpdate, LogoSize logoSize,
			AbstractHttpClient httpClient) {
		ViewFlowEvent res = ViewFlowEvent.COMPLETE_STATION_UPDATE;
		
		// �Â��f�[�^����������S��clear�B
		mStationData.clear();
		mLogoData.clear();
		
		// shouldUpdate���w�肳�ꂽ��A�T�[�o����f�[�^��download����B
		if(shouldUpdate) {
			res = downloadStationData(context, logoSize, httpClient);
		}

		// downloadStationData�����s���Ă��Ȃ���Ώ������s�B
		if(res.equals(ViewFlowEvent.COMPLETE_STATION_UPDATE)) {
			// Database����f�[�^��ǂݍ���Ń����o�ɂ��܂��B
			StationDatabaseAccessor db = new StationDatabaseAccessor(context);
			mStationData = db.getStationData();
		
//			// �����t�H�[�J�X��0�i�I�X�X���ԑg�j
//			setFocusedIndexAndSaveIfNeeded(context, 0);
		
			// Logo�f�[�^�������o�ɂ��܂��B
			loadLogoData();
		}
		
		return res;
	}
	
	/*
	 * �ǂ̃f�[�^��download���āADatabase�ɂ��܂��B
	 */
	private ViewFlowEvent downloadStationData(Context context, LogoSize logoSize, 
			AbstractHttpClient httpClient) {
		ViewFlowEvent res = ViewFlowEvent.COMPLETE_STATION_UPDATE;		

		// Target�G���A��ݒ�l����ǂݍ��ށB
		List<Area> targetAreas = AreaSettingPreference.getTargetAreas(context);

		// Network����ǂ̃f�[�^��download�B
		StationListDownloader downloader = new StationListDownloader();
		List<Station> stationData = downloader.getStationList(
				targetAreas, logoSize, httpClient);
		
		// �Â��f�[�^��DB����폜�B
		StationDatabaseAccessor db = new StationDatabaseAccessor(context);
		db.clearStationData();
		
		// Download�Ɏ��s������FAILED_STATION_UPDATE��ԋp�B
		if(null == stationData) { 
			res = ViewFlowEvent.FAILED_STATION_UPDATE;
		} else {
			// �ǂ̃f�[�^��DB�ɏ������ށB
			db.insertStationData(stationData);

			// Logo�f�[�^��download���ADB�ɏ������ށB
			updateStationLogoCache(context, stationData, httpClient);
		}
		
		return res;
	}
	
	private void updateStationLogoCache(Context context, 
			List<Station> stationData, AbstractHttpClient httpClient) {
		
		// logo�t�H���_����ɂ���B
		FileHandleUtil.removeAllFileInFolder(getCacheDirectory());
		
		for(Station s : stationData) {
			// logo�̃f�[�^��download�B
			String filePath = downloadLogoData(s, httpClient);
		
			// DB�ɓo�^
			StationDatabaseAccessor db = new StationDatabaseAccessor(context);
			db.updateLogoInfo(s.id, filePath);
		}
	}
	
	private String downloadLogoData(Station s, AbstractHttpClient httpClient) {
		HttpGet httpGet = new HttpGet(s.logoUrl);
		HttpResponse httpRes;
		
		try {
			// logo�̃f�[�^��download�B
			httpRes = httpClient.execute(httpGet);
			InputStream logoDataStream = httpRes.getEntity().getContent();

			// logo�̃f�[�^���t�@�C���ɕۑ����āAfullpath��ԋp�B
			String fileName = s.id + ".png";
			return FileHandleUtil.saveDataToFile(
					logoDataStream, getCacheDirectory(), fileName);
			
		} catch(IOException e) {
			Log.e(SugorokuonConst.LOGTAG, 
					"fail to get " + s.id + ".png : " + e.getMessage());
		}
		return "";	
	}
	
	private String getCacheDirectory() {
		File ext = Environment.getExternalStorageDirectory();
    	return ext.getAbsolutePath() + File.separator + LOGO_CACHE_DIR;
	}
	
	private void loadLogoData() {
		for(Station station : mStationData) {
			File logoFile = new File(station.logoCachePath);
			try {
				FileInputStream is = new FileInputStream(logoFile);
				mLogoData.put(station.id, BitmapFactory.decodeStream(is));
			} catch(FileNotFoundException e) {
				Log.e(SugorokuonConst.LOGTAG, 
						"fail to load : " + station.logoCachePath + " " + e.getMessage());
			}
		}
	}
	
}
