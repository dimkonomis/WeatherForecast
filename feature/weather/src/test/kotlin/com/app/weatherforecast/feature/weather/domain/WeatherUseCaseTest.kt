package com.app.weatherforecast.feature.weather.domain

import com.app.weatherforecast.contract.location.Location
import com.app.weatherforecast.contract.location.LocationRepository
import com.app.weatherforecast.core.model.AsyncResult
import com.app.weatherforecast.core.utils.DispatcherProvider
import com.app.weatherforecast.feature.weather.data.Unit
import com.app.weatherforecast.feature.weather.data.WeatherRepository
import com.app.weatherforecast.feature.weather.data.WeatherResponse
import com.app.weatherforecast.feature.weather.sampleWeatherResponse
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherUseCaseTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val locationRepository: LocationRepository = mockk()
    private val weatherRepository: WeatherRepository = mockk()
    private val dispatcherProvider = object : DispatcherProvider {
        override val io = dispatcher
        override val ui = dispatcher
        override val default = dispatcher
    }
    private lateinit var useCase: WeatherUseCase

    @Before
    fun setup() {
        useCase = WeatherUseCase(locationRepository, weatherRepository, dispatcherProvider)
    }

    @Test
    fun `get with success`() = runTest(dispatcher) {
        // Given
        val lat = 1.2
        val lon = 3.4
        val unit = Unit.METRIC
        val location = Location.Available(lat = lat, long = lon, name = "Test Location")
        val response = sampleWeatherResponse
        val states = mutableListOf<WeatherState>()
        val job = launch { useCase.state.toList(states) }

        every { locationRepository.selected } returns flowOf(location)
        coEvery { weatherRepository.fetch(lat, lon, unit) } coAnswers {
            delay(50)
            response
        }

        // When
        useCase.get()

        // Then
        assert(AsyncResult.Loading == states[1].weather)
        assert(AsyncResult.Success(Weather(location.name, response)) == states[2].weather)

        job.cancel()
    }

    @Test
    fun `get with location not available`() = runTest(dispatcher) {
        // Given
        val location = Location.NotAvailable
        val states = mutableListOf<WeatherState>()
        val job = launch { useCase.state.toList(states) }

        every { locationRepository.selected } returns flow {
            delay(50)
            emit(location)
        }

        // When
        useCase.get()

        // Then
        assert(AsyncResult.Loading == states[1].weather)
        assert(AsyncResult.Failure(IllegalStateException("Location not available")) == states[2].weather)

        job.cancel()
    }

    @Test
    fun `get with weather error`() = runTest(dispatcher) {
        // Given
        val lat = 1.2
        val lon = 3.4
        val unit = Unit.METRIC
        val location = Location.Available(lat = lat, long = lon, name = "Test Location")
        val error = RuntimeException("Weather error")
        val states = mutableListOf<WeatherState>()
        val job = launch { useCase.state.toList(states) }

        every { locationRepository.selected } returns flowOf(location)
        coEvery { weatherRepository.fetch(lat, lon, unit) } coAnswers {
            delay(50)
            throw error
        }

        // When
        useCase.get()

        // Then
        assert(AsyncResult.Loading == states[1].weather)
        assert(AsyncResult.Failure(RuntimeException("Weather error")) == states[2].weather)

        job.cancel()
    }

}