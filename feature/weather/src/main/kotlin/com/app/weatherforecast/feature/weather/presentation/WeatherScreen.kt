package com.app.weatherforecast.feature.weather.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.annotation.ExperimentalCoilApi
import com.app.weatherforecast.core.ui.theme.AppTheme
import com.app.weatherforecast.feature.weather.R
import com.app.weatherforecast.feature.weather.presentation.WeatherUiAction.Navigation
import com.app.weatherforecast.feature.weather.presentation.WeatherUiAction.Retry
import com.app.weatherforecast.feature.weather.presentation.WeatherUiState.Success
import com.app.weatherforecast.feature.weather.presentation.components.Container
import com.app.weatherforecast.feature.weather.presentation.components.Current
import com.app.weatherforecast.feature.weather.presentation.components.Forecast
import com.app.weatherforecast.feature.weather.presentation.components.Header
import com.app.weatherforecast.feature.weather.presentation.components.Today

@Composable
fun WeatherScreen() {
    val context = LocalContext.current
    val viewModel = hiltViewModel<WeatherViewModel>()

    val actions = remember {
        { action: WeatherUiAction ->
            when (action) {
                Retry -> viewModel.retry()
                Navigation.SelectLocation -> TODO()
            }
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WeatherScreenContent(uiState, actions)
}

@Composable
internal fun WeatherScreenContent(
    state: WeatherUiState,
    actions: (WeatherUiAction) -> Unit,
) {
    when (state) {
        is WeatherUiState.Loading -> WeatherScreenLoading(actions = actions)
        is WeatherUiState.Error -> WeatherScreenError(actions = actions)
        is Success -> WeatherScreenSuccess(state = state, actions = actions)
    }
}

@Composable
private fun WeatherScreenLoading(actions: (WeatherUiAction) -> Unit) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { Header(actions) }
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onBackground.copy(
                    alpha = 0.25f
                ),
            )
        }
    }
}

@Composable
private fun WeatherScreenError(actions: (WeatherUiAction) -> Unit) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { Header(actions) },
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Container(
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.Center)
                ) {
                    Text(
                        text = stringResource(R.string.weather_label_error),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { actions(Retry) },
                        shape = ShapeDefaults.Small,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.weather_button_try_again),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherScreenSuccess(state: Success, actions: (WeatherUiAction) -> Unit) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { Header(state.location, actions) }
    ) { innerPadding ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {

            item {
                Current(state.current)
            }

            if (state.today.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.weather_label_today),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                item {
                    Container(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(state.today) {
                                Today(it)
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = stringResource(R.string.weather_label_forecast),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            item {
                Container(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        state.forecast.forEach {
                            Forecast(it)
                        }
                    }
                }
            }

        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun ScreenLoadingPreview() {
    AppTheme {
        WeatherScreenLoading {

        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun ScreenErrorPreview() {
    AppTheme {
        WeatherScreenError {

        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun ScreenSuccessPreview() {
    AppTheme {
        WeatherScreenSuccess(
            state = dummyWeatherUiState,
            actions = {

            }
        )
    }
}