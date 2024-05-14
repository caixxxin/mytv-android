package top.yogiczy.mytv.ui.screens.panel.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyListState
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import top.yogiczy.mytv.data.entities.Epg
import top.yogiczy.mytv.data.entities.Epg.Companion.currentProgrammes
import top.yogiczy.mytv.data.entities.EpgProgramme
import top.yogiczy.mytv.data.entities.EpgProgramme.Companion.isLive
import top.yogiczy.mytv.data.entities.EpgProgramme.Companion.progress
import top.yogiczy.mytv.data.entities.EpgProgrammeList
import top.yogiczy.mytv.data.entities.Iptv
import top.yogiczy.mytv.tvmaterial.StandardDialog
import top.yogiczy.mytv.ui.theme.MyTVTheme
import top.yogiczy.mytv.ui.utils.focusOnInit
import top.yogiczy.mytv.ui.utils.focusOnInitSaveable
import top.yogiczy.mytv.ui.utils.handleDPadKeyEvents
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.max

@Composable
fun PanelIptvItem(
    modifier: Modifier = Modifier,
    iptv: Iptv = Iptv.EMPTY,
    onIptvSelected: () -> Unit = {},
    epg: Epg? = null,
    currentProgramme: EpgProgramme? = epg?.currentProgrammes()?.now,
    onIptvFavoriteToggle: () -> Unit = {},
    showProgrammeProgress: Boolean = false,
    onShowEpg: () -> Unit = {},
    initialFocused: Boolean = false,
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Card(
        onClick = { },
        modifier = modifier
            .width(130.dp)
            .height(54.dp)
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused || it.hasFocus }
            .handleDPadKeyEvents(
                onSelect = {
                    focusRequester.requestFocus()
                    onIptvSelected()
                },
                onLongSelect = {
                    focusRequester.requestFocus()
                    onIptvSelected()
                    // onIptvFavoriteToggle()
                },
                onSettings = {
                    focusRequester.requestFocus()
                    if (epg != null) onShowEpg()
                },
            )
            .focusOnInitSaveable(initialFocused),
        colors = CardDefaults.colors(
            containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
            contentColor = MaterialTheme.colorScheme.onBackground,
            focusedContainerColor = MaterialTheme.colorScheme.onBackground,
            focusedContentColor = MaterialTheme.colorScheme.background,
            pressedContentColor = MaterialTheme.colorScheme.background,
        ),
    ) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = iptv.name,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                )

                Text(
                    text = currentProgramme?.title ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    modifier = Modifier.alpha(0.8f),
                )

            }

            // 节目进度条
            if (showProgrammeProgress && currentProgramme != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth(currentProgramme.progress())
                        .height(3.dp)
                        .background(
                            if (isFocused) MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                        ),
                )
            }
        }
    }
}

@Preview
@Composable
private fun PanelIptvItemPreview() {
    MyTVTheme {
        Column(
            modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PanelIptvItem(
                iptv = Iptv.EXAMPLE,
                currentProgramme = EpgProgramme(
                    startAt = System.currentTimeMillis() - 100000,
                    endAt = System.currentTimeMillis() + 200000,
                    title = "新闻联播",
                ),
                showProgrammeProgress = true,
            )

            PanelIptvItem(
                iptv = Iptv.EXAMPLE,
                currentProgramme = EpgProgramme(
                    startAt = System.currentTimeMillis() - 100000,
                    endAt = System.currentTimeMillis() + 200000,
                    title = "新闻联播",
                ),
                showProgrammeProgress = true,
                initialFocused = true,
            )
        }
    }
}

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class,
    ExperimentalTvMaterial3Api::class
)
@Composable
fun PanelIptvItemEpgDialog(
    modifier: Modifier = Modifier,
    showDialog: Boolean = false,
    onDismissRequest: () -> Unit = {},
    iptv: Iptv = Iptv.EMPTY,
    epg: Epg = Epg.EMPTY,
) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    StandardDialog(
        showDialog = showDialog,
        onDismissRequest = onDismissRequest,
        title = { Text(text = iptv.channelName) },
        confirmButton = { Text(text = "左右切换频道") },
    ) {
        val listState = remember(iptv) {
            TvLazyListState(
                max(0, epg.programmes.indexOfFirst { it.isLive() })
            )
        }

        TvLazyColumn(
            modifier = modifier,
            state = listState,
            contentPadding = PaddingValues(vertical = 4.dp),
        ) {
            if (epg.programmes.isNotEmpty()) {
                items(epg.programmes, key = { it.hashCode() }) { programme ->
                    ListItem(
                        modifier = Modifier.focusOnInit(programme.isLive()),
                        selected = programme.isLive(),
                        onClick = { },
                        headlineContent = {
                            Text(
                                text = programme.title,
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 1,
                            )
                        },
                        overlineContent = {
                            Text(
                                text = timeFormat.format(programme.startAt) + " ~ " + timeFormat.format(
                                    programme.endAt
                                )
                            )
                        },
                        trailingContent = {
                            if (programme.isLive()) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "playing")
                            }
                        },
                    )
                }
            } else {
                item {
                    ListItem(
                        modifier = Modifier.focusOnInit(true),
                        selected = false,
                        onClick = { },
                        headlineContent = {
                            Text(
                                text = "当前频道暂无节目",
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 1,
                            )
                        },
                    )
                }
            }
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun PanelIptvItemEpgDialogPreview() {
    MyTVTheme {
        PanelIptvItemEpgDialog(
            showDialog = true, epg = Epg(
                "CCTV1",
                EpgProgrammeList(
                    listOf(
                        EpgProgramme(
                            startAt = System.currentTimeMillis() - 2000000,
                            endAt = System.currentTimeMillis() - 1000000,
                            title = "新闻联播"
                        ),
                        EpgProgramme(
                            startAt = System.currentTimeMillis() - 1000000,
                            endAt = System.currentTimeMillis() + 1000000,
                            title = "新闻联播1"
                        ),
                        EpgProgramme(
                            startAt = System.currentTimeMillis() + 1000000,
                            endAt = System.currentTimeMillis() + 2000000,
                            title = "新闻联播2"
                        ),
                    )
                ),
            )
        )
    }
}
