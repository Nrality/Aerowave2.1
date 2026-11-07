/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.metrolist.music.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush

@Composable
fun AeroBackground(
    dark: Boolean,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val base = if (dark) AeroTokens.DarkOcean else AeroTokens.LightSky
        val lower = if (dark) AeroTokens.DarkOcean else AeroTokens.LightMint
        drawRect(Brush.verticalGradient(listOf(base, lower), endY = height))

        val blueGlow = if (dark) AeroTokens.Sky.copy(alpha = 0.22f) else AeroTokens.Aqua.copy(alpha = 0.48f)
        val greenGlow = if (dark) AeroTokens.Grass.copy(alpha = 0.15f) else AeroTokens.Grass.copy(alpha = 0.28f)
        drawCircle(
            brush = Brush.radialGradient(listOf(blueGlow, blueGlow.copy(alpha = 0f)), center = Offset(width * 0.08f, height * 0.12f), radius = width * 0.8f),
            radius = width * 0.8f,
            center = Offset(width * 0.08f, height * 0.12f),
        )
        drawCircle(
            brush = Brush.radialGradient(listOf(greenGlow, greenGlow.copy(alpha = 0f)), center = Offset(width * 0.94f, height * 0.67f), radius = width * 0.65f),
            radius = width * 0.65f,
            center = Offset(width * 0.94f, height * 0.67f),
        )

        val bubbleColor = if (dark) AeroTokens.Aqua.copy(alpha = 0.07f) else androidx.compose.ui.graphics.Color.White.copy(alpha = 0.26f)
        drawCircle(bubbleColor, radius = width * 0.16f, center = Offset(width * 0.88f, height * 0.22f))
        drawCircle(bubbleColor.copy(alpha = bubbleColor.alpha * 0.7f), radius = width * 0.075f, center = Offset(width * 0.72f, height * 0.31f))
        drawCircle(bubbleColor.copy(alpha = bubbleColor.alpha * 0.65f), radius = width * 0.11f, center = Offset(width * 0.12f, height * 0.78f))
    }
}
