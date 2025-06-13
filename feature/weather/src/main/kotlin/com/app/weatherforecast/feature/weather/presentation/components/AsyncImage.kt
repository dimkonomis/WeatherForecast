package com.app.weatherforecast.feature.weather.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.app.weatherforecast.feature.weather.R

/**
 * AsyncImage placeholder doesn't in preview mode and in screenshot tests.
 * One solution is to provide an AsyncImagePreviewHandler that is used in preview mode.
 * https://coil-kt.github.io/coil/compose/#previews
 * But instead if wrapping all the previews in CompositionLocalProvider, any easier solution is to
 * use a placeholder hardcoded circle when in preview mode.
 */
@Composable
internal fun AsyncImage(
    model: String,
    modifier: Modifier
) {
    if (LocalInspectionMode.current) {
        Badge(
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = modifier
        )
    } else {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(model)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = modifier,
        )
    }

}