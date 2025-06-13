package com.app.weatherforecast.feature.weather.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

@Composable
fun Container(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(backgroundColor)
            .then(modifier)
    ) {
        content()
    }
}