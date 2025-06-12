package com.app.weatherforecast.feature.location.domain

import com.app.weatherforecast.contract.location.Location.Available
import com.app.weatherforecast.contract.location.LocationRepository
import com.app.weatherforecast.core.model.AsyncResult
import com.app.weatherforecast.core.utils.DispatcherProvider
import com.app.weatherforecast.feature.location.data.LocationDecoder
import com.app.weatherforecast.feature.location.data.LocationDecoder.DecodeResult
import com.app.weatherforecast.feature.location.data.LocationProvider
import com.app.weatherforecast.feature.location.data.LocationProvider.LocationResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LocationUseCaseTest {

    private val dispatcher = UnconfinedTestDispatcher()
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
        val states = mutableListOf<LocationState>()
        val job = launch { locationUseCase.state.toList(states) }
        every { locationProvider.location() } returns flow { delay(50); emit(location) }

        // When
        locationUseCase.findCurrentLocation()

        // Then
        assert(AsyncResult.Loading == states[1].current)
        assert(AsyncResult.Success(location) == states[2].current)

        job.cancel()
    }

    @Test
    fun `findCurrentLocation emits Loading then Failure`() = runTest(dispatcher) {
        // Given
        val error = RuntimeException("Location error")
        val states = mutableListOf<LocationState>()
        val job = launch { locationUseCase.state.toList(states) }
        every { locationProvider.location() } returns flow { delay(50); throw error }

        // When
        locationUseCase.findCurrentLocation()

        // Then
        assert(AsyncResult.Loading == states[1].current)
        assert(AsyncResult.Failure(error) == states[2].current)

        job.cancel()
    }

    @Test
    fun `findSelectedLocation emits Loading then Success`() = runTest(dispatcher) {
        // Given
        val location = Available("Test Location", 1.0, 2.0)
        val states = mutableListOf<LocationState>()
        val job = launch { locationUseCase.state.toList(states) }
        every { locationRepository.selected } returns flow { delay(50); emit(location) }

        // When
        locationUseCase.findSelectedLocation()

        // Then
        assert(AsyncResult.Loading == states[1].selected)
        assert(AsyncResult.Success(location) == states[2].selected)

        job.cancel()
    }

    @Test
    fun `findSelectedLocation emits Loading then Failure`() = runTest(dispatcher) {
        // Given
        val error = RuntimeException("Location error")
        val states = mutableListOf<LocationState>()
        val job = launch { locationUseCase.state.toList(states) }
        every { locationRepository.selected } returns flow { delay(50); throw error }

        // When
        locationUseCase.findSelectedLocation()

        // Then
        assert(AsyncResult.Loading == states[1].selected)
        assert(AsyncResult.Failure(error) == states[2].selected)

        job.cancel()
    }

    @Test
    fun `decodeLocation emits Loading then Success`() = runTest(dispatcher) {
        // Given
        val result = DecodeResult.Success(Available("Test Location", 1.0, 2.0))
        val states = mutableListOf<LocationState>()
        val job = launch { locationUseCase.state.toList(states) }
        every { locationDecoder.decodeLocation(1.0, 2.0) } returns flow { delay(50); emit(result) }

        // When
        locationUseCase.decodeLocation(1.0, 2.0)

        // Then
        assert(AsyncResult.Loading == states[1].decode)
        assert(AsyncResult.Success(result.location) == states[2].decode)

        job.cancel()
    }

    @Test
    fun `decodeLocation emits Loading then AddressNotFound`() = runTest(dispatcher) {
        // Given
        val result = DecodeResult.AddressNotFound
        val states = mutableListOf<LocationState>()
        val job = launch { locationUseCase.state.toList(states) }
        every { locationDecoder.decodeLocation(1.0, 2.0) } returns flow { delay(50); emit(result) }

        // When
        locationUseCase.decodeLocation(1.0, 2.0)

        // Then
        assert(AsyncResult.Loading == states[1].decode)
        assert(AsyncResult.Failure(Throwable("Address not found")) == states[2].decode)

        job.cancel()
    }

    @Test
    fun `decodeLocation emits Loading then Failure`() = runTest(dispatcher) {
        // Given
        val error = RuntimeException("Decode error")
        val states = mutableListOf<LocationState>()
        val job = launch { locationUseCase.state.toList(states) }
        every { locationDecoder.decodeLocation(1.0, 2.0) } returns flow { delay(50); throw error }

        // When
        locationUseCase.decodeLocation(1.0, 2.0)

        // Then
        assert(AsyncResult.Loading == states[1].decode)
        assert(AsyncResult.Failure(error) == states[2].decode)

        job.cancel()
    }

    @Test
    fun `selectLocation calls repository and updates state`() = runTest(dispatcher) {
        // Given
        val location = Available("Test Location", 1.0, 2.0)
        val result = DecodeResult.Success(Available("Test Location", 1.0, 2.0))
        val states = mutableListOf<LocationState>()
        val job = launch { locationUseCase.state.toList(states) }
        every { locationDecoder.decodeLocation(1.0, 2.0) } returns flow { emit(result) }
        coEvery { locationRepository.update(location) } returns Unit

        // When
        locationUseCase.decodeLocation(1.0, 2.0) // Call decode first to simulate location selection
        locationUseCase.selectLocation(location)

        // Then
        coVerify { locationRepository.update(location) }
        assert(AsyncResult.NotStarted == states[2].decode)

        job.cancel()
    }
}
