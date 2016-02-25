/**
 * Copyright (c) 
 * 2013 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network.radikoapi;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import tsuyogoro.sugorokuon.models.entities.Feed;
import tsuyogoro.sugorokuon.models.entities.OnAirSong;
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
     *
     * @param stationId
     * @return 取得に失敗した場合、nullが返る。原因はlogcatを見ること。
     */
	public static Feed fetch(String stationId) {

		FeedApiClient api = new FeedApiClient(new OkHttpClient());
		FeedApiClient.NowOnAir nowOnAir = api.fetchNowOnAirSongs(stationId);

		if (nowOnAir == null) {
			SugorokuonLog.e("Failed to fetch Feed : stationId = " + stationId);
			return null;
		}

		List<OnAirSong> onAirSongs = new ArrayList<>();

		for (FeedApiClient.NowOnAir.Item s : nowOnAir.onAirSongs) {
			OnAirSong song = new OnAirSong(stationId,
					(s.artist != null) ? AlphabetNormalizer.zenkakuToHankaku(s.artist) : "",
					(s.title != null) ? AlphabetNormalizer.zenkakuToHankaku(s.title) : "",
					s.stamp, s.img);
			song.amazon = s.amazon;
			song.recochoku = s.recochoku;
			onAirSongs.add(song);
		}

		return new Feed(onAirSongs);
	}
	
}
