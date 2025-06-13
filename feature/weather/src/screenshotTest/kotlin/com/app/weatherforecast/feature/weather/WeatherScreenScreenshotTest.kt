package com.app.weatherforecast.feature.weather

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.app.weatherforecast.feature.weather.presentation.ScreenErrorPreview
import com.app.weatherforecast.feature.weather.presentation.ScreenLoadingPreview
import com.app.weatherforecast.feature.weather.presentation.ScreenSuccessPreview

class WeatherScreenScreenshotTest {

    @Preview(widthDp = 600, heightDp = 800)
    @Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Composable
    fun ScreenLoadingTest() {
        ScreenLoadingPreview()
    }

    @Preview(widthDp = 600, heightDp = 800)
    @Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Composable
    fun ScreenErrorTest() {
        ScreenErrorPreview()
    }

    @Preview(widthDp = 600, heightDp = 800)
    @Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Composable
    fun ScreenSuccessTest() {
        ScreenSuccessPreview()
    }

}