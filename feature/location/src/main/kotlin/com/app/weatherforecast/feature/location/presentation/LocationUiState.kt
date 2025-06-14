package com.app.weatherforecast.feature.location.presentation

import com.app.weatherforecast.contract.location.Location
import com.app.weatherforecast.core.navigation.Route
import com.google.android.gms.maps.model.LatLng

/**
 * Represents the UI state for the Location feature.
 * params:
 * - [selected] - The selected location.
 * - [current] - The location based on device location.
 * - [pinned] - The new pinned location.
 * - [isLoading] - Indicates if the pinned location is being decoded.
 * - [shouldShowBackButton] - Indicates if the back button should be shown.
 */
internal data class LocationUiState(
    val selected: UiLocation = UiLocation.NotAvailable,
    val current: UiLocation = UiLocation.NotAvailable,
    val pinned: UiLocation = UiLocation.NotAvailable,
    val isLoading: Boolean = false,
    val shouldShowBackButton: Boolean = false
) {

    sealed interface UiLocation {
        val lat: Double
        val long: Double

        data class Pinned(
            val name: String,
            override val lat: Double,
            override val long: Double
        ) : UiLocation

        data class Map(
            override val lat: Double,
            override val long: Double
        ) : UiLocation

        data object NotAvailable : UiLocation {
            override val lat: Double = 0.0
            override val long: Double = 0.0
        }
    }

}

internal sealed interface LocationUiAction {
    data class SelectLocation(val latLng: LatLng) : LocationUiAction
    data object RequestLocation : LocationUiAction

    sealed interface Navigation : LocationUiAction {
        data object Back : LocationUiAction
    }

}

internal sealed interface LocationUiEffect {
    data class ShowNewSelection(
        val available: Location.Available,
        val popupTo: Route.PopupTo
    ) : LocationUiEffect
    data object ShowNewSelectionError : LocationUiEffect
}