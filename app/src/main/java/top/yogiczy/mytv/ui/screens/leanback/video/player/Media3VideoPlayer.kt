package top.yogiczy.mytv.ui.screens.leanback.video.player

import android.content.Context
import android.net.Uri
import android.view.SurfaceView
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DecoderReuseEvaluation
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.util.EventLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.yogiczy.mytv.ui.utils.SP
import androidx.media3.common.PlaybackException as Media3PlaybackException

import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.MediaInfo
import tv.danmaku.ijk.media.player.IjkMediaMeta
import top.yogiczy.mytv.ui.utils.IjkUtil
import android.util.Log

@OptIn(UnstableApi::class)
class LeanbackMedia3VideoPlayer(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
) : LeanbackVideoPlayer(coroutineScope) {
    private val videoPlayer = IjkUtil.getInstance()

    private val TAG = "Media3VideoPlayer"

    private fun addIjkUtilListener() {
        videoPlayer.setOnVideoSizeChangedListener("PlayerState") { width, height, sar_num, sar_den ->
            triggerResolution(width, height)
        }
        videoPlayer.setOnErrorListener("PlayerState") { what, extra ->
            triggerError(PlaybackException.UNSUPPORTED_TYPE)
            true
        }
        videoPlayer.setOnInfoListener("PlayerState") { what, extra ->
            if (what == IMediaPlayer.MEDIA_INFO_FIND_STREAM_INFO) {
                triggerError(null)
                triggerBuffering(true)
            } else if (what == IMediaPlayer.MEDIA_INFO_COMPONENT_OPEN) {
                triggerReady()
                triggerCurrentPosition(0)
            }
            if (what != IMediaPlayer.MEDIA_INFO_FIND_STREAM_INFO) {
                triggerBuffering(false)
            }
            true
        }
        videoPlayer.setOnPreparedListener("PlayerState") {
            var info : MediaInfo = videoPlayer.getMediaInfo()
            var TAG : String = "PlayerState"
            // Log.i(TAG, "mMediaPlayerName=" + info.mMediaPlayerName);
            // Log.i(TAG, "mVideoDecoder=" + info.mVideoDecoder);
            // Log.i(TAG, "mVideoDecoderImpl=" + info.mVideoDecoderImpl);
            // Log.i(TAG, "mAudioDecoder=" + info.mAudioDecoder);
            // Log.i(TAG, "mAudioDecoderImpl=" + info.mAudioDecoderImpl);
            // Log.i(TAG, "mBitrate=" + info.mMeta.mBitrate);

            // Log.i(TAG, "v mType=" + info.mMeta.mVideoStream.mType);
            // Log.i(TAG, "v mLanguage=" + info.mMeta.mVideoStream.mLanguage);
            // Log.i(TAG, "v mCodecName=" + info.mMeta.mVideoStream.mCodecName);
            // Log.i(TAG, "v mCodecProfile=" + info.mMeta.mVideoStream.mCodecProfile);
            // Log.i(TAG, "v mCodecLongName=" + info.mMeta.mVideoStream.mCodecLongName);
            // Log.i(TAG, "v mBitrate=" + info.mMeta.mVideoStream.mBitrate);
            // Log.i(TAG, "v mFpsNum=" + info.mMeta.mVideoStream.mFpsNum);

            // Log.i(TAG, "a mType=" + info.mMeta.mAudioStream.mType);
            // Log.i(TAG, "a mLanguage=" + info.mMeta.mAudioStream.mLanguage);
            // Log.i(TAG, "a mCodecName=" + info.mMeta.mAudioStream.mCodecName);
            // Log.i(TAG, "a mCodecProfile=" + info.mMeta.mAudioStream.mCodecProfile);
            // Log.i(TAG, "a mCodecLongName=" + info.mMeta.mAudioStream.mCodecLongName);
            // Log.i(TAG, "a mBitrate=" + info.mMeta.mAudioStream.mBitrate);
            // Log.i(TAG, "a mSampleRate=" + info.mMeta.mAudioStream.mSampleRate);
            // Log.i(TAG, "a mChannelLayout=" + info.mMeta.mAudioStream.mChannelLayout);

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

            metadata = metadata.copy(
                videoDecoder = info.mVideoDecoderImpl,
                videoMimeType = info.mMeta.mVideoStream.mCodecName,
                videoWidth = info.mMeta.mVideoStream.mWidth,
                videoHeight = info.mMeta.mVideoStream.mHeight,
                videoColor = "",
                // TODO 帧率、比特率目前是从tag中获取，有的返回空，后续需要实时计算
                videoFrameRate = info.mMeta.mVideoStream.mFpsNum.toFloat(),
                videoBitrate = 0,
                audioMimeType = info.mMeta.mAudioStream.mCodecName,
                audioDecoder = info.mAudioDecoderImpl,
                audioChannels = channelCount,
                audioSampleRate = info.mMeta.mAudioStream.mSampleRate,
            )
            triggerMetadata(metadata)
        }
    }

    private fun removeIjkUtilListener() {
        videoPlayer.removeOnVideoSizeChangedListener("PlayerState")
        videoPlayer.removeOnErrorListener("PlayerState")
        videoPlayer.removeOnPreparedListener("PlayerState")
    }

    private val eventLogger = EventLogger()

    override fun initialize() {
        Log.i(TAG, "initialize")
        super.initialize()
        // videoPlayer.addListener(playerListener)
        // videoPlayer.addAnalyticsListener(metadataListener)
        // videoPlayer.addAnalyticsListener(eventLogger)
        addIjkUtilListener()
    }

    override fun release() {
        Log.i(TAG, "release")
        // videoPlayer.removeListener(playerListener)
        // videoPlayer.removeAnalyticsListener(metadataListener)
        // videoPlayer.removeAnalyticsListener(eventLogger)
        removeIjkUtilListener()
        videoPlayer.release()
        super.release()
    }

    @UnstableApi
    override fun prepare(url: String) {
        Log.i(TAG, "prepare")
        videoPlayer.reset()
        videoPlayer.setDataSource(url)
        videoPlayer.useCacheDisplay()
        videoPlayer.prepareAsync()
    }

    override fun play() {
        Log.i(TAG, "play")
        videoPlayer.start()
    }

    override fun pause() {
        Log.i(TAG, "pause")
        videoPlayer.pause()
    }

    override fun setVideoSurfaceView(surfaceView: SurfaceView) {
        Log.i(TAG, "setVideoSurfaceView")
        videoPlayer.setCacheDisplay(surfaceView.holder)
    }
}