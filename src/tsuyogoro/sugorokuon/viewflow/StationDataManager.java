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
	 * StationDataManagerからのEventを受け取るためのlistener。
	 * 
	 * @author Tsuyoyo
	 *
	 */
	public static interface IEventListener {
		/**
		 * Focusの当たっているstationのindexが変わった場合、通知が行く（UIスレッドへ通知が届く）。
    	 * 0はオススメ番組。1上の場合、getStationInfo()のindexは、1つズレるので注意。
    	 * 
		 * @param newIndex
		 */
		public void onStationIndexChanged(int newIndex);
	}
	
	// Logoファイルのcacheを置くディレクトリのパス。getExternalStorage()で取れるパスに作る。
	// 隠しフォルダに作成して、media scannerに引っ掛からないようにする。
	private static final String LOGO_CACHE_DIR = 
		"radiconcierge" + File.separator + ".stationlogo" + File.separator;
	
	private List<Station> mStationData = new ArrayList<Station>();
	
	private Map<String, Bitmap> mLogoData = new HashMap<String, Bitmap>();
	
	private int mFocusedIndex;
	
	/**
	 * StationDataManagerから通知を受けたい場合は、このlistにaddすること。
	 * 必要無くなったら必ずremoveすること。
	 */
	public List<IEventListener> listeners = new ArrayList<IEventListener>();
	
	/**
	 * コンストラクタ。
	 * Preferenceから保存した値を取り出して、mFocusedIndexを初期化。
	 */
	public StationDataManager(Context context) {
		mFocusedIndex = 0;
		
		// TODO : focusの保存がうまくいくようになったらこっちを検討。
		//mFocusedIndex = LastStationFocusPreference.lastFocusedIndex(context);
	}
	
	/**
	 * 現在focusの当たっているStationのindexを返す。
	 * 0はオススメ番組。1上の場合、getStationInfo()のindexは、1つズレるので注意。
	 * 
	 * @return
	 */
	public int getFocusedIndex() {
		// TODO : focusの保存がうまくいくようになったらこっちを検討。
//		// 現在focusしているindexがmStationDataのsizeを超える場合は0を返しておく。
//		if(mFocusedIndex > mStationData.size()) {
//			return 0;
//		}
		return mFocusedIndex;
	}
	
	/**
	 * Stationのfocusが変わったら、このメソッドで変更をかけること。
	 * このメソッドを呼んだあと、別コンテキストで各Listenerに通知が届く。
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
		
		// TODO : focusの保存がうまくいくようになったらこっちを検討。
		// 「最後にみたStationを記憶する」がenableになっていたら、preferenceにnewIndexをしまう。
//		if(KeepStationFocusPreference.getKeepStationFocusSettings(context)) {
//			LastStationFocusPreference.saveLastFocusedIndex(context, mFocusedIndex);
//		}
	}
	
	/**
	 * DBからメモリ上のload済みの、全Station情報を返す。
	 * 
	 * @return
	 */
	public List<Station> getStationInfo() {
		return mStationData;
	}
	
	/**
	 * メモリ上にload済みのbitmapから、指定したstationのものを返す。
	 * 
	 * @return
	 */
	public Bitmap getStationLogo(String stationId) {
		return mLogoData.get(stationId);
	}

	/**
	 * 局の情報をメモリ（このクラスのメンバ）に読み込む。
	 * shouldUpdateがtrueならば、networkからデータをdownload。
	 * そうでなければDatabaseからdownload済みのデータを読み込む。
	 * 
	 * @param context
	 * @param shouldUpdate
	 * @param logoSize
	 * @param httpClient
	 * @return 成功したらCOMPLETE_STATION_UPDATE、失敗したらFAILED_STATION_UPDATE。
	 */
	ViewFlowEvent loadData(Context context, boolean shouldUpdate, LogoSize logoSize,
			AbstractHttpClient httpClient) {
		ViewFlowEvent res = ViewFlowEvent.COMPLETE_STATION_UPDATE;
		
		// 古いデータがあったら全てclear。
		mStationData.clear();
		mLogoData.clear();
		
		// shouldUpdateを指定されたら、サーバからデータをdownloadする。
		if(shouldUpdate) {
			res = downloadStationData(context, logoSize, httpClient);
		}

		// downloadStationDataが失敗していなければ処理続行。
		if(res.equals(ViewFlowEvent.COMPLETE_STATION_UPDATE)) {
			// Databaseからデータを読み込んでメンバにしまう。
			StationDatabaseAccessor db = new StationDatabaseAccessor(context);
			mStationData = db.getStationData();
		
//			// 初期フォーカスは0（オススメ番組）
//			setFocusedIndexAndSaveIfNeeded(context, 0);
		
			// Logoデータをメンバにしまう。
			loadLogoData();
		}
		
		return res;
	}
	
	/*
	 * 局のデータをdownloadして、Databaseにしまう。
	 */
	private ViewFlowEvent downloadStationData(Context context, LogoSize logoSize, 
			AbstractHttpClient httpClient) {
		ViewFlowEvent res = ViewFlowEvent.COMPLETE_STATION_UPDATE;		

		// Targetエリアを設定値から読み込む。
		List<Area> targetAreas = AreaSettingPreference.getTargetAreas(context);

		// Networkから局のデータをdownload。
		StationListDownloader downloader = new StationListDownloader();
		List<Station> stationData = downloader.getStationList(
				targetAreas, logoSize, httpClient);
		
		// 古いデータをDBから削除。
		StationDatabaseAccessor db = new StationDatabaseAccessor(context);
		db.clearStationData();
		
		// Downloadに失敗したらFAILED_STATION_UPDATEを返却。
		if(null == stationData) { 
			res = ViewFlowEvent.FAILED_STATION_UPDATE;
		} else {
			// 局のデータをDBに書き込む。
			db.insertStationData(stationData);

			// Logoデータをdownloadし、DBに書き込む。
			updateStationLogoCache(context, stationData, httpClient);
		}
		
		return res;
	}
	
	private void updateStationLogoCache(Context context, 
			List<Station> stationData, AbstractHttpClient httpClient) {
		
		// logoフォルダを空にする。
		FileHandleUtil.removeAllFileInFolder(getCacheDirectory());
		
		for(Station s : stationData) {
			// logoのデータをdownload。
			String filePath = downloadLogoData(s, httpClient);
		
			// DBに登録
			StationDatabaseAccessor db = new StationDatabaseAccessor(context);
			db.updateLogoInfo(s.id, filePath);
		}
	}
	
	private String downloadLogoData(Station s, AbstractHttpClient httpClient) {
		HttpGet httpGet = new HttpGet(s.logoUrl);
		HttpResponse httpRes;
		
		try {
			// logoのデータをdownload。
			httpRes = httpClient.execute(httpGet);
			InputStream logoDataStream = httpRes.getEntity().getContent();

			// logoのデータをファイルに保存して、fullpathを返却。
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
