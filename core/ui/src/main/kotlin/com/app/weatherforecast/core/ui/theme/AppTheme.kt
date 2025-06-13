package com.app.weatherforecast.core.ui.theme

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DarkColorPalette = darkColorScheme(
    primary = Color(0xFF0094ff),
    onPrimary = Color.White,
    secondary = Color(0xFF8A56AC),
    onSecondary = Color(0xFF8F95A4),
    background = Color(0xFF343434),
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF1E1E2A),
    error = Color(0xFFFF3939),
    onError = Color.White
)

private val LightColorPalette = lightColorScheme(
    primary = Color(0xFF0094ff),
    onPrimary = Color.White,
    secondary = Color(0xFF8A56AC),
    onSecondary = Color(0xFF8F95A4),
    background = Color(0xFFF5F5F5),
    onBackground = Color.Black,
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF333333),
    surfaceVariant = Color(0xFFF5F8F9),
    error = Color(0xFFFF3939),
    onError = Color.White
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette

    MaterialTheme(
        colorScheme = colors,
        typography = Typography(
            displayLarge = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                color = colors.onBackground
            ),
            displayMedium = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = colors.onBackground
            ),
            displaySmall = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 28.sp,
                color = colors.onBackground
            ),
            headlineLarge = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = colors.onBackground
            ),
            headlineMedium = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 22.sp,
                color = colors.onBackground
            ),
            headlineSmall = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                color = colors.onBackground
            ),
            titleLarge = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = colors.onSurface
            ),
            titleMedium = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = colors.onSurface
            ),
            titleSmall = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = colors.onSurface
            ),
            bodyLarge = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = colors.onBackground
            ),
            bodyMedium = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = colors.onBackground
            ),
            bodySmall = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = colors.onBackground
            ),
            labelLarge = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = colors.onPrimary
            ),
            labelMedium = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = colors.onPrimary
            ),
            labelSmall = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 10.sp,
                color = colors.onPrimary
            )
        ),
        content = content
    )
}

@Composable
private fun ThemeColorsPreview() {
    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Color Preview
            ColorRow("Primary", MaterialTheme.colorScheme.primary)
            ColorRow("On Primary", MaterialTheme.colorScheme.onPrimary)
            ColorRow("Secondary", MaterialTheme.colorScheme.secondary)
            ColorRow("On Secondary", MaterialTheme.colorScheme.onSecondary)
            ColorRow("Background", MaterialTheme.colorScheme.background)
            ColorRow("On Background", MaterialTheme.colorScheme.onBackground)
            ColorRow("Surface", MaterialTheme.colorScheme.surface)
            ColorRow("On Surface", MaterialTheme.colorScheme.onSurface)
            ColorRow("Error", MaterialTheme.colorScheme.error)
            ColorRow("On Error", MaterialTheme.colorScheme.onError)
        }
    }
}

@Composable
private fun ThemeTypographyPreview() {
    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Typography Preview
            TypographyRow("Display Medium", MaterialTheme.typography.displayMedium)
            TypographyRow("Display Small", MaterialTheme.typography.displaySmall)
            TypographyRow("Headline Large", MaterialTheme.typography.headlineLarge)
            TypographyRow("Headline Medium", MaterialTheme.typography.headlineMedium)
            TypographyRow("Headline Small", MaterialTheme.typography.headlineSmall)
            TypographyRow("Title Large", MaterialTheme.typography.titleLarge)
            TypographyRow("Title Medium", MaterialTheme.typography.titleMedium)
            TypographyRow("Title Small", MaterialTheme.typography.titleSmall)
            TypographyRow("Body Large", MaterialTheme.typography.bodyLarge)
            TypographyRow("Body Medium", MaterialTheme.typography.bodyMedium)
            TypographyRow("Body Small", MaterialTheme.typography.bodySmall)
            TypographyRow("Label Large", MaterialTheme.typography.labelLarge)
            TypographyRow("Label Medium", MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun ColorRow(label: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color)
        )
        Text(text = label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
private fun TypographyRow(label: String, textStyle: TextStyle) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onBackground)
        Text(text = "The quick brown fox jumps over the lazy dog", style = textStyle, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun ThemeColorsPreviewScreen() {
    ThemeColorsPreview()
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun ThemeTypographyPreviewScreen() {
    ThemeTypographyPreview()
}