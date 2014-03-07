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
 * 曲のFeedをdownloadして、Feedインスタンスを作るクラス。
 * 
 * @author Tsuyoyo
 *
 */
public class FeedDownloader {
	
	private final String FEED_URL = "http://radiko.jp/v2/station/feed_PC/";
	
	/**
	 * Feedをdownloadする。
	 * 現在のところ、曲のonAir情報を取得するためのみに使う（他の情報も取れるが、全て読み飛ばす）。
	 * 
	 * @param stationId
	 * @param client
	 * @return 取得に失敗した場合、nullが返る。原因はlogcatを見ること。
	 */
	public Feed getFeed(String stationId, AbstractHttpClient client) {
		Feed feed = null;
		
		String url = createFeedUrl(stationId);
		HttpGet httpGet = new HttpGet(url);
		HttpResponse httpRes;
		try {
			// Download station XML data.
			httpRes = client.execute(httpGet);
			
			// Status codeが400以上ならばエラー。
			// 参考 ： http://www.studyinghttp.net/status_code
			int statusCode = httpRes.getStatusLine().getStatusCode();
			if(400 <= statusCode) {
				Log.e(SugorokuonConst.LOGTAG, "Failed to download feed : status code " + statusCode);
			} else {
				// サーバからのresponseをparse。
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
