package com.app.weatherforecast.feature.location.ui

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import app.cash.turbine.test
import com.app.weatherforecast.contract.location.Location
import com.app.weatherforecast.contract.location.Location.Available
import com.app.weatherforecast.core.model.AsyncResult
import com.app.weatherforecast.core.navigation.LocationRoute
import com.app.weatherforecast.core.navigation.LocationRoute.Mode
import com.app.weatherforecast.core.navigation.Route.PopupTo
import com.app.weatherforecast.core.navigation.WeatherRoute
import com.app.weatherforecast.core.utils.DispatcherProvider
import com.app.weatherforecast.feature.location.data.LocationProvider.LocationResult
import com.app.weatherforecast.feature.location.domain.LocationState
import com.app.weatherforecast.feature.location.domain.LocationUseCase
import com.app.weatherforecast.feature.location.presentation.LocationUiEffect.ShowNewSelection
import com.app.weatherforecast.feature.location.presentation.LocationUiEffect.ShowNewSelectionError
import com.app.weatherforecast.feature.location.presentation.LocationUiState.UiLocation
import com.app.weatherforecast.feature.location.presentation.LocationViewModel
import com.google.android.gms.maps.model.LatLng
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LocationViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val locationUseCase: LocationUseCase = mockk()
    private val dispatcherProvider = object : DispatcherProvider {
        override val io = dispatcher
        override val ui = dispatcher
        override val default = dispatcher
    }
    private val savedState = SavedStateHandle()

    @Before
    fun setUp() {
        every { locationUseCase.state } returns MutableStateFlow(LocationState())
        coEvery { locationUseCase.findSelectedLocation() } returns Unit

        mockkStatic("androidx.navigation.SavedStateHandleKt")
        every { savedState.toRoute<LocationRoute>() } returns LocationRoute(Mode.Create)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `mode Create shouldShowBackButton is false`() {
        // Given
        val viewModel = LocationViewModel(locationUseCase, dispatcherProvider, savedState)

        // When
        val shouldShowBackButton = viewModel.uiState.value.shouldShowBackButton

        // Then
        assert(shouldShowBackButton.not())
    }

    @Test
    fun `mode Update shouldShowBackButton is true`() {
        // Given
        every { savedState.toRoute<LocationRoute>() } returns LocationRoute(Mode.Update)
        val viewModel = LocationViewModel(locationUseCase, dispatcherProvider, savedState)

        // When
        val shouldShowBackButton = viewModel.uiState.value.shouldShowBackButton

        // Then
        assert(shouldShowBackButton)
    }

    @Test
    fun `findCurrentLocation calls useCase`() = runTest {
        // Given
        coEvery { locationUseCase.findCurrentLocation() } returns Unit

        // When
        val viewModel = LocationViewModel(locationUseCase, dispatcherProvider, savedState)
        viewModel.findCurrentLocation()

        // Then
        coVerify { locationUseCase.findCurrentLocation() }
    }

    @Test
    fun `decodeLocation calls useCase`() = runTest {
        // Given
        val latLng = LatLng(12.34, 56.78)
        coEvery { locationUseCase.decodeLocation(latLng.latitude, latLng.longitude) } returns Unit

        // When
        val viewModel = LocationViewModel(locationUseCase, dispatcherProvider, savedState)
        viewModel.decodeLocation(latLng)

        // Then
        coVerify { locationUseCase.decodeLocation(latLng.latitude, latLng.longitude) }
    }

    @Test
    fun `select calls useCase`() = runTest {
        // Given
        val location = Available("Test Location", 1.0, 2.0)
        coEvery { locationUseCase.selectLocation(location) } returns Unit

        // When
        val viewModel = LocationViewModel(locationUseCase, dispatcherProvider, savedState)
        viewModel.select(location)

        // Then
        coVerify { locationUseCase.selectLocation(location) }
    }

    @Test
    fun `uiState initial state`() = runTest(dispatcher) {
        // Given
        val viewModel = LocationViewModel(locationUseCase, dispatcherProvider, savedState)

        // When
        viewModel.uiState.test {
            // Then
            val item = awaitItem()
            assert(UiLocation.NotAvailable == item.selected)
            assert(UiLocation.NotAvailable == item.current)
            assert(UiLocation.NotAvailable == item.pinned)
            assert(false == item.isLoading)
        }
    }

    @Test
    fun `uiState when all states are Success with Available`() = runTest(dispatcher) {
        // Given
        val selected = Available("Test Location", 1.0, 2.0)
        val current = LocationResult(3.0, 4.0)
        val decode = Available("Test Location", 5.0, 6.0)
        val useCase = mockk<LocationUseCase> {
            coEvery { state } returns createStateFlow(
                selected = AsyncResult.Success(selected),
                current = AsyncResult.Success(current),
                decode = AsyncResult.Success(decode)
            )
            coEvery { findSelectedLocation() } returns Unit
        }
        val viewModel = LocationViewModel(useCase, dispatcherProvider, savedState)

        // When
        // Drop the initial state emission from StateFlow
        viewModel.uiState.drop(1).test {
            // Then
            val item = awaitItem()
            assert(UiLocation.Pinned("Test Location", 1.0, 2.0) == item.selected)
            assert(UiLocation.Map(3.0, long = 4.0) == item.current)
            assert(UiLocation.Pinned("Test Location", 5.0, 6.0) == item.pinned)
            assert(false == item.isLoading)
        }
    }

    @Test
    fun `uiState when selected is not success`() = runTest(dispatcher) {
        // Given
        val current = LocationResult(3.0, 4.0)
        val decode = Available("Test Location", 5.0, 6.0)
        val useCase = mockk<LocationUseCase> {
            coEvery { state } returns createStateFlow(
                selected = AsyncResult.Success(Location.NotAvailable),
                current = AsyncResult.Success(current),
                decode = AsyncResult.Success(decode)
            )
            coEvery { findSelectedLocation() } returns Unit
        }
        val viewModel = LocationViewModel(useCase, dispatcherProvider, savedState)

        // When
        // Drop the initial state emission from StateFlow
        viewModel.uiState.drop(1).test {
            // Then
            val item = awaitItem()
            assert(UiLocation.NotAvailable == item.selected)
            assert(UiLocation.Map(lat = 3.0, long = 4.0) == item.current)
            assert(UiLocation.Pinned("Test Location", 5.0, 6.0) == item.pinned)
            assert(false == item.isLoading)
        }
    }

    @Test
    fun `uiState when current is not success`() = runTest(dispatcher) {
        // Given
        val selected = Available("Test Location", 1.0, 2.0)
        val decode = Available("Test Location", 5.0, 6.0)
        val useCase = mockk<LocationUseCase> {
            coEvery { state } returns createStateFlow(
                selected = AsyncResult.Success(selected),
                current = AsyncResult.Failure(Throwable("no location")),
                decode = AsyncResult.Success(decode)
            )
            coEvery { findSelectedLocation() } returns Unit
        }
        val viewModel = LocationViewModel(useCase, dispatcherProvider, savedState)

        // When
        // Drop the initial state emission from StateFlow
        viewModel.uiState.drop(1).test {
            // Then
            val item = awaitItem()
            assert(UiLocation.Pinned("Test Location", 1.0, 2.0) == item.selected)
            assert(UiLocation.Map(1.0, 2.0) == item.current)
            assert(UiLocation.Pinned("Test Location", 5.0, 6.0) == item.pinned)
            assert(false == item.isLoading)
        }
    }

    @Test
    fun `uiState when current and decode are not success`() = runTest(dispatcher) {
        // Given
        val selected = Available("Test Location", 1.0, 2.0)
        val useCase = mockk<LocationUseCase> {
            coEvery { state } returns createStateFlow(
                selected = AsyncResult.Success(selected),
                current = AsyncResult.Failure(Throwable("no location")),
                decode = AsyncResult.Failure(Throwable("no location"))
            )
            coEvery { findSelectedLocation() } returns Unit
        }
        val viewModel = LocationViewModel(useCase, dispatcherProvider, savedState)

        // When
        // Drop the initial state emission from StateFlow
        viewModel.uiState.drop(1).test {
            // Then
            val item = awaitItem()
            assert(UiLocation.Pinned("Test Location", 1.0, 2.0) == item.selected)
            assert(UiLocation.Map(1.0, 2.0) == item.current)
            assert(UiLocation.NotAvailable == item.pinned)
            assert(false == item.isLoading)
        }
    }

    @Test
    fun `uiState when decode is in progress`() = runTest(dispatcher) {
        // Given
        val selected = Available("Test Location", 1.0, 2.0)
        val current = LocationResult(3.0, 4.0)
        val useCase = mockk<LocationUseCase> {
            coEvery { state } returns createStateFlow(
                selected = AsyncResult.Success(selected),
                current = AsyncResult.Success(current),
                decode = AsyncResult.Loading
            )
            coEvery { findSelectedLocation() } returns Unit
        }
        val viewModel = LocationViewModel(useCase, dispatcherProvider, savedState)

        // When
        // Drop the initial state emission from StateFlow
        viewModel.uiState.drop(1).test {
            // Then
            val item = awaitItem()
            assert(UiLocation.Pinned("Test Location", 1.0, 2.0) == item.selected)
            assert(UiLocation.Map(lat = 3.0, long = 4.0) == item.current)
            assert(UiLocation.NotAvailable == item.pinned)
            assert(item.isLoading)
        }
    }

    @Test
    fun `effects emits ShowNewSelection on decode success and mode Create`() = runTest(dispatcher) {
        // Given
        val decode = Available("Test Location", 5.0, 6.0)
        val stateFlow = createStateFlow()
        val useCase = mockk<LocationUseCase> {
            coEvery { state } returns stateFlow
            coEvery { findSelectedLocation() } returns Unit
        }
        val viewModel = LocationViewModel(useCase, dispatcherProvider, savedState).apply {
            stateFlow.update { it.copy(decode = AsyncResult.Success(decode)) }
        }

        // When
        viewModel.effects.test {
            // Then
            val effect = awaitItem()
            assert(ShowNewSelection(decode, PopupTo(LocationRoute(Mode.Create), true)) == effect)
        }
    }

    @Test
    fun `effects emits ShowNewSelection on decode success and mode Update`() = runTest(dispatcher) {
        // Given
        val decode = Available("Test Location", 5.0, 6.0)
        val stateFlow = createStateFlow()
        val useCase = mockk<LocationUseCase> {
            coEvery { state } returns stateFlow
            coEvery { findSelectedLocation() } returns Unit
        }
        every { savedState.toRoute<LocationRoute>() } returns LocationRoute(Mode.Update)
        val viewModel = LocationViewModel(useCase, dispatcherProvider, savedState).apply {
            stateFlow.update { it.copy(decode = AsyncResult.Success(decode)) }
        }

        // When
        viewModel.effects.test {
            // Then
            val effect = awaitItem()
            assert(ShowNewSelection(decode, PopupTo(WeatherRoute(), true)) == effect)
        }
    }

    @Test
    fun `effects emits ShowNewSelectionError on decode failure`() = runTest(dispatcher) {
        // Given
        val error = RuntimeException("Decode error")
        val stateFlow = createStateFlow()
        val useCase = mockk<LocationUseCase> {
            coEvery { state } returns stateFlow
            coEvery { findSelectedLocation() } returns Unit
        }
        val viewModel = LocationViewModel(useCase, dispatcherProvider, savedState).apply {
            stateFlow.update { it.copy(decode = AsyncResult.Failure(error)) }
        }

        // When
        viewModel.effects.test {
            // Then
            val effect = awaitItem()
            assert(ShowNewSelectionError == effect)
        }
    }

    private fun createStateFlow(
        selected: AsyncResult<Location> = AsyncResult.NotStarted,
        current: AsyncResult<LocationResult> = AsyncResult.NotStarted,
        decode: AsyncResult<Location> = AsyncResult.NotStarted,
    ): MutableStateFlow<LocationState> {
        return MutableStateFlow(LocationState(selected = selected, current = current, decode = decode))
    }

}