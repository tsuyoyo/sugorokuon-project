package tsuyogoro.sugorokuon.di

import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.SugorokuonApplication
import tsuyogoro.sugorokuon.radiko.SearchUuidGenerator
import tsuyogoro.sugorokuon.model.SugorokuonAppState
import tsuyogoro.sugorokuon.rx.SchedulerProvider
import tsuyogoro.sugorokuon.rx.SchedulerProviderForApp
import javax.inject.Singleton

@Module
class SugorokuonAppModule(
        private val appContext: Context
) {

    @Singleton
    @Provides
    fun provideApplicationContext(): Context = appContext

    @Singleton
    @Provides
    fun provideAppComponent(appContext: Context): SugorokuonAppComponent =
            SugorokuonApplication
                    .application(appContext)
                    .appComponent()

    @Singleton
    @Provides
    fun provideResources(): Resources = appContext.resources

    @Singleton
    @Provides
    fun provideSchedulerProvider(): SchedulerProvider = SchedulerProviderForApp()

    @Singleton
    @Provides
    fun provideSugorokuonAppState(): SugorokuonAppState = SugorokuonAppState()

    @Singleton
    @Provides
    fun provideSearchUuidGenerator(): SearchUuidGenerator = SearchUuidGenerator()
}