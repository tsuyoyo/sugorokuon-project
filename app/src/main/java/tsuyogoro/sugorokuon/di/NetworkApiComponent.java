/**
 * Copyright (c)
 * 2016 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.di;

import javax.inject.Singleton;

import dagger.Component;
import tsuyogoro.sugorokuon.services.OnAirSongsService;
import tsuyogoro.sugorokuon.services.TimeTableService;

@Singleton
@Component(modules = {NetworkApiModule.class})
public interface NetworkApiComponent {

    void inject(OnAirSongsService onAirSongsService);

    void inject(TimeTableService timeTableService);

}
