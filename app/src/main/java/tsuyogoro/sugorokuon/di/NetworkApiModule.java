/**
 * Copyright (c)
 * 2016 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.di;

import dagger.Module;
import dagger.Provides;
import tsuyogoro.sugorokuon.network.IRadikoFeedFetcher;
import tsuyogoro.sugorokuon.network.IRadikoStationFetcher;
import tsuyogoro.sugorokuon.network.IRadikoTimeTableFetcher;
import tsuyogoro.sugorokuon.network.radikoapi.RadikoFeedFetcher;
import tsuyogoro.sugorokuon.network.radikoapi.RadikoStationsFetcher;
import tsuyogoro.sugorokuon.network.radikoapi.RadikoTimeTableFetcher;

@Module
public class NetworkApiModule {

    @Provides
    public IRadikoStationFetcher provideStationFetcher() {
        return new RadikoStationsFetcher();
    }

    @Provides
    public IRadikoTimeTableFetcher provideTimeTableFetcher() {
        return new RadikoTimeTableFetcher();
    }

    @Provides
    public IRadikoFeedFetcher provideFeedFetcher() {
        return new RadikoFeedFetcher();
    }

}
