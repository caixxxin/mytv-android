package top.yogiczy.mytv.ui.screens.leanback.classicpanel

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import top.yogiczy.mytv.data.entities.EpgList
import top.yogiczy.mytv.data.entities.Iptv
import top.yogiczy.mytv.data.entities.IptvGroup
import top.yogiczy.mytv.data.entities.IptvGroupList
import top.yogiczy.mytv.data.entities.IptvGroupList.Companion.iptvGroupIdx
import top.yogiczy.mytv.data.entities.IptvGroupList.Companion.iptvList
import top.yogiczy.mytv.data.entities.IptvList
import top.yogiczy.mytv.data.utils.Constants
import top.yogiczy.mytv.ui.rememberLeanbackChildPadding
import top.yogiczy.mytv.ui.screens.leanback.classicpanel.components.LeanbackClassicPanelEpgList
import top.yogiczy.mytv.ui.screens.leanback.classicpanel.components.LeanbackClassicPanelIptvGroupList
import top.yogiczy.mytv.ui.screens.leanback.classicpanel.components.LeanbackClassicPanelIptvList
import top.yogiczy.mytv.ui.screens.leanback.panel.PanelAutoCloseState
import top.yogiczy.mytv.ui.screens.leanback.panel.rememberPanelAutoCloseState
import top.yogiczy.mytv.ui.screens.leanback.toast.LeanbackToastState
import top.yogiczy.mytv.ui.theme.LeanbackTheme
import top.yogiczy.mytv.ui.utils.handleLeanbackKeyEvents
import kotlin.math.max

@Composable
fun LeanbackClassicPanelScreen(
    modifier: Modifier = Modifier,
    iptvGroupList: IptvGroupList = IptvGroupList(),
    epgList: EpgList = EpgList(),
    currentIptvProvider: () -> Iptv = { Iptv() },
    showProgrammeProgressProvider: () -> Boolean = { false },
    iptvFavoriteListProvider: () -> ImmutableList<String> = { persistentListOf() },
    iptvFavoriteListVisibleProvider: () -> Boolean = { false },
    onIptvFavoriteListVisibleChange: (Boolean) -> Unit = {},
    onIptvSelected: (Iptv) -> Unit = {},
    onIptvFavoriteToggle: (Iptv) -> Unit = {},
    onClose: () -> Unit = {},
    autoCloseState: PanelAutoCloseState = rememberPanelAutoCloseState(
        timeout = Constants.UI_PANEL_SCREEN_AUTO_CLOSE_DELAY,
        onTimeout = onClose,
    ),
) {
    val childPadding = rememberLeanbackChildPadding()

    LaunchedEffect(Unit) {
        autoCloseState.active()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures(onTap = { onClose() }) },
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .background(
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                    shape = MaterialTheme.shapes.medium.copy(
                        topStart = CornerSize(0.0.dp),
                        bottomStart = CornerSize(0.0.dp),
                    ),
                )
                .padding(
                    top = childPadding.top,
                    start = childPadding.start / 2,
                    end = childPadding.end,
                ),
        ) {
            var favoriteListVisible by remember { mutableStateOf(iptvFavoriteListVisibleProvider()) }

            if (favoriteListVisible)
                LeanbackClassicPanelFavoriteIptv(
                    iptvListProvider = {
                        IptvList(iptvGroupList.iptvList
                            .filter { iptvFavoriteListProvider().contains(it.channelName) })
                    },
                    epgList = epgList,
                    currentIptvProvider = currentIptvProvider,
                    showProgrammeProgressProvider = showProgrammeProgressProvider,
                    onIptvSelected = onIptvSelected,
                    onIptvFavoriteToggle = onIptvFavoriteToggle,
                    onClose = {
                        favoriteListVisible = false
                        onIptvFavoriteListVisibleChange(false)
                    },
                    onUserAction = { autoCloseState.active() },
                )
            else
                LeanbackClassicPanelIptvGroup(
                    iptvGroupList = iptvGroupList,
                    epgList = epgList,
                    currentIptvProvider = currentIptvProvider,
                    showProgrammeProgressProvider = showProgrammeProgressProvider,
                    onIptvSelected = onIptvSelected,
                    onIptvFavoriteToggle = onIptvFavoriteToggle,
                    onToFavorite = {
                        val favoriteList = iptvGroupList.iptvList
                            .filter { iptvFavoriteListProvider().contains(it.channelName) }

                        if (favoriteList.isNotEmpty()) {
                            favoriteListVisible = true
                            onIptvFavoriteListVisibleChange(true)
                        } else {
                            LeanbackToastState.I.showToast("没有收藏的频道")
                        }
                    },
                    onUserAction = { autoCloseState.active() },
                )
        }
    }
}

@Composable
private fun LeanbackClassicPanelIptvGroup(
    modifier: Modifier = Modifier,
    iptvGroupList: IptvGroupList = IptvGroupList(),
    epgList: EpgList = EpgList(),
    currentIptvProvider: () -> Iptv = { Iptv() },
    showProgrammeProgressProvider: () -> Boolean = { false },
    onIptvSelected: (Iptv) -> Unit = {},
    onIptvFavoriteToggle: (Iptv) -> Unit = {},
    onToFavorite: () -> Unit = {},
    onUserAction: () -> Unit = {},
) {
    var focusedIptvGroup by remember {
        mutableStateOf(
            iptvGroupList[max(0, iptvGroupList.iptvGroupIdx(currentIptvProvider()))]
        )
    }

    var focusedIptv by remember { mutableStateOf(currentIptvProvider()) }
    var focusedIptvFocusRequester by remember { mutableStateOf(FocusRequester.Default) }

    var inIptvGroupTab by remember { mutableStateOf(true) }

    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { onToFavorite() })
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            "向左查看收藏列表".map {
                Text(text = it.toString(), style = MaterialTheme.typography.labelSmall)
            }
        }

        LeanbackClassicPanelIptvGroupList(
            modifier = Modifier.handleLeanbackKeyEvents(
                onLeft = onToFavorite,
            ),
            iptvGroupListProvider = { iptvGroupList },
            initialIptvGroupProvider = {
                iptvGroupList.find { it.iptvList.contains(currentIptvProvider()) }
                    ?: IptvGroup()
            },
            onIptvGroupFocused = { focusedIptvGroup = it },
            exitFocusRequesterProvider = { focusedIptvFocusRequester },
            onFocusEnter = { inIptvGroupTab = true },
            onFocusExit = { inIptvGroupTab = false },
            onUserAction = onUserAction,
        )

        LeanbackClassicPanelIptvList(
            iptvListProvider = { focusedIptvGroup.iptvList },
            epgListProvider = { epgList },
            initialIptvProvider = currentIptvProvider,
            onIptvSelected = onIptvSelected,
            onIptvFavoriteToggle = onIptvFavoriteToggle,
            onIptvFocused = { iptv, focusRequester ->
                focusedIptv = iptv
                focusedIptvFocusRequester = focusRequester
            },
            showProgrammeProgressProvider = showProgrammeProgressProvider,
            onUserAction = onUserAction,
        )

        LeanbackClassicPanelEpgList(
            epgProvider = {
                if (inIptvGroupTab) null
                else epgList.firstOrNull { it.channel == focusedIptv.channelName }
            },
            exitFocusRequesterProvider = { focusedIptvFocusRequester },
            onUserAction = onUserAction,
        )
    }
}

@Composable
fun LeanbackClassicPanelFavoriteIptv(
    modifier: Modifier = Modifier,
    iptvListProvider: () -> IptvList = { IptvList() },
    epgList: EpgList = EpgList(),
    currentIptvProvider: () -> Iptv = { Iptv() },
    showProgrammeProgressProvider: () -> Boolean = { false },
    onIptvSelected: (Iptv) -> Unit = {},
    onIptvFavoriteToggle: (Iptv) -> Unit = {},
    onClose: () -> Unit = {},
    onUserAction: () -> Unit = {},
) {
    var key by remember { mutableIntStateOf(0) }
    val iptvList = remember(key) { iptvListProvider() }
    var focusedIptv by remember { mutableStateOf(currentIptvProvider()) }
    var focusedIptvFocusRequester by remember { mutableStateOf(FocusRequester.Default) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(iptvList) {
        if (iptvList.isEmpty()) onClose()
    }

    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { onClose() })
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            "向左查看全部列表".map {
                Text(text = it.toString(), style = MaterialTheme.typography.labelSmall)
            }
        }

        LeanbackClassicPanelIptvList(
            modifier = Modifier.handleLeanbackKeyEvents(
                onLeft = onClose,
            ),
            iptvListProvider = { iptvList },
            epgListProvider = { epgList },
            initialIptvProvider = {
                val currentIptv = currentIptvProvider()
                if (iptvList.contains(currentIptv)) currentIptv else iptvList.first()
            },
            onIptvSelected = onIptvSelected,
            onIptvFavoriteToggle = {
                if (iptvList.size == 1) {
                    onIptvFavoriteToggle(it)
                    onClose()
                } else {
                    if (iptvList.indexOf(it) < iptvList.size - 1) {
                        focusManager.moveFocus(FocusDirection.Down)
                    } else {
                        focusManager.moveFocus(FocusDirection.Up)
                    }
                    key++
                    onIptvFavoriteToggle(it)
                }
            },
            onIptvFocused = { iptv, focusRequester ->
                focusedIptv = iptv
                focusedIptvFocusRequester = focusRequester
            },
            showProgrammeProgressProvider = showProgrammeProgressProvider,
            onUserAction = onUserAction,
        )

        LeanbackClassicPanelEpgList(
            epgProvider = { epgList.firstOrNull { it.channel == focusedIptv.channelName } },
            exitFocusRequesterProvider = { focusedIptvFocusRequester },
            onUserAction = onUserAction,
        )
    }
}

@Preview
@Composable
private fun LeanbackClassicPanelScreenPreview() {
    LeanbackTheme {
        LeanbackClassicPanelScreen(
            iptvGroupList = IptvGroupList.EXAMPLE,
        )
    }
}