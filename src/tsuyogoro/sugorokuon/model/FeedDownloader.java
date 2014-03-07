/**
 * Copyright (c) 
 * 2013 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.model;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AbstractHttpClient;

import tsuyogoro.sugorokuon.constant.SugorokuonConst;
import tsuyogoro.sugorokuon.datatype.Feed;
import android.util.Log;

/**
 * �Ȃ�Feed��download���āAFeed�C���X�^���X�����N���X�B
 * 
 * @author Tsuyoyo
 *
 */
public class FeedDownloader {
	
	private final String FEED_URL = "http://radiko.jp/v2/station/feed_PC/";
	
	/**
	 * Feed��download����B
	 * ���݂̂Ƃ���A�Ȃ�onAir�����擾���邽�߂݂̂Ɏg���i���̏������邪�A�S�ēǂݔ�΂��j�B
	 * 
	 * @param stationId
	 * @param client
	 * @return �擾�Ɏ��s�����ꍇ�Anull���Ԃ�B������logcat�����邱�ƁB
	 */
	public Feed getFeed(String stationId, AbstractHttpClient client) {
		Feed feed = null;
		
		String url = createFeedUrl(stationId);
		HttpGet httpGet = new HttpGet(url);
		HttpResponse httpRes;
		try {
			// Download station XML data.
			httpRes = client.execute(httpGet);
			
			// Status code��400�ȏ�Ȃ�΃G���[�B
			// �Q�l �F http://www.studyinghttp.net/status_code
			int statusCode = httpRes.getStatusLine().getStatusCode();
			if(400 <= statusCode) {
				Log.e(SugorokuonConst.LOGTAG, "Failed to download feed : status code " + statusCode);
			} else {
				// �T�[�o�����response��parse�B
				FeedParser feedParser = new FeedParser(httpRes.getEntity().getContent(), "UTF-8");
				feed = feedParser.parse();
			}
		} catch(IOException e) {
			Log.e(SugorokuonConst.LOGTAG, "IOException at getFeed : " + e.getMessage());
		}
		
		return feed;
	}
	
	private String createFeedUrl(String stationId) {
		return FEED_URL + stationId + ".xml";
	}
	
}
