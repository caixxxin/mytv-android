package top.yogiczy.mytv.ui.screens.toast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import top.yogiczy.mytv.ui.rememberChildPadding
import top.yogiczy.mytv.ui.screens.toast.ToastProperty.Companion.toMs
import top.yogiczy.mytv.ui.theme.MyTVTheme

@Composable
fun ToastScreen(
    modifier: Modifier = Modifier,
    state: ToastState = rememberToastState(),
) {
    Box(modifier = modifier.fillMaxSize()) {
        Popup {
            AnimatedVisibility(
                visible = state.visible,
                // TODO * 1.2 暂时防止在手机上未完全隐藏
                enter = EnterTransition.None,
                exit = ExitTransition.None,
            ) {
                ToastItem(property = state.current)
            }
        }
    }
}

@Composable
fun ToastItem(
    modifier: Modifier = Modifier,
    property: ToastProperty = ToastProperty(),
) {
    val childPadding = rememberChildPadding()

    Box(
        modifier = modifier
            .padding(start = childPadding.start, top = childPadding.top)
            .background(
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                shape = MaterialTheme.shapes.small,
            )
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text = property.message,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Preview
@Composable
private fun ToastScreenPreview() {
    MyTVTheme {
        Box(
            modifier = Modifier
                .background(Color.White)
                .padding(bottom = 20.dp, end = 20.dp),
        ) {
            ToastItem(
                property = ToastProperty(message = "新版本: v1.2.2")
            )
        }
    }
}

class ToastState {
    private var _visible by mutableStateOf(false)
    val visible get() = _visible

    private var _current by mutableStateOf(ToastProperty())
    val current get() = _current

    private fun showToast(toast: ToastProperty) {
        // TODO 消息变化较生硬
        _current = toast
        _visible = true
        channel.trySend(toast.duration.toMs())
    }

    fun showToast(
        message: String,
        duration: ToastProperty.Duration = ToastProperty.Duration.Default,
    ) {
        showToast(ToastProperty(message = message, duration = duration))
    }

    private val channel = Channel<Int>(Channel.CONFLATED)

    @OptIn(FlowPreview::class)
    suspend fun observe() {
        channel.consumeAsFlow().debounce { it.toLong() }.collect { _visible = false }
    }

    companion object {
        // TODO 这种方法可能违反了 Compose 的规则
        lateinit var I: ToastState
    }
}

@Composable
fun rememberToastState() = remember { ToastState() }.also {
    ToastState.I = it
    LaunchedEffect(it) { it.observe() }
}

data class ToastProperty(
    val message: String = "",
    val duration: Duration = Duration.Default,
) {
    sealed interface Duration {
        data object Default : Duration
        data class Custom(val duration: Int) : Duration
    }

    companion object {
        fun Duration.toMs(): Int = when (val it = this) {
            is Duration.Default -> 2300
            is Duration.Custom -> it.duration
        }
    }
}