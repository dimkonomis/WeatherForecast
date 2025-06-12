package com.app.weatherforecast.feature.location.domain

import com.app.weatherforecast.contract.location.Location
import com.app.weatherforecast.contract.location.LocationRepository
import com.app.weatherforecast.core.model.AsyncResult
import com.app.weatherforecast.core.utils.DispatcherProvider
import com.app.weatherforecast.feature.location.data.LocationDecoder
import com.app.weatherforecast.feature.location.data.LocationProvider
import com.app.weatherforecast.feature.location.data.LocationProvider.LocationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

internal data class LocationState(
    val selected: AsyncResult<Location> = AsyncResult.NotStarted,
    val current: AsyncResult<LocationResult> = AsyncResult.NotStarted,
    val decode: AsyncResult<Location> = AsyncResult.NotStarted
)

internal class LocationUseCase @Inject constructor(
    private val locationProvider: LocationProvider,
    private val locationDecoder: LocationDecoder,
    private val locationRepository: LocationRepository,
    private val dispatcherProvider: DispatcherProvider
) {

    val state: Flow<LocationState>
        field = MutableStateFlow(LocationState())

    suspend fun findCurrentLocation() {
        withContext(dispatcherProvider.io) {
            locationProvider.location()
                .take(1)
                .onStart {
                    state.update { currentState ->
                        currentState.copy(current = AsyncResult.Loading)
                    }
                }
                .catch {
                    Timber.e(it, "Error getting current location")
                    state.update { currentState ->
                        currentState.copy(current = AsyncResult.Failure(it))
                    }
                }
                .onEach {
                    state.update { currentState ->
                        currentState.copy(current = AsyncResult.Success(it))
                    }
                }.launchIn(this)
        }
    }

    suspend fun findSelectedLocation() {
        withContext(dispatcherProvider.io) {
            locationRepository.selected
                .onStart {
                    state.update { currentState ->
                        currentState.copy(selected = AsyncResult.Loading)
                    }
                }
                .catch {
                    Timber.e(it, "Error getting selected location")
                    state.update { currentState ->
                        currentState.copy(selected = AsyncResult.Failure(it))
                    }
                }
                .onEach {
                    state.update { currentState ->
                        currentState.copy(selected = AsyncResult.Success(it))
                    }
                }.launchIn(this)
        }
    }

    suspend fun decodeLocation(lat: Double, long: Double) {
        withContext(dispatcherProvider.io) {
            locationDecoder.decodeLocation(lat, long)
                .onStart {
                    state.update { currentState ->
                        currentState.copy(decode = AsyncResult.Loading)
                    }
                }
                .catch {
                    Timber.e(it, "Error decoding location name")
                    state.update { currentState ->
                        currentState.copy(decode = AsyncResult.Failure(it))
                    }
                }
                .onEach { result ->
                    when (result) {
                        is LocationDecoder.DecodeResult.Success -> {
                            state.update { currentState ->
                                currentState.copy(decode = AsyncResult.Success(result.location))
                            }
                        }
                        is LocationDecoder.DecodeResult.AddressNotFound -> {
                            state.update { currentState ->
                                currentState.copy(decode = AsyncResult.Failure(Throwable("Address not found")))
                            }
                        }
                    }
                }.launchIn(this)
        }
    }

    suspend fun selectLocation(location: Location.Available) {
        withContext(dispatcherProvider.io) {
            locationRepository.update(location)
            state.update { currentState -> currentState.copy(decode = AsyncResult.NotStarted) }
        }
    }

}