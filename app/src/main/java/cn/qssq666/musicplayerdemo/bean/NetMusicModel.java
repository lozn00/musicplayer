package cn.qssq666.musicplayerdemo.bean;

import cn.qssq666.musicplayer.music.MusicData;
import cn.qssq666.musicplayerdemo.interfaces.ShowModelI;

/**
 * Created by qssq on 2017/7/25 qssq666@foxmail.com
 */

public class NetMusicModel implements MusicData, ShowModelI {
    public String getTitle() {
        return title;
    }

    public NetMusicModel(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public NetMusicModel() {
    }

    public void setTitle(String title) {
        this.title = title;
    }

    String title;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    String url;

    @Override
    public String getPlayUrl() {
        return getUrl();
    }


    @Override
    public String getMusicTitle() {
        return getTitle();
    }


    @Override
    public int getMusicId() {
        return 0;
    }


}
