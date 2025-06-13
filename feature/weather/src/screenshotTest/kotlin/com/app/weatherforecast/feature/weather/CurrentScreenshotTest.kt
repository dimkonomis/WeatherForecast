package com.app.weatherforecast.feature.weather

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import coil3.annotation.ExperimentalCoilApi
import com.app.weatherforecast.feature.weather.presentation.components.CurrentPreview

class CurrentScreenshotTest {

    @OptIn(ExperimentalCoilApi::class)
    @Preview(widthDp = 600, heightDp = 800)
    @Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Composable
    fun CurrentTest() {
        CurrentPreview()
    }

}