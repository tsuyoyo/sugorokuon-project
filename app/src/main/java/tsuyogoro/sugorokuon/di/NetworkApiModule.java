/**
 * Copyright (c)
 * 2016 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.di;

import dagger.Module;
import dagger.Provides;
import tsuyogoro.sugorokuon.network.FeedFetcher;
import tsuyogoro.sugorokuon.network.StationFetcher;
import tsuyogoro.sugorokuon.network.TimeTableFetcher;
import tsuyogoro.sugorokuon.network.radikoapi.RadikoFeedFetcher;
import tsuyogoro.sugorokuon.network.radikoapi.RadikoStationsFetcher;
import tsuyogoro.sugorokuon.network.radikoapi.RadikoTimeTableFetcher;

@Module
public class NetworkApiModule {

    @Provides
    public StationFetcher provideStationFetcher() {
        return new RadikoStationsFetcher();
    }

    @Provides
    public TimeTableFetcher provideTimeTableFetcher() {
        return new RadikoTimeTableFetcher();
    }

    @Provides
    public FeedFetcher provideFeedFetcher() {
        return new RadikoFeedFetcher();
    }

}
