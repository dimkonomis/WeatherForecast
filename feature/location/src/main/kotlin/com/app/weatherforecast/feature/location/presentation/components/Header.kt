package com.app.weatherforecast.feature.location.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.app.weatherforecast.feature.location.R
import com.app.weatherforecast.feature.location.presentation.LocationUiAction
import com.app.weatherforecast.feature.location.presentation.LocationUiAction.Navigation
import com.app.weatherforecast.feature.location.presentation.LocationUiAction.RequestLocation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun Header(shouldShowBackButton: Boolean, actions: (LocationUiAction) -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Select Location",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
        },
        navigationIcon = {
            if (shouldShowBackButton.not()) return@TopAppBar

            IconButton(onClick = { actions(Navigation.Back) }) {
                Icon(
                    Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
        },
        actions = {
            IconButton(onClick = { actions(RequestLocation) }) {
                Icon(
                    painter = painterResource(R.drawable.ic_location),
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}
