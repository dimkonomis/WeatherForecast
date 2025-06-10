package com.app.weatherforecast.core.net.flipper

import okhttp3.OkHttpClient

internal interface FlipperHelper {

    fun init() = Unit

    fun addInterceptor(builder: OkHttpClient.Builder) = Unit

}