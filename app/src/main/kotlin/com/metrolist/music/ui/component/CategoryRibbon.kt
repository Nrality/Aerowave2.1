package com.metrolist.music.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.metrolist.music.R
import androidx.compose.ui.res.stringResource

enum class NavStyle { TopRibbon, BottomBar }

val ActiveNavStyle = NavStyle.TopRibbon

private data class Destination(val route: String, val title: Int)

private val destinations = listOf(
    Destination("home", R.string.home),
    Destination("new_release", R.string.explore),
    Destination("library", R.string.filter_library),
)

@Composable
fun CategoryRibbon(
    selectedRoute: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Box(
            modifier = Modifier.size(82.dp).clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color(0xFFB5FF49), Color(0xFF31880E), Color(0xFF0A2108))))
                .border(2.dp, Color(0xFF9BFF36).copy(alpha = 0.86f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text("X", color = Color(0xFFE1FFB0), fontSize = 42.sp, fontWeight = FontWeight.Black)
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            destinations.forEach { destination ->
                val selected = destination.route == selectedRoute
                val glow by animateColorAsState(
                    if (selected) Color(0xFF9BFF36) else Color(0xFF31552A),
                    label = "dashboard selection",
                )
                Box(
                    modifier = Modifier.fillMaxWidth().height(34.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(if (selected) Brush.horizontalGradient(listOf(Color(0xFF8AF02B), Color(0xFF315F13))) else Brush.horizontalGradient(listOf(Color(0x66203A19), Color(0x33203A19))))
                        .border(1.dp, glow.copy(alpha = if (selected) 0.95f else 0.45f), RoundedCornerShape(5.dp))
                        .clickable { onSelect(destination.route) }
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        stringResource(destination.title).uppercase(),
                        color = if (selected) Color(0xFF071407) else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        letterSpacing = 1.4.sp,
                        fontWeight = if (selected) FontWeight.Black else FontWeight.Medium,
                    )
                }
            }
        }
    }
}
