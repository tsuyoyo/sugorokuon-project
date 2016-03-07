/**
 * Copyright (c) 
 * 2013 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.network.radikoapi;

import java.util.ArrayList;
import java.util.List;

import tsuyogoro.sugorokuon.models.entities.Feed;
import tsuyogoro.sugorokuon.models.entities.OnAirSong;
import tsuyogoro.sugorokuon.network.FeedFetcher;
import tsuyogoro.sugorokuon.network.OkHttpWrapper;
import tsuyogoro.sugorokuon.utils.SugorokuonLog;

/**
 * 曲のFeedをdownloadして、Feedインスタンスを作るクラス。
 *
 */
public class RadikoFeedFetcher implements FeedFetcher {

	@Override
	public Feed fetch(String stationId) {

		FeedApiClient api = new FeedApiClient(OkHttpWrapper.buildClient());
		FeedApiClient.NowOnAir nowOnAir = api.fetchNowOnAirSongs(stationId);

		if (nowOnAir == null) {
			SugorokuonLog.d("No feed : stationId = " + stationId);
			return null;
		}

		List<OnAirSong> onAirSongs = new ArrayList<>();

		for (FeedApiClient.NowOnAir.Item s : nowOnAir.onAirSongs) {
			OnAirSong song = new OnAirSong(stationId,
					(s.artist != null) ? AlphabetNormalizer.zenkakuToHankaku(s.artist) : "",
					(s.title != null) ? AlphabetNormalizer.zenkakuToHankaku(s.title) : "",
					s.stamp,
					(s.img != null) ? s.img : s.img_large);

			song.amazon = s.amazon;
			song.recochoku = s.recochoku;
			onAirSongs.add(song);
		}

		return new Feed(onAirSongs);
	}
	
}
