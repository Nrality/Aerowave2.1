/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.metrolist.music.ui.theme

import android.graphics.Bitmap
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.palette.graphics.Palette
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicColorScheme
import com.materialkolor.score.Score

val DefaultThemeColor = Color(0xFF00A9C7)

@Composable
fun MetrolistTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    pureBlack: Boolean = false,
    themeColor: Color = DefaultThemeColor,
    aeroEnabled: Boolean = false,
    content: @Composable () -> Unit,
) {
    val baseColorScheme = rememberDynamicColorScheme(
        seedColor = themeColor,
        isDark = darkTheme,
        specVersion = ColorSpec.SpecVersion.SPEC_2025,
        style = PaletteStyle.Vibrant,
    )

    // Apply pureBlack modification if needed, similar to original logic
    val colorScheme = remember(baseColorScheme, pureBlack, darkTheme, aeroEnabled) {
        when {
            aeroEnabled && darkTheme && pureBlack -> baseColorScheme.pureBlack(true).copy(
                primary = AeroTokens.Aqua,
                secondary = AeroTokens.Grass,
                tertiary = AeroTokens.Sky,
            )
            aeroEnabled && darkTheme -> baseColorScheme.copy(
                primary = AeroTokens.Aqua,
                onPrimary = AeroTokens.DarkOcean,
                secondary = AeroTokens.Grass,
                tertiary = AeroTokens.Sky,
                background = AeroTokens.DarkOcean,
                surface = AeroTokens.DarkGlass,
                surfaceContainerLowest = Color(0xCC0B2F40),
                surfaceContainerLow = Color(0xD9103545),
                surfaceContainer = AeroTokens.DarkGlass,
                surfaceContainerHigh = Color(0xE31A4658),
                surfaceContainerHighest = Color(0xEE205366),
            )
            aeroEnabled -> baseColorScheme.copy(
                primary = Color(0xFF087F9C),
                onPrimary = Color.White,
                secondary = Color(0xFF388B51),
                tertiary = Color(0xFF168FB1),
                background = AeroTokens.LightSky,
                surface = AeroTokens.LightGlass,
                surfaceContainerLowest = Color(0xEFFFFFFF),
                surfaceContainerLow = Color(0xEAF7FDFF),
                surfaceContainer = AeroTokens.LightGlass,
                surfaceContainerHigh = Color(0xEAF0FBFF),
                surfaceContainerHighest = Color(0xE5E4F7FC),
            )
            darkTheme && pureBlack -> baseColorScheme.pureBlack(true)
            darkTheme -> baseColorScheme.copy(
                background = Color(0xFF071D2A),
                surface = Color(0xFF0B2938),
                surfaceContainerLowest = Color(0xFF061722),
                surfaceContainerLow = Color(0xFF0C2A39),
                surfaceContainer = Color(0xFF103447),
                surfaceContainerHigh = Color(0xFF164156),
                surfaceContainerHighest = Color(0xFF1D4D62),
            )
            else -> baseColorScheme.copy(
                background = Color(0xFFE7F7FB),
                surface = Color(0xFFF5FCFF),
                surfaceContainerLowest = Color.White,
                surfaceContainerLow = Color(0xFFF1FBFE),
                surfaceContainer = Color(0xFFDDF3F8),
                surfaceContainerHigh = Color(0xFFCAEAF2),
                surfaceContainerHighest = Color(0xFFB5DFEA),
            )
        }
    }

    // Use standard MaterialTheme instead of MaterialExpressiveTheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}

fun Bitmap.extractThemeColor(): Color = Color(
    Palette.from(this)
        .maximumColorCount(8)
        .generate()
        .rankedColors(1, DefaultThemeColor.toArgb())
        .first()
)

internal fun Palette.rankedColors(
    desiredColorCount: Int,
    fallbackColor: Int,
): List<Int> = Score.score(
    swatches.associate { it.rgb to it.population },
    desiredColorCount,
    fallbackColor,
    true,
)

fun ColorScheme.pureBlack(apply: Boolean) =
    if (apply) copy(
        surface = Color.Black,
        background = Color.Black
    ) else this

val ColorSaver = object : Saver<Color, Int> {
    override fun restore(value: Int): Color = Color(value)
    override fun SaverScope.save(value: Color): Int = value.toArgb()
}
