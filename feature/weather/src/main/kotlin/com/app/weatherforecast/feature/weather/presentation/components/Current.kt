package com.app.weatherforecast.feature.weather.presentation.components

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.annotation.ExperimentalCoilApi
import com.app.weatherforecast.core.ui.theme.AppTheme
import com.app.weatherforecast.feature.weather.R
import com.app.weatherforecast.feature.weather.presentation.WeatherUiState.Success.Current
import com.app.weatherforecast.feature.weather.presentation.decimalFormatter
import com.app.weatherforecast.feature.weather.presentation.dummyWeatherUiState
import java.text.DecimalFormat

@Composable
internal fun Current(
    current: Current
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {

        AsyncImage(
            model = current.icon,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.weather_label_placeholder_temperature, decimalFormatter.format(current.temperature)),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = current.summary,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.weather_label_feel_like, decimalFormatter.format(current.feelsLike)),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(
                alpha = 0.75f
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Container(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Condition(
                        title = stringResource(R.string.weather_label_visibility),
                        value = stringResource(R.string.weather_label_placeholder_km, decimalFormatter.format(current.visibility)),
                        icon = R.drawable.ic_feels,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Condition(
                        title = stringResource(R.string.weather_label_humidity),
                        value = stringResource(R.string.weather_label_placeholder_percentage, decimalFormatter.format(current.humidity)),
                        icon = R.drawable.ic_humidity,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Condition(
                        title = stringResource(R.string.weather_label_wind),
                        value = stringResource(R.string.weather_label_placeholder_kmh, decimalFormatter.format(current.windSpeed)),
                        icon = R.drawable.ic_wind,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Condition(
                        title = stringResource(R.string.weather_label_pressure),
                        value = stringResource(R.string.weather_label_placeholder_pressure, decimalFormatter.format(current.pressure)),
                        icon = R.drawable.ic_pressure,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun Condition(
    title: String,
    value: String,
    @DrawableRes icon: Int,
    modifier: Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(
                alpha = 0.5f
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(
                alpha = 0.5f
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }

}

@OptIn(ExperimentalCoilApi::class)
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun CurrentPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Current(
                current = dummyWeatherUiState.current
            )
        }

    }
}