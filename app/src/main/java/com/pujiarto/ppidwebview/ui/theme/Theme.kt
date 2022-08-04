package com.pujiarto.ppidwebview.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColors(
    primary = LobarBlue,
    primaryVariant = dark,
    secondary = Teal200
)

private val LightColorPalette = lightColors(
    primary = LobarBlue,
    primaryVariant = dark,
    secondary = Teal200
)


@Composable

fun PpidWebviewTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = MaterialTheme.colors.isLight
    val colors = if (darkTheme) {
        systemUiController.setSystemBarsColor(
            color = Color(0xff1f2fab),
        )
        DarkColorPalette
    } else {
        systemUiController.setSystemBarsColor(
            color = Color(0xff1f2fab),
            darkIcons = useDarkIcons
        )
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}