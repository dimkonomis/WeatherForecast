package com.app.weatherforecast.core.utils

import java.time.LocalDateTime

interface DateProvider {
    val now: LocalDateTime
}

internal object DateProviderImpl : DateProvider {
    override val now: LocalDateTime
        get() = LocalDateTime.now()
}