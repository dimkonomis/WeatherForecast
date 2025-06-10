package com.app.weatherforecast.core.net

import android.content.Context
import com.app.weatherforecast.core.net.flipper.FlipperHelper
import com.app.weatherforecast.core.net.flipper.FlipperHelperImpl
import com.app.weatherforecast.core.net.qualifier.LoggingInterceptor
import com.moczul.ok2curl.logger.Loggable
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import okhttp3.logging.HttpLoggingInterceptor.Logger
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DebugModule {

    @Provides
    @Singleton
    fun provideFlipperHelper(@ApplicationContext context: Context): FlipperHelper =
        FlipperHelperImpl(context)

    @Singleton
    @Provides
    @LoggingInterceptor
    internal fun provideLoggingInterceptor(): Interceptor = object : Interceptor {
        private val fullHttpLoggingInterceptor =
            HttpLoggingInterceptor(object : Logger, Loggable {
                override fun log(message: String) {
                    Timber.d(message)
                }
            }).apply { this.level = BODY }

        override fun intercept(chain: Interceptor.Chain): Response = chain.request()
            .let { fullHttpLoggingInterceptor.intercept(chain) }
    }

}