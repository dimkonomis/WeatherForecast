package com.app.weatherforecast.feature.weather.presentation

sealed interface WeatherUiState {
    data object Loading : WeatherUiState
    data object Error : WeatherUiState
    data class Success(
        val location: String,
        val current: Current,
        val today: List<Today>,
        val forecast: List<Forecast>
    ) : WeatherUiState {

        data class Current(
            val summary: String,
            val temperature: Double,
            val feelsLike: Double,
            val visibility: Double,
            val pressure: Int,
            val humidity: Double,
            val windSpeed: Double,
            val icon: String
        )

        data class Today(
            val dateTime: String,
            val temperature: Double,
            val summary: String,
            val icon: String
        )

        data class Forecast(
            val dateTime: String,
            val min: Double,
            val max: Double,
            val summary: String,
            val icon: String
        )

    }
}

internal sealed interface WeatherUiAction {

    data object Retry : WeatherUiAction

    sealed interface Navigation : WeatherUiAction {
        data object SelectLocation : Navigation
    }

}