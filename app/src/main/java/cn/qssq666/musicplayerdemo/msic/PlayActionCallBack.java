package cn.qssq666.musicplayerdemo.msic;

import cn.qssq666.musicplayer.music.MusicData;
import cn.qssq666.musicplayer.music.PlayService;

/**
 * Created by qssq on 2017/7/25 qssq666@foxmail.com
 */

public interface PlayActionCallBack {
    int getTotalTime();

    MusicData getCurrentMusicData();

    void setPlayMode(PlayService.PLAYMODE mode);

    void seekTo(int duration);

    boolean playNext();

    boolean playPre();

    void pauseOrPlay();

    PlayService.PLAYMODE getPlayMode();


  /*      void onClickClock();

        void onClickShare();*/

    boolean isPlay();
}
