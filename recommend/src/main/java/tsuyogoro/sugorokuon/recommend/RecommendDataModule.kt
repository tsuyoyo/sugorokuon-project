package tsuyogoro.sugorokuon.recommend

import android.content.Context
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import tsuyogoro.sugorokuon.recommend.settings.RecommendSettingsRepository
import javax.inject.Singleton

@Module
class RecommendDataModule {

    @Singleton
    @Provides
    fun provideRecommendSettingsRepository(context: Context): RecommendSettingsRepository =
        RecommendSettingsRepository(context, PreferenceManager.getDefaultSharedPreferences(context))

}