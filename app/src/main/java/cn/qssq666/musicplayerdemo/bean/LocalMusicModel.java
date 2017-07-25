package cn.qssq666.musicplayerdemo.bean;

import cn.qssq666.musicplayer.music.MusicData;

/**
 * Created by qssq on 2017/7/25 qssq666@foxmail.com
 */

public class LocalMusicModel implements MusicData {
    @Override
    public String getPlayUrl() {
        return path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    String path;
}
