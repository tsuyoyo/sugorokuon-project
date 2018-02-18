package tsuyogoro.sugorokuon.v3.di

import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.v3.api.SearchUuidGenerator
import tsuyogoro.sugorokuon.v3.model.SugorokuonAppState
import tsuyogoro.sugorokuon.v3.rx.SchedulerProvider
import tsuyogoro.sugorokuon.v3.rx.SchedulerProviderForApp
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