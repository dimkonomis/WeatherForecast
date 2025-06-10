package com.app.weatherforecast.core.net

import com.app.weatherforecast.core.net.flipper.FlipperHelper
import com.app.weatherforecast.core.net.interceptor.AppIdParamInterceptor
import com.app.weatherforecast.core.net.qualifier.AppIdInterceptor
import com.app.weatherforecast.core.net.qualifier.BaseUrl
import com.app.weatherforecast.core.net.qualifier.LoggingInterceptor
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.net.URL
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface NetModule {

    @AppIdInterceptor
    @Binds
    fun bindAppIdInterceptor(
        interceptor: AppIdParamInterceptor
    ): Interceptor

    companion object {

        @Provides
        @BaseUrl
        fun provideBaseUrl(): URL = URL("https://api.openweathermap.org/data/")

        @Provides
        @Singleton
        fun provideOkHttpClient(
            @LoggingInterceptor loggingInterceptor: Interceptor,
            @AppIdInterceptor appIdInterceptor: Interceptor,
            flipperHelper: FlipperHelper,
        ): OkHttpClient =
            OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
                .apply {
                    addInterceptor(loggingInterceptor)
                    addInterceptor(appIdInterceptor)
                    flipperHelper.addInterceptor(this)
                }
                .build()

        @Provides
        @Singleton
        fun provideRetrofit(
            @BaseUrl baseUrl: URL,
            okHttpClient: OkHttpClient,
        ): Retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(JSON.asConverterFactory(MEDIA_TYPE_JSON))
            .build()

    }

}