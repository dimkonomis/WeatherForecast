package com.app.weatherforecast.feature.weather.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.annotation.ExperimentalCoilApi
import com.app.weatherforecast.core.ui.theme.AppTheme
import com.app.weatherforecast.feature.weather.R
import com.app.weatherforecast.feature.weather.presentation.WeatherUiState.Success.Forecast
import com.app.weatherforecast.feature.weather.presentation.decimalFormatter
import com.app.weatherforecast.feature.weather.presentation.dummyWeatherUiState

@Composable
internal fun Forecast(
    forecast: Forecast,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .then(modifier)
    ) {
        Text(
            text = forecast.dateTime,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(60.dp)
        )

        AsyncImage(
            model = forecast.icon,
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = forecast.summary,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(
                alpha = 0.5f
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(
                R.string.weather_label_placeholder_temperature,
                decimalFormatter.format(forecast.min)
            ),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurface.copy(
                alpha = 0.5f
            )
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = stringResource(
                R.string.weather_label_placeholder_temperature,
                decimalFormatter.format(forecast.max)
            ),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }

}

@OptIn(ExperimentalCoilApi::class)
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun ForecastPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Forecast(
                forecast = dummyWeatherUiState.forecast.first(),
            )
        }
    }
}