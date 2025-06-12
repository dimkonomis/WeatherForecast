package com.app.weatherforecast.feature.location.presentation

import com.app.weatherforecast.contract.location.Location
import com.google.android.gms.maps.model.LatLng

/**
 * Represents the UI state for the Location feature.
 * params:
 * - [selected] - The selected location.
 * - [current] - The location based on device location.
 * - [pinned] - The new pinned location.
 */
internal data class LocationUiState(
    val selected: Location = Location.NotAvailable,
    val current: Location = Location.NotAvailable,
    val pinned: Location = Location.NotAvailable,
    val isLoading: Boolean = false
)

internal sealed interface LocationUiAction {
    data class SelectLocation(val latLng: LatLng) : LocationUiAction
    data object RequestLocation : LocationUiAction

    sealed interface Navigation : LocationUiAction {
        data object Forecast : Navigation
        data object Back : LocationUiAction
    }

}

internal sealed interface LocationUiEffect {
    data class ShowNewSelection(val available: Location.Available) : LocationUiEffect
    data object ShowNewSelectionError : LocationUiEffect
}