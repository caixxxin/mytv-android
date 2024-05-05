package top.yogiczy.mytv.ui.utils;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;


public class IjkUtil {
    private static IjkMediaPlayer instance;
 
    public static IjkMediaPlayer getInstance() {
        synchronized(IjkUtil.class) {
            if (instance == null) {
                instance = new IjkMediaPlayer();
            }
        }
        return instance;
    }
}