package tsuyogoro.sugorokuon.v3.api

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.transform.RegistryMatcher
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.*
import javax.inject.Named
import javax.inject.Singleton

@Module
open class RadikoApiModule {

    @Singleton
    @Provides
    fun provideApiConfig(): ApiConfig = ApiConfig()

    @Singleton
    @Named("calendar")
    @Provides
    fun provideDateConverter(): Serializer = RegistryMatcher()
            .apply { bind(Calendar::class.java, ApiDateConverter()) }
            .let { Persister(it) }

    @Provides
    fun provideStationApi(apiConfig: ApiConfig): StationApi =
            Retrofit.Builder()
                    .baseUrl(apiConfig.API_ROOT)
                    .client(OkHttpClient())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .build()
                    .create(StationApi::class.java)

    @Provides
    fun provideSearchApi(apiConfig: ApiConfig): SearchApi =
            Retrofit.Builder()
                    .baseUrl(apiConfig.API_ROOT)
                    .client(OkHttpClient())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(
                            GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()
                    ))
                    .build()
                    .create(SearchApi::class.java)

    @Provides
    fun provideTimeTableApi(apiConfig: ApiConfig,
                            @Named("calendar") serializer: Serializer): TimeTableApi =
            Retrofit.Builder()
                    .baseUrl(apiConfig.API_ROOT)
                    .client(OkHttpClient())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(SimpleXmlConverterFactory.create(serializer))
                    .build()
                    .create(TimeTableApi::class.java)

    @Provides
    fun provideFeedApi(apiConfig: ApiConfig,
                       @Named("calendar") serializer: Serializer): FeedApi =
            Retrofit.Builder()
                    .baseUrl(apiConfig.API_ROOT)
                    .client(OkHttpClient())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(SimpleXmlConverterFactory.create(serializer))
                    .build()
                    .create(FeedApi::class.java)

}