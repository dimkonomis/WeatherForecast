package com.app.weatherforecast.core.net.interceptor

import com.app.weatherforecast.core.net.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

internal class AppIdParamInterceptor @Inject constructor() : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val url = original.url
            .newBuilder()
            .addQueryParameter(APP_ID, BuildConfig.API_KEY)
            .build()


        val request = original.newBuilder()
            .url(url)
            .build()

        return chain.proceed(request)
    }

    companion object {
        private const val APP_ID = "appid"
    }
}