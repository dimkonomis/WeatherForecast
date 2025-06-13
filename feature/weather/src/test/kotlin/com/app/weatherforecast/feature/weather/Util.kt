package com.app.weatherforecast.feature.weather

import com.app.weatherforecast.feature.weather.data.Current
import com.app.weatherforecast.feature.weather.data.Daily
import com.app.weatherforecast.feature.weather.data.Hourly
import com.app.weatherforecast.feature.weather.data.Temperature
import com.app.weatherforecast.feature.weather.data.Weather
import com.app.weatherforecast.feature.weather.data.WeatherResponse

import java.time.LocalDateTime

internal val sampleWeatherResponse = WeatherResponse(
    current = Current(
        dateTime = LocalDateTime.of(2025, 6, 10, 13, 0),
        temperature = 22.5,
        feelsLike = 21.0,
        visibility = 10000.0,
        pressure = 1013,
        humidity = 60.0,
        windSpeed = 5.5,
        weatherList = listOf(
            Weather(
                id = 800,
                main = "Clear",
                description = "clear sky",
                icon = "01d"
            )
        )
    ),
    hourly = listOf(
        Hourly(
            dateTime = LocalDateTime.of(2025, 6, 10, 14, 0),
            temperature = 23.0,
            feelsLike = 22.0,
            weatherList = listOf(
                Weather(
                    id = 801,
                    main = "Clouds",
                    description = "few clouds",
                    icon = "02d"
                )
            )
        ),
        Hourly(
            dateTime = LocalDateTime.of(2025, 6, 10, 15, 0),
            temperature = 24.0,
            feelsLike = 23.0,
            weatherList = listOf(
                Weather(
                    id = 500,
                    main = "Rain",
                    description = "light rain",
                    icon = "10d"
                )
            )
        ),
        Hourly(
            dateTime = LocalDateTime.of(2025, 6, 11, 3, 0),
            temperature = 24.0,
            feelsLike = 23.0,
            weatherList = listOf(
                Weather(
                    id = 500,
                    main = "Rain",
                    description = "light rain",
                    icon = "10d"
                )
            )
        )
    ),
    daily = listOf(
        Daily(
            dateTime = LocalDateTime.of(2025, 6, 11, 12, 0),
            temperature = Temperature(min = 18.0, max = 27.0),
            summary = "Partly cloudy with light rain",
            weatherList = listOf(
                Weather(
                    id = 803,
                    main = "Clouds",
                    description = "broken clouds",
                    icon = "04d"
                )
            )
        ),
        Daily(
            dateTime = LocalDateTime.of(2025, 6, 12, 12, 0),
            temperature = Temperature(min = 17.0, max = 25.0),
            summary = "Sunny and dry",
            weatherList = listOf(
                Weather(
                    id = 800,
                    main = "Clear",
                    description = "clear sky",
                    icon = "01d"
                )
            )
        )
    )
)
