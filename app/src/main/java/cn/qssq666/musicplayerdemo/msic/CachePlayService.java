package cn.qssq666.musicplayerdemo.msic;

import android.media.MediaPlayer;

import cn.qssq666.musicplayer.music.PlayService;

/**
 * Created by qssq on 2017/7/25 qssq666@foxmail.com
 * 支持自动离线缓存
 */

public class CachePlayService extends PlayService {
    @Override
    protected MediaPlayer onCreateMediaPlayer() {
        return new CacheMediaPlayer();
    }
}
