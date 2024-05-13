package top.yogiczy.mytv.ui.screens.video

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.Format
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi

import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.MediaInfo
import tv.danmaku.ijk.media.player.IjkMediaMeta
import top.yogiczy.mytv.ui.utils.IjkUtil
import android.util.Log

/**
 * 播放器状态
 */
class PlayerState {
    /** 视频分辨率 */
    var resolution by mutableStateOf(Pair(0, 0))

    /** 视频宽高比 */
    var aspectRatio by mutableFloatStateOf(16f / 9f)

    /** 是否出现错误 */
    var error by mutableStateOf(false)

    /** 元数据 */
    var metadata by mutableStateOf(Metadata())

    /** 元数据 */
    data class Metadata(
        /** 视频编码 */
        val videoMimeType: String = "",
        /** 视频宽度 */
        val videoWidth: Int = 0,
        /** 视频高度 */
        val videoHeight: Int = 0,
        /** 视频颜色 */
        val videoColor: String = "",
        /** 视频帧率 */
        val videoFrameRate: Float = 0f,
        /** 视频比特率 */
        val videoBitrate: Int = 0,
        /** 视频解码器 */
        val videoDecoder: String = "",

        /** 音频编码 */
        val audioMimeType: String = "",
        /** 音频通道 */
        val audioChannels: Int = 0,
        /** 音频采样率 */
        val audioSampleRate: Int = 0,
        /** 音频解码器 */
        val audioDecoder: String = "",
    )
}

@OptIn(UnstableApi::class)
@Composable
fun rememberPlayerState(
    ijkUtilInst: IjkUtil = IjkUtil.getInstance(),
): PlayerState {
    val state = remember { PlayerState() }

    fun addIjkUtilListener() {
        ijkUtilInst.setOnVideoSizeChangedListener("PlayerState") { width, height, sar_num, sar_den ->
            state.resolution = Pair(width, height)

            if (width != 0 && height != 0) {
                state.aspectRatio = width.toFloat() / height
            }
        }

        ijkUtilInst.setOnErrorListener("PlayerState") { what, extra ->
            state.error = true
        }

        ijkUtilInst.setOnInfoListener("PlayerState") { what, extra ->
            if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_START) {
                state.error = false
            }
        }

        ijkUtilInst.setOnPreparedListener("PlayerState") {
            var info : MediaInfo = ijkUtilInst.getMediaInfo()
            var TAG : String = "PlayerState"
            Log.i(TAG, "mMediaPlayerName=" + info.mMediaPlayerName);
            Log.i(TAG, "mVideoDecoder=" + info.mVideoDecoder);
            Log.i(TAG, "mVideoDecoderImpl=" + info.mVideoDecoderImpl);
            Log.i(TAG, "mAudioDecoder=" + info.mAudioDecoder);
            Log.i(TAG, "mAudioDecoderImpl=" + info.mAudioDecoderImpl);
            Log.i(TAG, "mBitrate=" + info.mMeta.mBitrate);

            Log.i(TAG, "v mType=" + info.mMeta.mVideoStream.mType);
            Log.i(TAG, "v mLanguage=" + info.mMeta.mVideoStream.mLanguage);
            Log.i(TAG, "v mCodecName=" + info.mMeta.mVideoStream.mCodecName);
            Log.i(TAG, "v mCodecProfile=" + info.mMeta.mVideoStream.mCodecProfile);
            Log.i(TAG, "v mCodecLongName=" + info.mMeta.mVideoStream.mCodecLongName);
            Log.i(TAG, "v mBitrate=" + info.mMeta.mVideoStream.mBitrate);
            Log.i(TAG, "v mFpsNum=" + info.mMeta.mVideoStream.mFpsNum);

            Log.i(TAG, "a mType=" + info.mMeta.mAudioStream.mType);
            Log.i(TAG, "a mLanguage=" + info.mMeta.mAudioStream.mLanguage);
            Log.i(TAG, "a mCodecName=" + info.mMeta.mAudioStream.mCodecName);
            Log.i(TAG, "a mCodecProfile=" + info.mMeta.mAudioStream.mCodecProfile);
            Log.i(TAG, "a mCodecLongName=" + info.mMeta.mAudioStream.mCodecLongName);
            Log.i(TAG, "a mBitrate=" + info.mMeta.mAudioStream.mBitrate);
            Log.i(TAG, "a mSampleRate=" + info.mMeta.mAudioStream.mSampleRate);
            Log.i(TAG, "a mChannelLayout=" + info.mMeta.mAudioStream.mChannelLayout);

            var channelCount : Int = 2
            when (info.mMeta.mAudioStream.mChannelLayout) {
                IjkMediaMeta.AV_CH_LAYOUT_MONO -> channelCount = 1

                IjkMediaMeta.AV_CH_LAYOUT_STEREO,
                IjkMediaMeta.AV_CH_LAYOUT_2POINT1,
                IjkMediaMeta.AV_CH_LAYOUT_STEREO_DOWNMIX -> channelCount = 2

                IjkMediaMeta.AV_CH_LAYOUT_2_1,
                IjkMediaMeta.AV_CH_LAYOUT_SURROUND,
                IjkMediaMeta.AV_CH_LAYOUT_3POINT1 -> channelCount = 3

                IjkMediaMeta.AV_CH_LAYOUT_4POINT0,
                IjkMediaMeta.AV_CH_LAYOUT_4POINT1,
                IjkMediaMeta.AV_CH_LAYOUT_2_2,
                IjkMediaMeta.AV_CH_LAYOUT_QUAD -> channelCount = 4

                IjkMediaMeta.AV_CH_LAYOUT_5POINT0,
                IjkMediaMeta.AV_CH_LAYOUT_5POINT1,
                IjkMediaMeta.AV_CH_LAYOUT_5POINT0_BACK,
                IjkMediaMeta.AV_CH_LAYOUT_5POINT1_BACK -> channelCount = 5

                IjkMediaMeta.AV_CH_LAYOUT_6POINT0,
                IjkMediaMeta.AV_CH_LAYOUT_6POINT0_FRONT,
                IjkMediaMeta.AV_CH_LAYOUT_HEXAGONAL,
                IjkMediaMeta.AV_CH_LAYOUT_6POINT1,
                IjkMediaMeta.AV_CH_LAYOUT_6POINT1_BACK,
                IjkMediaMeta.AV_CH_LAYOUT_6POINT1_FRONT -> channelCount = 6

                IjkMediaMeta.AV_CH_LAYOUT_7POINT0,
                IjkMediaMeta.AV_CH_LAYOUT_7POINT0_FRONT,
                IjkMediaMeta.AV_CH_LAYOUT_7POINT1,
                IjkMediaMeta.AV_CH_LAYOUT_7POINT1_WIDE,
                IjkMediaMeta.AV_CH_LAYOUT_7POINT1_WIDE_BACK -> channelCount = 7

                IjkMediaMeta.AV_CH_LAYOUT_OCTAGONAL -> channelCount = 8
            }

            state.metadata = state.metadata.copy(
                videoDecoder = info.mVideoDecoderImpl,
                videoMimeType = info.mMeta.mVideoStream.mCodecName,
                videoWidth = info.mMeta.mVideoStream.mWidth,
                videoHeight = info.mMeta.mVideoStream.mHeight,
                videoColor = "",
                // TODO 帧率、比特率目前是从tag中获取，有的返回空，后续需要实时计算
                videoFrameRate = info.mMeta.mVideoStream.mFpsNum.toFloat(),
                videoBitrate = 0,
                audioDecoder = info.mAudioDecoderImpl,
                audioChannels = channelCount,
                audioSampleRate = info.mMeta.mAudioStream.mSampleRate,
            )
        }
    }

    fun removeIjkUtilListener() {
        ijkUtilInst.removeOnVideoSizeChangedListener("PlayerState")
        ijkUtilInst.removeOnErrorListener("PlayerState")
        ijkUtilInst.removeOnPreparedListener("PlayerState")
    }

    DisposableEffect(Unit) {
        addIjkUtilListener()

        onDispose {
            removeIjkUtilListener()
        }
    }

    return state
}
