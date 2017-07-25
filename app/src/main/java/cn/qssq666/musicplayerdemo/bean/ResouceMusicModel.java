package cn.qssq666.musicplayerdemo.bean;

import cn.qssq666.musicplayer.music.MusicData;

/**
 * Created by qssq on 2017/7/25 qssq666@foxmail.com
 */

public class ResouceMusicModel extends LocalMusicModel implements MusicData {
    @Override
    public String getPlayUrl() {
//        return Uri.parse(getPath());
return null;//要播放资源文件，只需要..继承PlayService

    }
}
