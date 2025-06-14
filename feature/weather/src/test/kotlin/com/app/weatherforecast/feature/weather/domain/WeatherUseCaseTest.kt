package com.app.weatherforecast.feature.weather.domain

import com.app.weatherforecast.contract.location.Location
import com.app.weatherforecast.contract.location.LocationRepository
import com.app.weatherforecast.core.model.AsyncResult
import com.app.weatherforecast.core.test.TestFlow.Companion.testFlow
import com.app.weatherforecast.core.utils.DispatcherProvider
import com.app.weatherforecast.feature.weather.data.Unit
import com.app.weatherforecast.feature.weather.data.WeatherRepository
import com.app.weatherforecast.feature.weather.sampleWeatherResponse
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
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
        every { locationRepository.selected } returns flowOf(location)
        coEvery { weatherRepository.fetch(lat, lon, unit) } returns sampleWeatherResponse

        useCase
            .state
            .map { it.weather }
            .testFlow(
                CoroutineScope(UnconfinedTestDispatcher(testScheduler))
            ) {
                `when` {
                    useCase.get()
                }.then(
                    AsyncResult.NotStarted,
                    AsyncResult.Loading,
                    AsyncResult.Success(Weather(location.name, sampleWeatherResponse))
                )
            }
    }

    @Test
    fun `get with location not available`() = runTest(dispatcher) {
        // Given
        val location = Location.NotAvailable
        every { locationRepository.selected } returns flow { emit(location) }

        useCase
            .state
            .map { it.weather }
            .testFlow(
                CoroutineScope(UnconfinedTestDispatcher(testScheduler))
            ) {
                `when` {
                    useCase.get()
                }.then(
                    AsyncResult.NotStarted,
                    AsyncResult.Loading,
                    AsyncResult.Failure(IllegalStateException("Location not available"))
                )
            }
    }

    @Test
    fun `get with weather error`() = runTest(dispatcher) {
        // Given
        val lat = 1.2
        val lon = 3.4
        val unit = Unit.METRIC
        val location = Location.Available(lat = lat, long = lon, name = "Test Location")
        val error = RuntimeException("Weather error")
        every { locationRepository.selected } returns flowOf(location)
        coEvery { weatherRepository.fetch(lat, lon, unit) } coAnswers { throw error }

        useCase
            .state
            .map { it.weather }
            .testFlow(
                CoroutineScope(UnconfinedTestDispatcher(testScheduler))
            ) {
                `when` {
                    useCase.get()
                }.then(
                    AsyncResult.NotStarted,
                    AsyncResult.Loading,
                    AsyncResult.Failure(RuntimeException("Weather error"))
                )
            }
    }

}