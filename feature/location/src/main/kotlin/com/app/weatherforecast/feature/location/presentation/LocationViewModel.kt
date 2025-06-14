package com.app.weatherforecast.feature.location.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.app.weatherforecast.contract.location.Location.Available
import com.app.weatherforecast.core.model.AsyncResult
import com.app.weatherforecast.core.model.AsyncResult.Success
import com.app.weatherforecast.core.navigation.LocationRoute
import com.app.weatherforecast.core.navigation.LocationRoute.Mode
import com.app.weatherforecast.core.navigation.Route.PopupTo
import com.app.weatherforecast.core.navigation.WeatherRoute
import com.app.weatherforecast.core.utils.DispatcherProvider
import com.app.weatherforecast.core.utils.optional
import com.app.weatherforecast.feature.location.domain.LocationUseCase
import com.app.weatherforecast.feature.location.presentation.LocationUiState.UiLocation
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.produceIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class LocationViewModel @Inject constructor(
    private val useCase: LocationUseCase,
    private val dispatcherProvider: DispatcherProvider,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    init {
        viewModelScope.launch(dispatcherProvider.ui) {
            useCase.findSelectedLocation()
        }
    }

    private val route = savedStateHandle.toRoute<LocationRoute>()
    private val shouldShowBackButton = route.mode == Mode.Update

    val uiState: StateFlow<LocationUiState> = useCase.state
        .distinctUntilChanged()
        .map { state ->
            val selected = when {
                state.selected is Success && state.selected.value is Available -> {
                    val available = state.selected.value as Available
                    UiLocation.Pinned(
                        name = available.name,
                        lat = available.lat,
                        long = available.long
                    )
                }

                else -> UiLocation.NotAvailable
            }
            val current = when {
                state.current is Success -> UiLocation.Map(
                    lat = state.current.value.lat,
                    long = state.current.value.long
                )

                state.selected is Success && state.selected.value is Available -> {
                    val available = state.selected.value as Available
                    UiLocation.Map(
                        lat = available.lat,
                        long = available.long
                    )
                }

                else -> UiLocation.NotAvailable
            }
            val pinned = when {
                state.decode is Success && state.decode.value is Available -> {
                    val available = state.decode.value as Available
                    UiLocation.Pinned(
                        name = available.name,
                        lat = available.lat,
                        long = available.long
                    )
                }

                else -> UiLocation.NotAvailable
            }
            LocationUiState(
                selected = selected,
                current = current,
                pinned = pinned,
                isLoading = state.decode is AsyncResult.Loading,
                shouldShowBackButton = shouldShowBackButton
            ).also {
                Timber.d("Location UI State: $it")
            }
        }
        .stateIn(
            viewModelScope,
            WhileSubscribed(stopTimeoutMillis = 300L),
            LocationUiState(shouldShowBackButton = shouldShowBackButton)
        )

    val effects: Flow<LocationUiEffect> = channelFlow {
        merge(
            useCase.state
                .optional { it.decode.success?.value?.available }
                .onEach {
                    send(
                        LocationUiEffect.ShowNewSelection(
                            available = it,
                            popupTo = when (val mode = route.mode) {
                                Mode.Create -> PopupTo(LocationRoute(mode), true)
                                Mode.Update -> PopupTo(WeatherRoute(), true)
                            }
                        )
                    )
                },
            useCase.state
                .optional { it.decode.failure }
                .onEach {
                    send(LocationUiEffect.ShowNewSelectionError)
                }
        ).collect()
    }.produceIn(viewModelScope).receiveAsFlow()

    fun findCurrentLocation() {
        viewModelScope.launch(dispatcherProvider.ui) {
            useCase.findCurrentLocation()
        }
    }

    fun decodeLocation(latLng: LatLng) {
        viewModelScope.launch(dispatcherProvider.ui) {
            useCase.decodeLocation(latLng.latitude, latLng.longitude)
        }
    }

    fun select(available: Available) {
        viewModelScope.launch(dispatcherProvider.ui) {
            useCase.selectLocation(available)
        }
    }

}