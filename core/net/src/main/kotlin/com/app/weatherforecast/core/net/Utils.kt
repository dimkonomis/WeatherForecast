package com.app.weatherforecast.core.net

import com.app.weatherforecast.core.net.serializer.DateSerializer
import com.app.weatherforecast.core.utils.getJSON
import kotlinx.serialization.modules.SerializersModule
import retrofit2.Converter
import retrofit2.converter.kotlinx.serialization.asConverterFactory

val factory: Converter.Factory = getJSON(SerializersModule {
    contextual(DateSerializer.targetClass, DateSerializer)
}).asConverterFactory(MEDIA_TYPE_JSON)