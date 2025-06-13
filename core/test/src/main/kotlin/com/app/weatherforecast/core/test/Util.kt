package com.app.weatherforecast.core.test

import com.app.weatherforecast.core.net.MEDIA_TYPE_JSON
import com.app.weatherforecast.core.net.factory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Retrofit

internal typealias Callback = (Interceptor.Chain) -> Response

class TestInterceptor(
    private val callback: Callback = { it.proceed(it.request()) },
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return callback(chain)
    }
}

inline fun<reified API> createTestApi(
    noinline intercept: Callback
) : API {
    return Retrofit.Builder()
        .baseUrl("http://localhost")
        .client(
            OkHttpClient.Builder()
                .addInterceptor(TestInterceptor(intercept))
                .build()
        )
        .addConverterFactory(factory)
        .build()
        .create(API::class.java)
}

fun Request.createJsonResponse(code: Int, responseBody: String?): Response = Response.Builder()
    .request(this)
    .protocol(Protocol.HTTP_2)
    .message("")
    .code(code)
    .body(responseBody?.toResponseBody(MEDIA_TYPE_JSON))
    .build()