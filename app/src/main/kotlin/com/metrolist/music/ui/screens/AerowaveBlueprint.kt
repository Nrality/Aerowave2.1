package com.metrolist.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.metrolist.innertube.models.SongItem
import com.metrolist.music.LocalNavController
import com.metrolist.music.LocalPlayerConnection
import com.metrolist.music.R
import com.metrolist.music.constants.AutoRadioQueueKey
import com.metrolist.music.db.entities.Song
import com.metrolist.music.extensions.toMediaItem
import com.metrolist.music.models.toMediaMetadata
import com.metrolist.music.playback.queues.ListQueue
import com.metrolist.music.playback.queues.YouTubeQueue
import com.metrolist.music.ui.theme.AeroBackground
import com.metrolist.music.utils.SearchRoutes
import com.metrolist.music.utils.rememberPreference
import com.metrolist.music.viewmodels.HomeViewModel

private val Ink = Color(0xFF123D5D)
private val MutedInk = Color(0xFF47728B)
private val Glass = Color(0xCCFFFFFF)
private val TileGradients = listOf(
    listOf(Color(0xFFFFA05A), Color(0xFFFF5F86)),
    listOf(Color(0xFFFFDA52), Color(0xFFFF9F3C)),
    listOf(Color(0xFFC87BFF), Color(0xFF6868F8)),
    listOf(Color(0xFF6BE5A1), Color(0xFF18B89E)),
    listOf(Color(0xFF57CDF5), Color(0xFF557BFF)),
)

@Composable
private fun BlueprintPage(title: String, onSettings: (() -> Unit)? = null, onBack: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    Box(Modifier.fillMaxSize()) {
        AeroBackground(dark = false)
        Column(
            Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 20.dp),
        ) {
            Row(
                Modifier.fillMaxWidth().height(62.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (onBack != null) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(46.dp).clip(CircleShape).background(Glass),
                    ) { Icon(painterResource(R.drawable.arrow_back), "Back", tint = Ink) }
                }
                Text(title, color = Ink, fontSize = 29.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                if (onSettings != null) {
                    IconButton(
                        onClick = onSettings,
                        modifier = Modifier.size(48.dp).clip(CircleShape).background(Glass).border(1.dp, Color.White, CircleShape),
                    ) { Icon(painterResource(R.drawable.settings), "Settings", tint = Ink) }
                }
            }
            Column(
                Modifier.fillMaxSize().weight(1f).padding(bottom = 6.dp),
                content = content,
            )
        }
    }
}

@Composable
private fun GlassCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier.clip(RoundedCornerShape(24.dp)).background(Glass).border(1.dp, Color.White.copy(alpha = .92f), RoundedCornerShape(24.dp)),
        content = content,
    )
}

@Composable
private fun SectionLabel(label: String, modifier: Modifier = Modifier) {
    Text(label, color = Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = modifier.padding(top = 16.dp, bottom = 8.dp))
}

@Composable
private fun Artwork(url: String?, index: Int, modifier: Modifier = Modifier) {
    val colors = TileGradients[index.mod(TileGradients.size)]
    Box(modifier.clip(RoundedCornerShape(18.dp)).background(Brush.linearGradient(colors))) {
        if (!url.isNullOrBlank()) {
            AsyncImage(model = url, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun BlueprintSongRow(song: Song, index: Int, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(bottom = 9.dp).clip(RoundedCornerShape(22.dp)).background(Glass)
            .border(1.dp, Color.White, RoundedCornerShape(22.dp)).clickable(onClick = onClick).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Artwork(song.thumbnailUrl, index, Modifier.size(46.dp))
        Column(Modifier.weight(1f).padding(start = 12.dp)) {
            Text(song.title, color = Ink, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
            Text(song.artists.joinToString { it.name }, color = MutedInk, fontSize = 12.sp, maxLines = 1)
        }
        Icon(painterResource(R.drawable.play), contentDescription = "Play ${song.title}", tint = Color(0xFF16892B), modifier = Modifier.size(22.dp))
    }
}

@Composable
fun AerowaveBlueprintHome(viewModel: HomeViewModel = hiltViewModel()) {
    val navController = LocalNavController.current
    val player = LocalPlayerConnection.current ?: return
    val quickPicks by viewModel.quickPicks.collectAsStateWithLifecycle()
    val homeItems by viewModel.allLocalItems.collectAsStateWithLifecycle()
    val songs = (quickPicks.orEmpty() + homeItems.filterIsInstance<Song>()).distinctBy { it.id }.take(12)
    val (autoRadio) = rememberPreference(AutoRadioQueueKey, true)
    LaunchedEffect(Unit) { viewModel.loadHomeData() }

    BlueprintPage("Aerowave", onSettings = { navController.navigate("settings") }) {
        LazyColumn(contentPadding = PaddingValues(bottom = 104.dp), verticalArrangement = Arrangement.spacedBy(0.dp)) {
            item {
                GlassCard(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Artwork(songs.firstOrNull()?.thumbnailUrl, 4, Modifier.size(82.dp))
                        Column(Modifier.weight(1f).padding(horizontal = 14.dp)) {
                            Text("Daily Wave", color = Ink, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text("A mix made from your week", color = MutedInk, fontSize = 14.sp)
                        }
                        PlayOrb {
                            if (songs.isNotEmpty()) player.playQueue(ListQueue("Daily Wave", songs.map { it.toMediaItem() }))
                            else navController.navigate("search_input")
                        }
                    }
                }
            }
            item {
                SectionLabel("Quick picks")
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    songs.take(5).forEachIndexed { index, song ->
                        Column(Modifier.width(112.dp).clickable {
                            if (autoRadio) player.playQueue(YouTubeQueue.radio(song.toMediaMetadata()))
                            else player.playQueue(ListQueue(song.title, listOf(song.toMediaItem())))
                        }) {
                            Artwork(song.thumbnailUrl, index, Modifier.fillMaxWidth().height(100.dp))
                            Text(song.title, color = Ink, fontSize = 13.sp, maxLines = 1, modifier = Modifier.padding(top = 5.dp))
                            Text(song.artists.firstOrNull()?.name.orEmpty(), color = MutedInk, fontSize = 11.sp, maxLines = 1)
                        }
                    }
                    if (songs.isEmpty()) Text("Search for music to build your picks", color = MutedInk, modifier = Modifier.padding(12.dp))
                }
            }
            item {
                SectionLabel("Your playlists")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf("Chill Vibes", "Road Trip", "Focus").forEachIndexed { index, name ->
                        Column(Modifier.weight(1f).clickable { navController.navigate("library") }) {
                            Artwork(null, index + 3, Modifier.fillMaxWidth().height(88.dp))
                            Text(name, color = Ink, fontSize = 13.sp, maxLines = 1, modifier = Modifier.padding(top = 5.dp))
                            Text("Open library", color = MutedInk, fontSize = 11.sp)
                        }
                    }
                }
            }
            item { SectionLabel("Popular songs") }
            items(songs.take(6).size) { index ->
                val song = songs[index]
                BlueprintSongRow(song, index) {
                    if (autoRadio) player.playQueue(YouTubeQueue.radio(song.toMediaMetadata()))
                    else player.playQueue(ListQueue(song.title, listOf(song.toMediaItem())))
                }
            }
        }
    }
}

@Composable
fun AerowaveBlueprintSearch() {
    val navController = LocalNavController.current
    var query by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf("Songs") }
    val keyboard = LocalSoftwareKeyboardController.current
    val categories = listOf("Songs", "Albums", "Artists", "Playlists")
    fun search() { if (query.isNotBlank()) { keyboard?.hide(); navController.navigate(SearchRoutes.resultRoute(query.trim())) } }

    BlueprintPage("Search", onSettings = { navController.navigate("settings") }) {
        Column(Modifier.fillMaxSize()) {
            SectionLabel("Quick results")
            listOf("Midnight Drive", "Midnight Drive (Live)", "Midnight Drivers", "Midnight Drive Mix").forEachIndexed { index, label ->
                Row(Modifier.fillMaxWidth().padding(bottom = 8.dp).clip(RoundedCornerShape(22.dp)).background(Glass).clickable { query = label; search() }.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Artwork(null, index, Modifier.size(44.dp))
                    Column(Modifier.padding(start = 12.dp)) {
                        Text(label, color = Ink, fontWeight = FontWeight.SemiBold)
                        Text(if (index == 2) "Artist" else "Song · from your music", color = MutedInk, fontSize = 12.sp)
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categories.forEach { category ->
                    val active = category == selected
                    Text(category, color = if (active) Color(0xFF167A2B) else Ink, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clip(CircleShape).background(Glass).clickable { selected = category }.padding(horizontal = 16.dp, vertical = 10.dp))
                }
            }
            Row(Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 12.dp).clip(CircleShape).background(Glass).border(1.dp, Color.White, CircleShape).padding(start = 16.dp, end = 6.dp, top = 6.dp, bottom = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(R.drawable.search), null, tint = MutedInk, modifier = Modifier.size(22.dp))
                BasicTextField(value = query, onValueChange = { query = it }, modifier = Modifier.weight(1f).padding(horizontal = 12.dp), singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Ink), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search), keyboardActions = KeyboardActions(onSearch = { search() }),
                    decorationBox = { inner -> if (query.isEmpty()) Text("Search songs, artists, albums", color = MutedInk); inner() })
                PlayOrb(label = "→", onClick = { search() })
            }
        }
    }
}

@Composable
fun AerowaveBlueprintLibrary() {
    val navController = LocalNavController.current
    var tab by remember { mutableStateOf("Playlists") }
    BlueprintPage("Library", onSettings = { navController.navigate("settings") }) {
        LazyColumn(contentPadding = PaddingValues(bottom = 104.dp)) {
            item {
                GlassCard(Modifier.fillMaxWidth().padding(top = 8.dp).clickable { navController.navigate("wrapped") }) {
                    Column(Modifier.background(Brush.horizontalGradient(listOf(Color(0xFFFFF0C5), Color(0xFFFFCBE7), Color(0xFFD7C8FF)))).fillMaxWidth().padding(18.dp)) {
                        Text("Wave Wrapped", color = Ink, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                        Text("Your year in music", color = MutedInk, fontSize = 16.sp)
                    }
                }
            }
            item {
                Row(Modifier.horizontalScroll(rememberScrollState()).padding(vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf("Playlists", "Albums", "Artists").forEach { label ->
                        Text(label, color = if (tab == label) Color(0xFF157A2D) else Ink, modifier = Modifier.clip(CircleShape).background(Glass).clickable { tab = label }.padding(horizontal = 16.dp, vertical = 10.dp))
                    }
                }
            }
            item {
                val entries = when (tab) {
                    "Albums" -> listOf("Recently added", "Saved albums")
                    "Artists" -> listOf("Your artists", "Recently played")
                    else -> listOf("Liked songs", "Downloads", "Most played", "Recently added")
                }
                entries.chunked(2).forEachIndexed { rowIndex, pair ->
                    Row(Modifier.fillMaxWidth().padding(bottom = 10.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        pair.forEachIndexed { index, name ->
                            Column(Modifier.weight(1f).height(112.dp).clip(RoundedCornerShape(24.dp)).background(Glass).clickable {
                                when (name) {
                                    "Liked songs" -> navController.navigate("auto_playlist/liked")
                                    "Downloads" -> navController.navigate("auto_playlist/downloaded")
                    "Most played" -> navController.navigate("top_playlist/50")
                                    else -> navController.navigate("auto_playlist/recently_added")
                                }
                            }.padding(14.dp)) {
                                Artwork(null, rowIndex * 2 + index, Modifier.size(42.dp))
                                Text(name, color = Ink, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
                                Text(if (name == "Liked songs") "Your favorites" else "Open collection", color = MutedInk, fontSize = 12.sp)
                            }
                        }
                        if (pair.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
            item { SectionLabel("Your playlists") }
            item {
                listOf("Chill Vibes", "Road Trip", "Focus").forEachIndexed { index, name ->
                    Row(Modifier.fillMaxWidth().padding(bottom = 8.dp).clip(RoundedCornerShape(22.dp)).background(Glass).clickable { navController.navigate("auto_playlist/liked") }.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Artwork(null, index + 2, Modifier.size(48.dp))
                        Column(Modifier.padding(start = 12.dp)) { Text(name, color = Ink, fontWeight = FontWeight.SemiBold); Text("Open playlist", color = MutedInk, fontSize = 12.sp) }
                    }
                }
            }
        }
    }
}

@Composable
fun AerowaveBlueprintOrbit() {
    val navController = LocalNavController.current
    BlueprintPage("Orbit", onSettings = { navController.navigate("settings") }) {
        LazyColumn(contentPadding = PaddingValues(bottom = 104.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            item {
                Spacer(Modifier.height(12.dp))
                Box(Modifier.size(190.dp).clip(CircleShape).background(Color.White.copy(alpha = .24f)), contentAlignment = Alignment.Center) {
                    PlayOrb(label = "🎙", size = 138.dp, onClick = { navController.navigate("recognition?autoStart=true") })
                }
                Text("Recognize a song", color = Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
                Text("Tap to listen to the music around you", color = MutedInk, fontSize = 14.sp)
                SectionLabel("Moods", Modifier.fillMaxWidth())
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf("Chill", "Focus", "Energy", "Night").forEachIndexed { index, mood ->
                        Box(Modifier.weight(1f).aspectRatio(1f).clip(CircleShape).background(Brush.linearGradient(TileGradients[index])).clickable { navController.navigate(SearchRoutes.resultRoute("$mood music")) }, contentAlignment = Alignment.Center) {
                            Text(mood, color = Ink, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OrbitFeature("Listen Together", "Sync with a friend", 4) { navController.navigate("listen_together_from_topbar") }
                    OrbitFeature("Sleep timer", "Fade out and stop", 2) { navController.navigate("settings/player") }
                }
                Row(Modifier.fillMaxWidth().padding(top = 12.dp).clip(RoundedCornerShape(22.dp)).background(Glass).clickable { navController.navigate("wrapped") }.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Artwork(null, 1, Modifier.size(48.dp))
                    Column(Modifier.padding(start = 12.dp)) { Text("Time capsule", color = Ink, fontWeight = FontWeight.SemiBold); Text("Songs you loved a year ago", color = MutedInk, fontSize = 12.sp) }
                }
            }
        }
    }
}

@Composable
fun AerowaveBlueprintSettings() {
    val navController = LocalNavController.current
    BlueprintPage("Settings", onBack = { navController.popBackStack() }) {
        LazyColumn(contentPadding = PaddingValues(bottom = 96.dp)) {
            item {
                Row(Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 14.dp).clip(RoundedCornerShape(24.dp)).background(Glass).clickable { navController.navigate("account") }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Artwork(null, 2, Modifier.size(58.dp).clip(CircleShape))
                    Column(Modifier.weight(1f).padding(start = 14.dp)) {
                        Text("Your account", color = Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Manage your music account", color = MutedInk, fontSize = 13.sp)
                    }
                    Text("›", color = MutedInk, fontSize = 28.sp)
                }
                GlassCard(Modifier.fillMaxWidth()) {
                    val options = listOf(
                        Triple("Appearance", "Theme, colors, player style", "settings/appearance"),
                        Triple("Player and audio", "Quality, crossfade, normalization", "settings/player"),
                        Triple("Content", "Language, region, explicit filter", "settings/content"),
                        Triple("Storage and downloads", "Cache, offline songs", "settings/storage"),
                        Triple("Privacy", "History, permissions", "settings/privacy"),
                        Triple("Backup and restore", "Export or import your library", "settings/backup_restore"),
                        Triple("About", "Version, licenses, credits", "settings/about"),
                        Triple("Terms and conditions", "Legal", "settings/about"),
                    )
                    options.forEachIndexed { index, (title, subtitle, route) ->
                        Row(Modifier.fillMaxWidth().clickable { navController.navigate(route) }.padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Artwork(null, index, Modifier.size(38.dp))
                            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                                Text(title, color = Ink, fontWeight = FontWeight.SemiBold)
                                Text(subtitle, color = MutedInk, fontSize = 12.sp)
                            }
                            Text("›", color = MutedInk, fontSize = 24.sp)
                        }
                        if (index < options.lastIndex) Spacer(Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = .65f)))
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.OrbitFeature(title: String, description: String, color: Int, onClick: () -> Unit) {
    Column(Modifier.weight(1f).height(118.dp).clip(RoundedCornerShape(22.dp)).background(Glass).clickable(onClick = onClick).padding(14.dp)) {
        Artwork(null, color, Modifier.size(40.dp))
        Text(title, color = Ink, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
        Text(description, color = MutedInk, fontSize = 12.sp)
    }
}

@Composable
private fun PlayOrb(label: String = "▶", size: androidx.compose.ui.unit.Dp = 50.dp, onClick: () -> Unit) {
    Box(Modifier.size(size).clip(CircleShape).background(Brush.verticalGradient(listOf(Color(0xFF99F179), Color(0xFF19A82F)))).border(2.dp, Color.White.copy(alpha = .85f), CircleShape).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        if (label == "🎙") {
            Icon(painterResource(R.drawable.mic), contentDescription = "Recognize a song", tint = Color.White, modifier = Modifier.size(48.dp))
        } else {
            Text(label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = if (size > 80.dp) 42.sp else 20.sp)
        }
    }
}

