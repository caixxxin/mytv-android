package top.yogiczy.mytv.tv.ui.utils;

import android.view.SurfaceHolder;
import android.util.Log;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;
import tv.danmaku.ijk.media.player.misc.IjkTrackInfo;
import tv.danmaku.ijk.media.player.MediaInfo;

import java.util.HashMap;

public class IjkUtil implements IMediaPlayer.OnPreparedListener,
                                IMediaPlayer.OnCompletionListener,
                                IMediaPlayer.OnBufferingUpdateListener,
                                IMediaPlayer.OnSeekCompleteListener,
                                IMediaPlayer.OnVideoSizeChangedListener,
                                IMediaPlayer.OnErrorListener,
                                IMediaPlayer.OnInfoListener {

    private String TAG = "IjkUtil";
    private static IjkUtil instance;
    private IjkMediaPlayer player;
    private String url;
    private IjkUtil() {
        Log.i(TAG, "constructor.");
        player = new IjkMediaPlayer();
        url = "";
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnBufferingUpdateListener(this);
        player.setOnSeekCompleteListener(this);
        player.setOnVideoSizeChangedListener(this);
        player.setOnErrorListener(this);
        player.setOnInfoListener(this);

        mOnPreparedListeners = new HashMap<>();
        mOnCompletionListeners = new HashMap<>();
        mOnBufferingUpdateListeners = new HashMap<>();
        mOnSeekCompleteListeners = new HashMap<>();
        mOnVideoSizeChangedListeners = new HashMap<>();
        mOnErrorListeners = new HashMap<>();
        mOnInfoListeners = new HashMap<>();
    }

    private HashMap<String, OnPreparedListener> mOnPreparedListeners;
    private HashMap<String, OnCompletionListener> mOnCompletionListeners;
    private HashMap<String, OnBufferingUpdateListener> mOnBufferingUpdateListeners;
    private HashMap<String, OnSeekCompleteListener> mOnSeekCompleteListeners;
    private HashMap<String, OnVideoSizeChangedListener> mOnVideoSizeChangedListeners;
    private HashMap<String, OnErrorListener> mOnErrorListeners;
    private HashMap<String, OnInfoListener> mOnInfoListeners;
    
    private Object lockOnPreparedListener = new Object();
    private Object lockOnCompletionListener = new Object();
    private Object lockOnBufferingUpdateListener = new Object();
    private Object lockOnSeekCompleteListener = new Object();
    private Object lockOnVideoSizeChangedListener = new Object();
    private Object lockOnErrorListener = new Object();
    private Object lockOnInfoListener = new Object();

    /*--------------------
     * Listeners
     */
    public interface OnPreparedListener {
        void onPrepared();
    }

    public interface OnCompletionListener {
        void onCompletion();
    }

    public interface OnBufferingUpdateListener {
        void onBufferingUpdate(int percent);
    }

    public interface OnSeekCompleteListener {
        void onSeekComplete();
    }

    public interface OnVideoSizeChangedListener {
        void onVideoSizeChanged(int width, int height,
                                int sar_num, int sar_den);
    }

    public interface OnErrorListener {
        void onError(int what, int extra);
    }

    public interface OnInfoListener {
        void onInfo(int what, int extra);
    }

    /*
     * 单实例 getInstance方法
     */
    public static IjkUtil getInstance() {
        synchronized(IjkUtil.class) {
            if (instance == null) {
                instance = new IjkUtil();
            }
        }
        return instance;
    }

    /*
     * 设置listener 方法
     */
    public void setOnPreparedListener(String key, OnPreparedListener listener) {
        synchronized (lockOnPreparedListener) {
            mOnPreparedListeners.put(key, listener);
        }
    }
    public void removeOnPreparedListener(String key) {
        synchronized (lockOnPreparedListener) {
            mOnPreparedListeners.remove(key);
        }
    }

    public void setOnCompletionListener(String key, OnCompletionListener listener) {
        synchronized (lockOnCompletionListener) {
            mOnCompletionListeners.put(key, listener);
        }
    }
    public void removeOnCompletionListener(String key) {
        synchronized (lockOnCompletionListener) {
            mOnCompletionListeners.remove(key);
        }
    }

    public void setOnBufferingUpdateListener(String key, OnBufferingUpdateListener listener) {
        synchronized (lockOnBufferingUpdateListener) {
            mOnBufferingUpdateListeners.put(key, listener);
        }
    }
    public void removeOnBufferingUpdateListener(String key) {
        synchronized (lockOnBufferingUpdateListener) {
            mOnBufferingUpdateListeners.remove(key);
        }
    }

    public void setOnSeekCompleteListener(String key, OnSeekCompleteListener listener) {
        synchronized (lockOnSeekCompleteListener) {
            mOnSeekCompleteListeners.put(key, listener);
        }
    }
    public void removeOnSeekCompleteListener(String key) {
        synchronized (lockOnSeekCompleteListener) {
            mOnSeekCompleteListeners.remove(key);
        }
    }

    public void setOnVideoSizeChangedListener(String key, OnVideoSizeChangedListener listener) {
        synchronized (lockOnVideoSizeChangedListener) {
            mOnVideoSizeChangedListeners.put(key, listener);
        }
    }
    public void removeOnVideoSizeChangedListener(String key) {
        synchronized (lockOnVideoSizeChangedListener) {
            mOnVideoSizeChangedListeners.remove(key);
        }
    }

    public void setOnErrorListener(String key, OnErrorListener listener) {
        synchronized (lockOnErrorListener) {
            mOnErrorListeners.put(key, listener);
        }
    }
    public void removeOnErrorListener(String key) {
        synchronized (lockOnErrorListener) {
            mOnErrorListeners.remove(key);
        }
    }

    public void setOnInfoListener(String key, OnInfoListener listener) {
        synchronized (lockOnInfoListener) {
            mOnInfoListeners.put(key, listener);
        }
    }
    public void removeOnInfoListener(String key) {
        synchronized (lockOnInfoListener) {
            mOnInfoListeners.remove(key);
        }
    }

    /*
     * ijkplayer方法
     */
    public void setDisplay(SurfaceHolder sh) {
        Log.i(TAG, "setDisplay SurfaceHolder=" + sh);
        player.setDisplay(sh);
    }

    public void setCacheDisplay(SurfaceHolder sh) {
        synchronized (lockCacheSurfaceHolder) {
            Log.i(TAG, "setCacheDisplay SurfaceHolder=" + sh);
            mCacheSurfaceHolder = sh;
        }
    }
    public void useCacheDisplay() {
        synchronized (lockCacheSurfaceHolder) {
            Log.i(TAG, "useCacheDisplay SurfaceHolder=" + mCacheSurfaceHolder);
            player.setDisplay(mCacheSurfaceHolder);
        }
    }
    public boolean hasCacheSurfaceHolder() {
        synchronized (lockCacheSurfaceHolder) {
            return (mCacheSurfaceHolder != null);
        }
    }
    private SurfaceHolder mCacheSurfaceHolder = null;
    private Object lockCacheSurfaceHolder = new Object();

    public void setDataSource(String path) {
        try {
            Log.i(TAG, "setDataSource path=" + path);
            player.setDataSource(path);
            url = path;
        } catch (Exception e) {
            Log.e(TAG, "setDataSource exception=" + e);
            notifyCommonError();
        }
    }

    public void prepareAsync() {
        try {
            Log.i(TAG, "prepareAsync");
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1);
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_timeout", 0);
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "user_agent", "qqlive");
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-all-videos", 1);
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect", 1);
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "infbuf", 1);
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1L);
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max_cached_duration", 15000);
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1);
            player.prepareAsync();
        } catch (IllegalStateException e) {
            Log.e(TAG, "prepareAsync exception=" + e);
            notifyCommonError();
        }
    }

    public void start() {
        try {
            Log.i(TAG, "start");
            player.start();
        } catch (IllegalStateException e) {
            Log.e(TAG, "start exception=" + e);
            notifyCommonError();
        }
    }

    public void pause() {
        try {
            Log.i(TAG, "pause");
            player.pause();
        } catch (IllegalStateException e) {
            Log.e(TAG, "pause exception=" + e);
            notifyCommonError();
        }
    }

    public void stop() {
        try {
            Log.i(TAG, "stop");
            player.stop();
        } catch (IllegalStateException e) {
            Log.e(TAG, "stop exception=" + e);
            notifyCommonError();
        }
    }

    public void reset() {
        Log.i(TAG, "reset");
        url = "";
        if (player == null) {
            Log.w(TAG, "reset unexpect case, need new player instance.");
            player = new IjkMediaPlayer();
            player.setOnPreparedListener(this);
            player.setOnCompletionListener(this);
            player.setOnBufferingUpdateListener(this);
            player.setOnSeekCompleteListener(this);
            player.setOnVideoSizeChangedListener(this);
            player.setOnErrorListener(this);
            player.setOnInfoListener(this);
        }
        player.reset();
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public String getUrl() {
        return url;
    }

    public String getVideoCodec() {
        MediaInfo media = player.getMediaInfo();
        return media.mMeta.mVideoStream.mCodecName.toUpperCase();
    }

    public String getAudioCodec() {
        MediaInfo media = player.getMediaInfo();
        return media.mMeta.mAudioStream.mCodecName.toUpperCase();
    }

    public MediaInfo getMediaInfo() {
        return player.getMediaInfo();
    }

    public long currentPosition() {
        Log.i(TAG, "currentPosition=" + player.getCurrentPosition());
        return player.getCurrentPosition();
    }

    public long getDuration() {
        Log.i(TAG, "getDuration=" + player.getDuration());
        return player.getDuration();
    }

    public void release() {
        Log.i(TAG, "release");
        player.setOnPreparedListener(null);
        player.setOnCompletionListener(null);
        player.setOnBufferingUpdateListener(null);
        player.setOnSeekCompleteListener(this);
        player.setOnVideoSizeChangedListener(null);
        player.setOnErrorListener(null);
        player.setOnInfoListener(null);
        player.release();
        player = null;
    }

    @Override
    public void onPrepared(IMediaPlayer mp) {
        synchronized (lockOnPreparedListener) {
            Log.i(TAG, "onPrepared");
            for (OnPreparedListener listener : mOnPreparedListeners.values()) {
                if (listener != null) {
                    listener.onPrepared();
                } else {
                    Log.e(TAG, "onPrepared listener is null.");
                }
            }
        }

        start();
        if (!isVideo()) {
            synchronized (lockOnInfoListener) {
                Log.i(TAG, "onInfo(prepared fail safe) MEDIA_INFO_VIDEO_RENDERING_START");
                for (OnInfoListener listener : mOnInfoListeners.values()) {
                    if (listener != null) {
                        listener.onInfo(IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START, 0);
                    } else {
                        Log.e(TAG, "onInfo listener is null.");
                    }
                }
            }
        }

        // MediaInfo info = mp.getMediaInfo();
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
    }

    @Override
    public void onCompletion(IMediaPlayer mp) {
        synchronized (lockOnCompletionListener) {
            Log.i(TAG, "onCompletion");
            for (OnCompletionListener listener : mOnCompletionListeners.values()) {
                if (listener != null) {
                    listener.onCompletion();
                } else {
                    Log.e(TAG, "onCompletion listener is null.");
                }
            }
        }
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer mp, int percent) {
        synchronized (lockOnBufferingUpdateListener) {
            // Log.i(TAG, "onBufferingUpdate percent=" + percent);
            for (OnBufferingUpdateListener listener : mOnBufferingUpdateListeners.values()) {
                if (listener != null) {
                    listener.onBufferingUpdate(percent);
                } else {
                    Log.e(TAG, "onBufferingUpdate listener is null.");
                }
            }
        }
    }

    @Override
    public void onSeekComplete(IMediaPlayer mp) {
        synchronized (lockOnSeekCompleteListener) {
            Log.i(TAG, "onSeekComplete");
            for (OnSeekCompleteListener listener : mOnSeekCompleteListeners.values()) {
                if (listener != null) {
                    listener.onSeekComplete();
                } else {
                    Log.e(TAG, "onSeekComplete listener is null.");
                }
            }
        }
    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {
        Log.i(TAG, "onVideoSizeChanged <- widht=" + width + " height=" + height + " sar_num=" + sar_num + " sar_den=" + sar_den);
        int videoWidth = mp.getVideoWidth();
        int videoHeight = mp.getVideoHeight();
        if (videoWidth != 0 && videoHeight != 0) {
            synchronized (lockOnVideoSizeChangedListener) {
                Log.i(TAG, "onVideoSizeChanged -> widht=" + videoWidth + " height=" + videoHeight + " sar_num=" + sar_num + " sar_den=" + sar_den);
                for (OnVideoSizeChangedListener listener : mOnVideoSizeChangedListeners.values()) {
                    if (listener != null) {
                        listener.onVideoSizeChanged(videoWidth, videoHeight, sar_num, sar_den);
                    } else {
                        Log.e(TAG, "onVideoSizeChanged listener is null.");
                    }
                }
            }
        }
    }

    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra) {
        synchronized (lockOnErrorListener) {
            Log.i(TAG, "onError what=" + what + " extra=" + extra);
            for (OnErrorListener listener : mOnErrorListeners.values()) {
                if (listener != null) {
                    listener.onError(what, extra);
                } else {
                    Log.e(TAG, "onError listener is null.");
                }
            }
        }
        return true;
    }

    // int MEDIA_INFO_UNKNOWN = 1;
    // int MEDIA_INFO_STARTED_AS_NEXT = 2;
    // int MEDIA_INFO_VIDEO_RENDERING_START = 3;
    // int MEDIA_INFO_VIDEO_TRACK_LAGGING = 700;
    // int MEDIA_INFO_BUFFERING_START = 701;
    // int MEDIA_INFO_BUFFERING_END = 702;
    // int MEDIA_INFO_NETWORK_BANDWIDTH = 703;
    // int MEDIA_INFO_BAD_INTERLEAVING = 800;
    // int MEDIA_INFO_NOT_SEEKABLE = 801;
    // int MEDIA_INFO_METADATA_UPDATE = 802;
    // int MEDIA_INFO_TIMED_TEXT_ERROR = 900;
    // int MEDIA_INFO_UNSUPPORTED_SUBTITLE = 901;
    // int MEDIA_INFO_SUBTITLE_TIMED_OUT = 902;

    // int MEDIA_INFO_VIDEO_ROTATION_CHANGED = 10001;
    // int MEDIA_INFO_AUDIO_RENDERING_START  = 10002;
    // int MEDIA_INFO_AUDIO_DECODED_START    = 10003;
    // int MEDIA_INFO_VIDEO_DECODED_START    = 10004;
    // int MEDIA_INFO_OPEN_INPUT             = 10005;
    // int MEDIA_INFO_FIND_STREAM_INFO       = 10006;
    // int MEDIA_INFO_COMPONENT_OPEN         = 10007;
    // int MEDIA_INFO_VIDEO_SEEK_RENDERING_START = 10008;
    // int MEDIA_INFO_AUDIO_SEEK_RENDERING_START = 10009;
    // int MEDIA_INFO_MEDIA_ACCURATE_SEEK_COMPLETE = 10100;

    // int MEDIA_ERROR_UNKNOWN = 1;
    // int MEDIA_ERROR_SERVER_DIED = 100;
    // int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 200;
    // int MEDIA_ERROR_IO = -1004;
    // int MEDIA_ERROR_MALFORMED = -1007;
    // int MEDIA_ERROR_UNSUPPORTED = -1010;
    // int MEDIA_ERROR_TIMED_OUT = -110;

    @Override
    public boolean onInfo(IMediaPlayer mp, int what, int extra) {
        synchronized (lockOnInfoListener) {
            Log.i(TAG, "onInfo what=" + what + " extra=" + extra);
            for (OnInfoListener listener : mOnInfoListeners.values()) {
                if (listener != null) {
                    listener.onInfo(what, extra);
                } else {
                    Log.e(TAG, "onInfo listener is null.");
                }
            }
        }
        return true;
    }

    private void notifyCommonError() {
        synchronized (lockOnErrorListener) {
            Log.i(TAG, "notifyCommonError.");
            for (OnErrorListener listener : mOnErrorListeners.values()) {
                if (listener != null) {
                    listener.onError(IMediaPlayer.MEDIA_ERROR_UNKNOWN, 100);
                } else {
                    Log.e(TAG, "notifyCommonError listener is null.");
                }
            }
        }
    }

    private boolean isVideo() {
        IjkTrackInfo[] trackInfo = player.getTrackInfo();
        if (trackInfo == null) return false;
        for (IjkTrackInfo info : trackInfo) {
            if (info.getTrackType() == ITrackInfo.MEDIA_TRACK_TYPE_VIDEO) {
                return true;
            }
        }
        return false;
    }
}
