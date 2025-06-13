package com.app.weatherforecast.feature.weather.domain

import com.app.weatherforecast.contract.location.Location
import com.app.weatherforecast.contract.location.LocationRepository
import com.app.weatherforecast.core.model.AsyncResult
import com.app.weatherforecast.core.utils.DispatcherProvider
import com.app.weatherforecast.feature.weather.data.Unit
import com.app.weatherforecast.feature.weather.data.WeatherRepository
import com.app.weatherforecast.feature.weather.data.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

internal data class WeatherState(
    val weather: AsyncResult<Weather> = AsyncResult.NotStarted,
)

internal data class Weather(
    val location: String,
    val response: WeatherResponse
)

internal class WeatherUseCase @Inject constructor(
    private val locationRepository: LocationRepository,
    private val weatherRepository: WeatherRepository,
    private val dispatcherProvider: DispatcherProvider
) {

    val state: Flow<WeatherState>
        field = MutableStateFlow(WeatherState())

    suspend fun get() {
        withContext(dispatcherProvider.io) {
            locationRepository.selected
                .map { location ->
                    when (location) {
                        is Location.Available -> location
                        Location.NotAvailable -> throw IllegalStateException("Location not available")
                    }
                }
                .onStart {
                    state.update { currentState ->
                        currentState.copy(weather = AsyncResult.Loading)
                    }
                }
                .map { location ->
                    val response = weatherRepository.fetch(
                        lat = location.lat,
                        lon = location.long,
                        unit = Unit.METRIC
                    )
                    Weather(
                        location = location.name,
                        response = response
                    )
                }
                .catch {
                    Timber.e(it, "Error fetching weather data")
                    state.update { currentState ->
                        currentState.copy(weather = AsyncResult.Failure(it))
                    }
                }
                .onEach {
                    state.update { currentState ->
                        currentState.copy(weather = AsyncResult.Success(it))
                    }
                }
                .launchIn(this)
        }
    }

}