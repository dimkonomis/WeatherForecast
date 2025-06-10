package com.app.weatherforecast.core.ui

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.app.weatherforecast.core.ui.theme.ThemeColorsPreviewScreen
import com.app.weatherforecast.core.ui.theme.ThemeTypographyPreviewScreen

class AppThemeScreenshotTest {

    @Preview
    @Composable
    fun ThemeColorsPreviewTest() {
        ThemeColorsPreviewScreen()
    }

    @Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Composable
    fun ThemeColorsDarkPreviewTest() {
        ThemeColorsPreviewScreen()
    }

    @Preview
    @Composable
    fun ThemeTypographyPreviewTest() {
        ThemeTypographyPreviewScreen()
    }

    @Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Composable
    fun ThemeTypographyDarkPreviewTest() {
        ThemeTypographyPreviewScreen()
    }

}