package com.app.weatherforecast.feature.weather

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.app.weatherforecast.feature.weather.presentation.components.TodayPreview

class TodayScreenshotTest {

    @Preview(widthDp = 600, heightDp = 800)
    @Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Composable
    fun TodayTest() {
        TodayPreview()
    }

}