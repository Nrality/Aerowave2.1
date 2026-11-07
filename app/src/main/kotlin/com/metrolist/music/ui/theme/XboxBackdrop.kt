package com.metrolist.music.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun XboxBackdrop(modifier: Modifier = Modifier) {
    Canvas(modifier.fillMaxSize()) {
        val lime = Color(0xFF63E62E)
        drawRect(Brush.verticalGradient(listOf(Color(0xFF071407), Color(0xFF020702)), endY = size.height))
        val spacing = 30.dp.toPx()
        var x = 0f
        while (x < size.width + size.height) {
            drawLine(lime.copy(alpha = 0.10f), Offset(x, 0f), Offset(x - size.height * 0.45f, size.height), strokeWidth = 0.7.dp.toPx())
            x += spacing
        }
        var y = 0f
        while (y < size.height) {
            drawLine(lime.copy(alpha = 0.075f), Offset(0f, y), Offset(size.width, y), strokeWidth = 0.6.dp.toPx())
            y += spacing
        }
        drawCircle(Brush.radialGradient(listOf(lime.copy(alpha = 0.20f), Color.Transparent), center = Offset(size.width * 0.18f, size.height * 0.32f), radius = size.width * 0.55f), radius = size.width * 0.55f, center = Offset(size.width * 0.18f, size.height * 0.32f))
        drawCircle(lime.copy(alpha = 0.35f), radius = size.width * 0.16f, center = Offset(size.width * 0.18f, size.height * 0.32f), style = Stroke(width = 2.dp.toPx()))
    }
}
