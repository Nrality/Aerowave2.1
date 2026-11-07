package com.metrolist.music.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.metrolist.music.R
import kotlinx.coroutines.launch

enum class NavStyle { TopRibbon, BottomBar }

// A single switch restores the current bottom navigation and adaptive rail.
val ActiveNavStyle = NavStyle.TopRibbon

private data class RibbonDestination(val route: String, val titleRes: Int)

private val destinations = listOf(
    RibbonDestination("home", R.string.home),
    RibbonDestination("new_release", R.string.explore),
    RibbonDestination("library", R.string.filter_library),
)

@Composable
fun CategoryRibbon(
    selectedRoute: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedPage = destinations.indexOfFirst { it.route == selectedRoute }.coerceAtLeast(0)
    val pagerState = rememberPagerState(initialPage = selectedPage) { destinations.size }
    val scope = rememberCoroutineScope()

    LaunchedEffect(selectedPage) {
        if (pagerState.currentPage != selectedPage) pagerState.animateScrollToPage(selectedPage)
    }
    LaunchedEffect(pagerState.currentPage) {
        val destination = destinations[pagerState.currentPage]
        if (destination.route != selectedRoute) onSelect(destination.route)
    }

    BoxWithConstraints(modifier.fillMaxWidth().height(48.dp), contentAlignment = Alignment.Center) {
        val pageWidth = ((maxWidth - 40.dp) / destinations.size).coerceAtLeast(76.dp)
        val outerPadding = ((maxWidth - (pageWidth * destinations.size) - 8.dp) / 2).coerceAtLeast(8.dp)
        HorizontalPager(
            state = pagerState,
            pageSize = PageSize.Fixed(pageWidth),
            contentPadding = PaddingValues(horizontal = outerPadding),
            pageSpacing = 4.dp,
            modifier = Modifier.fillMaxWidth(),
        ) { page ->
            val destination = destinations[page]
            val selected = page == pagerState.currentPage
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clickable {
                        if (page == pagerState.currentPage) {
                            onSelect(destination.route)
                        } else {
                            scope.launch { pagerState.animateScrollToPage(page) }
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(destination.titleRes),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        }
    }
}
