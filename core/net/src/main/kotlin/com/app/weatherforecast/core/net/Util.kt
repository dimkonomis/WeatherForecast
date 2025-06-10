package com.app.weatherforecast.core.net

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

fun getJSON(module: SerializersModule? = null): Json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true // properties that we don't want to parse
    allowStructuredMapKeys = true // class as keys in maps
    coerceInputValues = true // returns default value in case unknown enum type or null json key
    isLenient = true
    module?.let { serializersModule = it }
}

/**
 * Common [Json] configuration
 */
val JSON: Json = getJSON(SerializersModule {
    // Add any contextual serializers here
})