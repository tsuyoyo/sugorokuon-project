package tsuyogoro.sugorokuon.v3.di

import android.content.Context
import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.v3.rx.SchedulerProvider
import tsuyogoro.sugorokuon.v3.rx.SchedulerProviderForApp
import javax.inject.Singleton

@Module
class SugorokuonAppModule(
        private val appContext: Context
) {

    @Singleton
    @Provides
    fun provideApplicationContext() : Context = appContext


    @Singleton
    @Provides
    fun provideResources() = appContext.resources

    @Singleton
    @Provides
    fun provideSchedulerProvider(): SchedulerProvider = SchedulerProviderForApp()
}