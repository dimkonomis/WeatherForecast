package com.app.weatherforecast.feature.location.domain

import com.app.weatherforecast.contract.location.Location.Available
import com.app.weatherforecast.contract.location.LocationRepository
import com.app.weatherforecast.core.model.AsyncResult
import com.app.weatherforecast.core.test.TestFlow.Companion.testFlow
import com.app.weatherforecast.core.utils.DispatcherProvider
import com.app.weatherforecast.feature.location.data.LocationDecoder
import com.app.weatherforecast.feature.location.data.LocationDecoder.DecodeResult
import com.app.weatherforecast.feature.location.data.LocationProvider
import com.app.weatherforecast.feature.location.data.LocationProvider.LocationResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LocationUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val locationProvider: LocationProvider = mockk()
    private val locationDecoder: LocationDecoder = mockk()
    private val locationRepository: LocationRepository = mockk()
    private val dispatcherProvider = object : DispatcherProvider {
        override val io = dispatcher
        override val ui = dispatcher
        override val default = dispatcher
    }
    private lateinit var locationUseCase: LocationUseCase

    @Before
    fun setUp() {
        locationUseCase = LocationUseCase(
            locationProvider,
            locationDecoder,
            locationRepository,
            dispatcherProvider
        )
    }

    @Test
    fun `findCurrentLocation emits Loading then Success`() = runTest(dispatcher) {
        // Given
        val location = LocationResult(1.0, 2.0)
        every { locationProvider.location() } returns flow { emit(location) }

        locationUseCase
            .state
            .map { it.current }
            .testFlow(
                CoroutineScope(UnconfinedTestDispatcher(testScheduler))
            ) {
                `when` {
                    locationUseCase.findCurrentLocation()
                }.then(
                    AsyncResult.NotStarted,
                    AsyncResult.Loading,
                    AsyncResult.Success(location)
                )
            }
    }

    @Test
    fun `findCurrentLocation emits Loading then Failure`() = runTest(dispatcher) {
        // Given
        val error = RuntimeException("Location error")
        every { locationProvider.location() } returns flow { throw error }

        locationUseCase
            .state
            .map { it.current }
            .testFlow(
                CoroutineScope(UnconfinedTestDispatcher(testScheduler))
            ) {
                `when` {
                    locationUseCase.findCurrentLocation()
                }.then(
                    AsyncResult.NotStarted,
                    AsyncResult.Loading,
                    AsyncResult.Failure(error)
                )
            }
    }

    @Test
    fun `findSelectedLocation emits Loading then Success`() = runTest(dispatcher) {
        // Given
        val location = Available("Test Location", 1.0, 2.0)
        every { locationRepository.selected } returns flow { emit(location) }

        locationUseCase
            .state
            .map { it.selected }
            .testFlow(
                CoroutineScope(UnconfinedTestDispatcher(testScheduler))
            ) {
                `when` {
                    locationUseCase.findSelectedLocation()
                }.then(
                    AsyncResult.NotStarted,
                    AsyncResult.Loading,
                    AsyncResult.Success(location)
                )
            }
    }

    @Test
    fun `findSelectedLocation emits Loading then Failure`() = runTest(dispatcher) {
        // Given
        val error = RuntimeException("Location error")
        every { locationRepository.selected } returns flow { throw error }

        locationUseCase
            .state
            .map { it.selected }
            .testFlow(
                CoroutineScope(UnconfinedTestDispatcher(testScheduler))
            ) {
                `when` {
                    locationUseCase.findSelectedLocation()
                }.then(
                    AsyncResult.NotStarted,
                    AsyncResult.Loading,
                    AsyncResult.Failure(error)
                )
            }
    }

    @Test
    fun `decodeLocation emits Loading then Success`() = runTest(dispatcher) {
        // Given
        val result = DecodeResult.Success(Available("Test Location", 1.0, 2.0))
        every { locationDecoder.decodeLocation(1.0, 2.0) } returns flow { emit(result) }

        locationUseCase
            .state
            .map { it.decode }
            .testFlow(
                CoroutineScope(UnconfinedTestDispatcher(testScheduler))
            ) {
                `when` {
                    locationUseCase.decodeLocation(1.0, 2.0)
                }.then(
                    AsyncResult.NotStarted,
                    AsyncResult.Loading,
                    AsyncResult.Success(result.location)
                )
            }
    }

    @Test
    fun `decodeLocation emits Loading then AddressNotFound`() = runTest(dispatcher) {
        // Given
        val result = DecodeResult.AddressNotFound
        every { locationDecoder.decodeLocation(1.0, 2.0) } returns flow { emit(result) }

        locationUseCase
            .state
            .map { it.decode }
            .testFlow(
                CoroutineScope(UnconfinedTestDispatcher(testScheduler))
            ) {
                `when` {
                    locationUseCase.decodeLocation(1.0, 2.0)
                }.then(
                    AsyncResult.NotStarted,
                    AsyncResult.Loading,
                    AsyncResult.Failure(Throwable("Address not found"))
                )
            }
    }

    @Test
    fun `decodeLocation emits Loading then Failure`() = runTest(dispatcher) {
        // Given
        val error = RuntimeException("Decode error")
        every { locationDecoder.decodeLocation(1.0, 2.0) } returns flow { throw error }

        locationUseCase
            .state
            .map { it.decode }
            .testFlow(
                CoroutineScope(UnconfinedTestDispatcher(testScheduler))
            ) {
                `when` {
                    locationUseCase.decodeLocation(1.0, 2.0)
                }.then(
                    AsyncResult.NotStarted,
                    AsyncResult.Loading,
                    AsyncResult.Failure(error)
                )
            }
    }

    @Test
    fun `selectLocation calls repository and updates state`() = runTest(dispatcher) {
        // Given
        val location = Available("Test Location", 1.0, 2.0)
        val result = DecodeResult.Success(Available("Test Location", 1.0, 2.0))

        every { locationDecoder.decodeLocation(1.0, 2.0) } returns flow { emit(result) }
        coEvery { locationRepository.update(location) } returns Unit

        locationUseCase
            .state
            .map { it.decode }
            .testFlow(
                CoroutineScope(UnconfinedTestDispatcher(testScheduler))
            ) {
                `when` {
                    // Call decode first to simulate location selection
                    locationUseCase.decodeLocation(1.0, 2.0)
                    locationUseCase.selectLocation(location)
                }.then(
                    AsyncResult.NotStarted,
                    AsyncResult.Loading,
                    AsyncResult.Success(result.location),
                    AsyncResult.NotStarted,
                )
            }
        coVerify { locationRepository.update(location) }
    }
}
