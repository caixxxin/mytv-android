package top.yogiczy.mytv.ui.utils;

import android.view.SurfaceHolder;
import android.util.Log;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;
import tv.danmaku.ijk.media.player.misc.IjkTrackInfo;

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
    private IjkUtil() {
        player = new IjkMediaPlayer();
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
        player.setDisplay(sh);
    }

    public void setDataSource(String path) {
        try {
            Log.i(TAG, "setDataSource path=" + path);
            player.setDataSource(path);
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
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-all-videos", 1);
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
        player.reset();
    }

    public boolean isPlaying() {
        return player.isPlaying();
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
        new Thread() {
            @Override
            public void run() {
                try {
                    player.release();
                    player = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void onPrepared(IMediaPlayer mp) {
        synchronized (lockOnPreparedListener) {
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
                for (OnInfoListener listener : mOnInfoListeners.values()) {
                    if (listener != null) {
                        listener.onInfo(IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START, 0);
                    } else {
                        Log.e(TAG, "onInfo listener is null.");
                    }
                }
            }
        }
    }

    @Override
    public void onCompletion(IMediaPlayer mp) {
        synchronized (lockOnCompletionListener) {
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
        int videoWidth = mp.getVideoWidth();
        int videoHeight = mp.getVideoHeight();
        if (videoWidth != 0 && videoHeight != 0) {
            synchronized (lockOnVideoSizeChangedListener) {
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

    @Override
    public boolean onInfo(IMediaPlayer mp, int what, int extra) {
        synchronized (lockOnInfoListener) {
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
