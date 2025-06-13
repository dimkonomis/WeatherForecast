package com.app.weatherforecast.feature.weather.data

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
internal data class WeatherResponse(
    @SerialName("current") val current: Current,
    @SerialName("hourly") val hourly: List<Hourly>,
    @SerialName("daily") val daily: List<Daily>,
)

@Serializable
internal data class Current(
    @SerialName("dt") @Contextual val dateTime: LocalDateTime,
    @SerialName("temp") val temperature: Double,
    @SerialName("feels_like") val feelsLike: Double,
    @SerialName("visibility") val visibility: Double,
    @SerialName("pressure") val pressure: Int,
    @SerialName("humidity") val humidity: Double,
    @SerialName("wind_speed") val windSpeed: Double,
    @SerialName("weather") private val weatherList: List<Weather>,
) {
    val weather: Weather
        get() = weatherList.first()
}

@Serializable
internal data class Hourly(
    @SerialName("dt") @Contextual val dateTime: LocalDateTime,
    @SerialName("temp") val temperature: Double,
    @SerialName("feels_like") val feelsLike: Double,
    @SerialName("weather") private val weatherList: List<Weather>,
) {
    val weather: Weather
        get() = weatherList.first()
}

@Serializable
internal data class Daily(
    @SerialName("dt") @Contextual val dateTime: LocalDateTime,
    @SerialName("temp") val temperature: Temperature,
    @SerialName("summary") val summary: String,
    @SerialName("weather") private val weatherList: List<Weather>,
) {
    val weather: Weather
        get() = weatherList.first()
}

@Serializable
internal data class Temperature(
    @SerialName("min") val min: Double,
    @SerialName("max") val max: Double,
)

@Serializable
internal data class Weather(
    @SerialName("id") val id: Int,
    @SerialName("main") val main: String,
    @SerialName("description") val description: String,
    @SerialName("icon") val icon: String,
)

internal enum class Unit(val value: String) {
    METRIC("metric"),
    IMPERIAL("imperial"),
    STANDARD("standard"),
}