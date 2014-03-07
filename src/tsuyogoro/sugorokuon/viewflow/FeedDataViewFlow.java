/**
 * Copyright (c) 
 * 2013 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.viewflow;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.impl.client.DefaultHttpClient;

import tsuyogoro.sugorokuon.constant.SugorokuonConst;
import tsuyogoro.sugorokuon.datatype.Feed;
import tsuyogoro.sugorokuon.model.FeedDownloader;
import android.content.Context;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.util.Log;

/**
 * Feed����download���邽�߂̃N���X�B
 * �ŋ߂�onAir�ȏ�����邽�߂Ɏg���B
 * 
 * @author Tsuyoyo
 *
 */
public class FeedDataViewFlow extends ViewFlowBase {

	// invokeDownloadFeed����Ԃ�l
	public static final int START_TASK_EXECUTION = 0;
	public static final int STATION_MANAGE_HAS_NOT_INITIALIZED = 1;
	public static final int NO_FEED_FOR_RECOMMEND_CATEGORY = 2;	
	
	private static FeedDataViewFlow sInstance;
	
	public static FeedDataViewFlow getInstance() {
		if(null == sInstance) {
			sInstance = new FeedDataViewFlow();
		}
		return sInstance;
	}
	
	// Key��StationID�B
	private Map<String, Feed> mFeedMap = new HashMap<String, Feed>();
	
	private LoadDataTask mLoadTask;
	
	/*
	 * �w�肵��station�i�R���X�g���N�^�Ŏw��j�́AonAir�����擾���邽�߂�task�B
	 */
	private class LoadDataTask extends AsyncTask<Context, Void, ViewFlowEvent> {
		
		private final String stationId;
		
		LoadDataTask(String _stationId) {
			stationId = _stationId;
		}
		
		@Override
		protected ViewFlowEvent doInBackground(Context... arg0) {
			// focusedStationId��feed���L���b�V���̒��ɖ�����΁Adownload���s���B
			if(!mFeedMap.containsKey(stationId)) {
				// feed�擾�J�n�B
				FeedDownloader downloader = new FeedDownloader();
				Feed feed = downloader.getFeed(stationId, new DefaultHttpClient());
			
				// getFeed��null��������download�Ɏ��s���Ă���B
				if(null == feed) {
					return ViewFlowEvent.FAILED_FEED_DONWLOAD;
				} else {
					mFeedMap.put(stationId, feed);
				}
			}
			return ViewFlowEvent.COMPLETE_FEED_DOWNLOAD;
		}
		
		@Override
		protected void onPostExecute(ViewFlowEvent result) {
			super.onPostExecute(result);
			notifyEvent(result);
		}
	}
	
	/**
	 * �t�H�[�J�X���������Ă���ǂ�Feed�̎擾���J�n����B
	 * ����������AonViewFlowEvent��COMPLETE_FEED_DOWNLOAD
	 * �i���s�����ꍇ��FAILED_FEED_DONWLOAD�j���ʒm�����B
	 * �������getFeed�ŁAdownload����feed��񂪎擾�ł���B
	 * ����Task�������Ă��鎞�́A����Task���L�����Z�����ĐV����task�𑖂点��B
	 * 
	 * @param context
	 * @return task�𖳎����点�邱�Ƃ��ł�����START_TASK_EXECUTION���Ԃ�B
	 */
	public int invokeDownloadFeed(Context context) {
		// StationDataMgr����set��������Fail�Ƃ���B
		StationDataManager stationMgr = DataViewFlow.getInstance().getStationDataMgr();
		if(null == stationMgr) {
			return STATION_MANAGE_HAS_NOT_INITIALIZED;
		}
		
		// ����focus�̓������Ă���station��ID���擾�B
		int focusedStationIndex = stationMgr.getFocusedIndex();

		// �I�X�X����focus���������Ă��鎞��feed���ɂ�������_���B
		if(0 == focusedStationIndex) {
			Log.e(SugorokuonConst.LOGTAG, "No feed for recommend programs.");
			return NO_FEED_FOR_RECOMMEND_CATEGORY;
		}
		String focusedStationId = stationMgr.getStationInfo().get(focusedStationIndex - 1).id;

		// ���ɑ����Ă���task������̂Ȃ�A�����cancel�B
		if(null != mLoadTask && mLoadTask.getStatus().equals(Status.RUNNING)) {
			mLoadTask.cancel(true);
		}		
		
		mLoadTask = new LoadDataTask(focusedStationId);
		mLoadTask.execute(context);
		
		return START_TASK_EXECUTION;
	}
	
	/**
	 * ����focus���������Ă���station�́A�ŋ�onAir�Ȃ�cache�������B
	 * reload���鎞�ɁAinvokeDownloadFeed()���O��call���邱�ƁB
	 * 
	 */
	public void removeCurrentFocusedStationCache() {
		// StationDataMgr����set��������Fail�Ƃ���B
		StationDataManager stationMgr = DataViewFlow.getInstance().getStationDataMgr();
		if(null != stationMgr) {
			int focusedStationIndex = stationMgr.getFocusedIndex();
			String focusedStationId = stationMgr.getStationInfo().get(focusedStationIndex - 1).id;
		
			// ����focusedStationId�̗v�f��map�ɓo�^����Ă������U�����B
			if(mFeedMap.containsKey(focusedStationId)) {
				mFeedMap.remove(focusedStationId);
			}
		}
	}
	
	/**
	 * ���݃t�H�[�J�X���������Ă���ǂ�Feed���A�L���b�V������擾����B
	 * �L���b�V��������Ȃ�������i�܂��擾���Ă��Ȃ�������jnull���Ԃ�̂ŁA
	 * ���̏ꍇ��invokeDownloadFeed��download���s�����B
	 * 
	 */
	public Feed getFocusedStationFeed() {
		String focusedStationId = getFocusedStationId();
		if(null != focusedStationId) {
			return mFeedMap.get(focusedStationId);
		}
		return null;
	}
	
	private String getFocusedStationId() {
		// StationDataMgr����set��������Fail�Ƃ���B
		StationDataManager stationMgr = DataViewFlow.getInstance().getStationDataMgr();
		if(null == stationMgr || 0 == stationMgr.getFocusedIndex()) {
			return null;
		}
		return stationMgr.getStationInfo().get(stationMgr.getFocusedIndex() - 1).id;
	}
	
}
