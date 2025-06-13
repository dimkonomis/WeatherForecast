package com.app.weatherforecast.feature.weather.presentation.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.app.weatherforecast.feature.weather.R
import com.app.weatherforecast.feature.weather.presentation.WeatherUiAction
import com.app.weatherforecast.feature.weather.presentation.WeatherUiAction.Navigation

@Composable
internal fun Header(actions: (WeatherUiAction) -> Unit) {
    HeaderComponent(stringResource(R.string.weather_label_title), actions)
}

@Composable
internal fun Header(location: String, actions: (WeatherUiAction) -> Unit) {
    HeaderComponent(location, actions)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HeaderComponent(text: String, actions: (WeatherUiAction) -> Unit) {

    TopAppBar(
        title = {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
        },
        actions = {
            IconButton(
                onClick = { actions(Navigation.SelectLocation) },
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_down),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}
