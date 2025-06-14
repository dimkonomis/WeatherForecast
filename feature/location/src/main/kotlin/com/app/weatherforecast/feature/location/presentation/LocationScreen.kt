package com.app.weatherforecast.feature.location.presentation

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.weatherforecast.core.navigation.Back.Companion.back
import com.app.weatherforecast.core.navigation.RouteViewModel
import com.app.weatherforecast.core.navigation.WeatherRoute
import com.app.weatherforecast.core.ui.extensions.SingleEventEffect
import com.app.weatherforecast.core.ui.extensions.dismiss
import com.app.weatherforecast.feature.location.R
import com.app.weatherforecast.feature.location.presentation.LocationUiAction.Navigation
import com.app.weatherforecast.feature.location.presentation.LocationUiAction.RequestLocation
import com.app.weatherforecast.feature.location.presentation.LocationUiAction.SelectLocation
import com.app.weatherforecast.feature.location.presentation.LocationUiState.UiLocation
import com.app.weatherforecast.feature.location.presentation.components.Header
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationScreen() {
    val context = LocalContext.current
    val routeViewModel: RouteViewModel = hiltViewModel(
        viewModelStoreOwner = LocalContext.current as androidx.activity.ComponentActivity
    )
    val viewModel = hiltViewModel<LocationViewModel>()
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
    ){ permissions ->
        val areALlPermissionsGranted = permissions.values.reduce { acc, next ->
            acc && next
        }

        if (areALlPermissionsGranted) {
            viewModel.findCurrentLocation()
        }
    }
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        snackBarHostState.showSnackbar(
            message = context.getString(R.string.location_label_disclaimer),
            actionLabel = context.getString(R.string.location_label_disclaimer_action),
            duration = SnackbarDuration.Indefinite
        )
        snackBarHostState.dismiss()
    }

    LaunchedEffect(permissionState) {
        when {
            permissionState.allPermissionsGranted -> viewModel.findCurrentLocation()
            permissionState.shouldShowRationale -> scope.launch {
                val result = snackBarHostState.showSnackbar(
                    message = context.getString(R.string.location_label_permission_required),
                    actionLabel = context.getString(R.string.location_label_permission_required_action)
                )
                when (result) {
                    SnackbarResult.ActionPerformed -> permissionState.launchMultiplePermissionRequest()
                    SnackbarResult.Dismissed -> Unit
                }
            }
            else -> permissionState.launchMultiplePermissionRequest()
        }
    }

    val actions = remember {
        { action: LocationUiAction ->
            when (action) {
                is SelectLocation -> viewModel.decodeLocation(action.latLng)
                RequestLocation -> permissionState.launchMultiplePermissionRequest()
                Navigation.Back -> routeViewModel.navigate(back)
            }
        }
    }

    SingleEventEffect(sideEffectFlow = viewModel.effects) { effect ->
        when (effect) {
            is LocationUiEffect.ShowNewSelection -> scope.launch {
                snackBarHostState.dismiss()

                val result = snackBarHostState.showSnackbar(
                    message = context.getString(R.string.location_label_position_selected, effect.available.name),
                    actionLabel = context.getString(R.string.location_label_position_selected_action),
                    duration = SnackbarDuration.Indefinite
                )
                when (result) {
                    SnackbarResult.ActionPerformed -> {
                        viewModel.select(effect.available)
                        routeViewModel.navigate(WeatherRoute(popupTo = effect.popupTo))
                    }
                    SnackbarResult.Dismissed -> Unit
                }
            }
            LocationUiEffect.ShowNewSelectionError -> scope.launch {
                snackBarHostState.dismiss()

                snackBarHostState.showSnackbar(
                    message = context.getString(R.string.location_label_position_selection_failed),
                    duration = SnackbarDuration.Long
                )
            }
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LocationContent(uiState, permissionState.allPermissionsGranted, snackBarHostState, actions)
}

@Composable
private fun LocationContent(
    uiState: LocationUiState,
    permissionsGranted: Boolean,
    snackBarHostState: SnackbarHostState,
    actions: (LocationUiAction) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState) {
                Snackbar(
                    snackbarData = it,
                    containerColor = MaterialTheme.colorScheme.background,
                    actionColor = MaterialTheme.colorScheme.primary
                )
            }
        },
        topBar = { Header(uiState.shouldShowBackButton, actions) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {

            val cameraPositionState = rememberCameraPositionState()

            LaunchedEffect(uiState.current) {
                when (val current = uiState.current) {
                    is UiLocation.Map -> cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLngZoom(LatLng(current.lat, current.long), 12f),
                        durationMs = 500
                    )
                    else -> Unit
                }
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = permissionsGranted
                ),
                onMapLongClick = { actions(SelectLocation(it))  },
            ) {
                when (val pinned = uiState.pinned) {
                    is UiLocation.Pinned -> MarkerComposable(
                        state = MarkerState(position = LatLng(pinned.lat, pinned.long)),
                        title = pinned.name
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_pin),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    else -> Unit
                }
                when (val selected = uiState.selected) {
                    is UiLocation.Pinned -> Marker(
                        state = MarkerState(position = LatLng(selected.lat, selected.long)),
                        title = selected.name
                    )
                    else -> Unit
                }
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(64.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.25f
                        ),
                    )
                }
            }
        }
    }
}