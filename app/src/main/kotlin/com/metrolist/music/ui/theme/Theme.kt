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

val DefaultThemeColor = Color(0xFF7BFF18)

@Composable
fun MetrolistTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    pureBlack: Boolean = false,
    themeColor: Color = DefaultThemeColor,
    aeroEnabled: Boolean = false,
    content: @Composable () -> Unit,
) {
    val baseColorScheme = rememberDynamicColorScheme(
        seedColor = Color(0xFF72F319),
        isDark = darkTheme,
        specVersion = ColorSpec.SpecVersion.SPEC_2025,
        style = PaletteStyle.Vibrant,
    )

    // Apply pureBlack modification if needed, similar to original logic
    val colorScheme = remember(baseColorScheme, pureBlack, darkTheme, aeroEnabled) {
        when {
            darkTheme && pureBlack -> baseColorScheme.pureBlack(true).copy(primary = Color(0xFF9BFF36), secondary = Color(0xFF5AE620))
            darkTheme -> baseColorScheme.copy(
                primary = Color(0xFF9BFF36), onPrimary = Color(0xFF142400), secondary = Color(0xFF63E62E), tertiary = Color(0xFFB6F66D),
                background = Color(0xFF030A04), surface = Color(0xFF071208),
                surfaceContainerLowest = Color(0xFF030A04), surfaceContainerLow = Color(0xFF09170A),
                surfaceContainer = Color(0xFF0D1E0D), surfaceContainerHigh = Color(0xFF152914),
                surfaceContainerHighest = Color(0xFF20391B),
            )
            else -> baseColorScheme.copy(
                primary = Color(0xFF397E12), secondary = Color(0xFF3F7F22),
                background = Color(0xFFE7F0DF), surface = Color(0xFFF2F7EC),
                surfaceContainerLowest = Color.White, surfaceContainerLow = Color(0xFFEEF5E8),
                surfaceContainer = Color(0xFFE4EEDB), surfaceContainerHigh = Color(0xFFD8E6CC),
                surfaceContainerHighest = Color(0xFFCCDDBE),
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
