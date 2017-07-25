package cn.qssq666.musicplayerdemo.bean;

import cn.qssq666.musicplayer.music.MusicData;

/**
 * Created by qssq on 2017/7/25 qssq666@foxmail.com
 */

public class TestMusicModel implements MusicData {

    @Override
    public String getPlayUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    String title;
    String url;
}
