package com.app.weatherforecast.core.utils

import android.os.Build
import javax.inject.Inject
import javax.inject.Singleton

interface SdkProvider {
    val sdkInt: Int
}

@Singleton
internal class SdkProviderImpl @Inject constructor(): SdkProvider {
    override val sdkInt: Int
        get() = Build.VERSION.SDK_INT
}