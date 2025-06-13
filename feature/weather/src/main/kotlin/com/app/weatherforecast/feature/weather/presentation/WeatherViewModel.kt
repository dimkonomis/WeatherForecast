package com.app.weatherforecast.feature.weather.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.weatherforecast.core.model.AsyncResult
import com.app.weatherforecast.core.utils.DateProvider
import com.app.weatherforecast.core.utils.DispatcherProvider
import com.app.weatherforecast.feature.weather.domain.Weather
import com.app.weatherforecast.feature.weather.domain.WeatherUseCase
import com.app.weatherforecast.feature.weather.presentation.WeatherUiState.Error
import com.app.weatherforecast.feature.weather.presentation.WeatherUiState.Loading
import com.app.weatherforecast.feature.weather.presentation.WeatherUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
internal class WeatherViewModel @Inject constructor(
    private val weatherUseCase: WeatherUseCase,
    private val dateProvider: DateProvider,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val todayFormatter = DateTimeFormatter.ofPattern("h a", Locale.getDefault())
    private val forecastFormatter = DateTimeFormatter.ofPattern("EEE", Locale.getDefault())

    init {
        viewModelScope.launch(dispatcherProvider.io) {
            weatherUseCase.get()
        }
    }

    val uiState: StateFlow<WeatherUiState> = weatherUseCase.state
        .distinctUntilChanged()
        .map { state ->
            when (val weather = state.weather) {
                is AsyncResult.NotStarted, AsyncResult.Loading -> Loading
                is AsyncResult.Failure -> Error
                is AsyncResult.Success -> weather.value.toUiState()
            }
        }
        .stateIn(viewModelScope, WhileSubscribed(stopTimeoutMillis = 300L), Loading)

    fun retry() {
        viewModelScope.launch(dispatcherProvider.ui) {
            weatherUseCase.get()
        }
    }

    private fun Weather.toUiState(): Success {
        val now = dateProvider.now

        return Success(
            location = location,
            current = Success.Current(
                summary = response.current.weather.main,
                temperature = response.current.temperature,
                feelsLike = response.current.feelsLike,
                visibility = response.current.visibility / 1000, // Convert to km
                pressure = response.current.pressure,
                humidity = response.current.humidity,
                windSpeed = response.current.windSpeed,
                icon = String.format(ICON_URL_FORMAT, response.current.weather.icon)
            ),
            today = response.hourly
                .filter { it.dateTime.isToday(now) }
                .map {
                    Success.Today(
                        dateTime = it.dateTime.format(todayFormatter),
                        temperature = it.temperature,
                        summary = it.weather.main,
                        icon = String.format(ICON_URL_FORMAT, it.weather.icon)
                    )
                },
            forecast = response.daily.map {
                Success.Forecast(
                    dateTime = it.dateTime.format(forecastFormatter),
                    min = it.temperature.min,
                    max = it.temperature.max,
                    summary = it.weather.main,
                    icon = String.format(ICON_URL_FORMAT, it.weather.icon)
                )
            }
        )
    }

    private fun LocalDateTime.isToday(now: LocalDateTime): Boolean {
        return dayOfMonth == now.dayOfMonth
    }

    companion object {
        private const val ICON_URL_FORMAT = "https://openweathermap.org/img/wn/%s@2x.png"
    }

}