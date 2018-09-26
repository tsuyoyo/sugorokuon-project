package tsuyogoro.sugorokuon.data

import android.content.Context
import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.appstate.AppPrefRepository
import tsuyogoro.sugorokuon.appstate.AppPrefs
import tsuyogoro.sugorokuon.preference.AreaPrefs
import tsuyogoro.sugorokuon.preference.SearchMethodPrefs
import tsuyogoro.sugorokuon.preference.StationPrefs
import javax.inject.Singleton

@Module
class DataModule {

    @Singleton
    @Provides
    fun provideSettingsRepository(appContext: Context): SettingsRepository =
        SettingsRepository(
            areaPrefs = AreaPrefs.get(appContext),
            stationsPrefs = StationPrefs.get(appContext),
            searchMethodPrefs = SearchMethodPrefs.get(appContext)
        )

    @Singleton
    @Provides
    fun provideAppPrefsRepository(appContext: Context): AppPrefRepository =
        AppPrefRepository(appPrefs = AppPrefs.get(appContext))

}