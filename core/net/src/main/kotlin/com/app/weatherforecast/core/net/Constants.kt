package com.app.weatherforecast.core.net

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType

internal const val NETWORK_TIMEOUT = 30L // seconds
val MEDIA_TYPE_JSON: MediaType = "application/json".toMediaType()