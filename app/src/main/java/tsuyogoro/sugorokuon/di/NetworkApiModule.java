/**
 * Copyright (c)
 * 2016 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.di;

import dagger.Module;
import dagger.Provides;
import tsuyogoro.sugorokuon.network.IRadikoFeedFetcher;
import tsuyogoro.sugorokuon.network.IStationFetcher;
import tsuyogoro.sugorokuon.network.ITimeTableFetcher;
import tsuyogoro.sugorokuon.network.nhk.NhkStationsFetcher;
import tsuyogoro.sugorokuon.network.nhk.NhkTimeTableFetcher;
import tsuyogoro.sugorokuon.network.radikoapi.RadikoFeedFetcher;
import tsuyogoro.sugorokuon.network.radikoapi.RadikoStationsFetcher;
import tsuyogoro.sugorokuon.network.radikoapi.RadikoTimeTableFetcher;

@Module
public class NetworkApiModule {

    @Provides
    public IStationFetcher provideStationFetcher() {
        return new RadikoStationsFetcher();
    }

    @Provides
    public ITimeTableFetcher provideTimeTableFetcher() {
        return new RadikoTimeTableFetcher();
    }

    @Provides
    public IRadikoFeedFetcher provideFeedFetcher() {
        return new RadikoFeedFetcher();
    }

    @Provides
    public NhkStationsFetcher provideNhkStationsFetcher() {
        return new NhkStationsFetcher();
    }

    @Provides
    public NhkTimeTableFetcher provideNhkTimeTableFetcher() {
        return new NhkTimeTableFetcher();
    }

}
