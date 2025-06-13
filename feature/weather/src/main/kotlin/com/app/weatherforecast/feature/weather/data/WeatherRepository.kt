package com.app.weatherforecast.feature.weather.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class WeatherRepository @Inject constructor(
    private val weatherApi: WeatherApi
)  {

    suspend fun fetch(
        lat: Double,
        lon: Double,
        unit: Unit
    ): WeatherResponse {
        return weatherApi.weather(
            lat = lat,
            lon = lon,
            unit = unit.value
        )
    }
}