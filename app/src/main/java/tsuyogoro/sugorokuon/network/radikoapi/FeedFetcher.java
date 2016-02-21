/**
 * Copyright (c) 
 * 2013 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network.radikoapi;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tsuyogoro.sugorokuon.constants.SugorokuonConst;
import tsuyogoro.sugorokuon.models.entities.Feed;
import tsuyogoro.sugorokuon.utils.SugorokuonLog;

/**
 * 曲のFeedをdownloadして、Feedインスタンスを作るクラス。
 *
 * @author Tsuyoyo
 *
 */
public class FeedFetcher {

    /**
     * Feedをdownloadする。
     * 現在のところ、曲のonAir情報を取得するためのみに使う（他の情報も取れるが、全て読み飛ばす）。
     *
     * @param stationId
     * @return 取得に失敗した場合、nullが返る。原因はlogcatを見ること。
     */
	public static Feed fetch(String stationId) {
		Feed feed = null;
		
		String url = createFeedUrl(stationId);

		OkHttpClient client = new OkHttpClient();

		Request request = new Request.Builder().url(url).build();
		try {
			// Download station XML data.
			Response httpRes = client.newCall(request).execute();

            // Status codeが400以上ならばエラー。
            // 参考 ： http://www.studyinghttp.net/status_code
			int statusCode = httpRes.code();

			if(400 <= statusCode) {
				Log.e(SugorokuonConst.LOGTAG, "Failed to download feed : status code " + statusCode);
			} else {
				FeedResponseParser feedResponseParser = new FeedResponseParser(
						httpRes.body().byteStream(), "UTF-8");
				feed = feedResponseParser.parse();
			}

		} catch(IOException e) {
            SugorokuonLog.e("IOException at fetching feed : " + e.getMessage());
		}
		
		return feed;
	}
	
	private static String createFeedUrl(String stationId) {
        Uri.Builder builder = new Uri.Builder();

        // "http://radiko.jp/v2/station/feed_PC/";
        builder.scheme("http").authority("radiko.jp")
                .appendPath("v2").appendPath("station").appendPath("feed_PC")
                .appendPath(stationId + ".xml");

        return builder.build().toString();
	}
	
}
