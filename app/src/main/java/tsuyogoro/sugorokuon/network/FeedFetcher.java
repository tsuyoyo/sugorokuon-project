package tsuyogoro.sugorokuon.network;

import tsuyogoro.sugorokuon.models.entities.Feed;

public interface FeedFetcher {

    /**
     * Feedをdownloadする。
     *
     * @param stationId
     * @return 取得に失敗した場合、nullが返る。原因はlogcatを見ること。
     */
    Feed fetch(String stationId);
}
