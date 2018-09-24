/**
 * Copyright (c)
 * 2018 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.radiko.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.transform.RegistryMatcher
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.*

object RadikoApiProvider {

    fun provideStationApi(): StationApi = retrofitBuilderForXmlResponseApi()
            .build()
            .create(StationApi::class.java)

    fun provideSearchApi(): SearchApi = retrofitBuilderWithCommonSettings()
            .addConverterFactory(GsonConverterFactory.create(
                GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()
            ))
            .build()
            .create(SearchApi::class.java)

    fun provideTimeTableApi(): TimeTableApi = retrofitBuilderForXmlResponseApi()
            .build()
            .create(TimeTableApi::class.java)

    fun provideFeedApi(): FeedApi = retrofitBuilderForXmlResponseApi()
            .build()
            .create(FeedApi::class.java)

    private val apiConfig = ApiConfig()

    private val dateConverter: Serializer = Persister(
        RegistryMatcher().apply { bind(Calendar::class.java, ApiDateConverter()) }
    )

    private fun retrofitBuilderWithCommonSettings() = Retrofit.Builder()
        .baseUrl(apiConfig.API_ROOT)
        .client(httpClientWithCommonSettings())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

    private fun retrofitBuilderForXmlResponseApi() = retrofitBuilderWithCommonSettings()
        .addConverterFactory(SimpleXmlConverterFactory.create(dateConverter))

    private fun httpClientWithCommonSettings() = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
        )
        .build()
}