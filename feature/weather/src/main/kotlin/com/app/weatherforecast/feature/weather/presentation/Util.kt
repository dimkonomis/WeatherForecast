package com.app.weatherforecast.feature.weather.presentation

internal val dummyWeatherUiState = WeatherUiState.Success(
    location = "New York",
    current = WeatherUiState.Success.Current(
        summary = "Sunny",
        temperature = 22.5,
        feelsLike = 25.2,
        visibility = 10.5,
        pressure = 1012,
        humidity = 62.4,
        windSpeed = 5.4,
        icon = "01d"
    ),
    today = listOf(
        WeatherUiState.Success.Today(
            dateTime = "12 PM",
            temperature = 22.5,
            summary = "Sunny",
            icon = "01d"
        ),
        WeatherUiState.Success.Today(
            dateTime = "2 PM",
            temperature = 24.2,
            summary = "Rainy",
            icon = "01d"
        )
    ),
    forecast = listOf(
        WeatherUiState.Success.Forecast(
            dateTime = "Wed",
            min = 18.4,
            max = 24.5,
            summary = "Partly Cloudy",
            icon = "02d"
        ),
        WeatherUiState.Success.Forecast(
            dateTime = "Thu",
            min = 19.6,
            max = 26.6,
            summary = "Sunny",
            icon = "02d"
        )
    )
)