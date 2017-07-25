package cn.qssq666.musicplayer.music;

/**
 * Created by Administrator on 2016/12/1.
 */
public enum PLAYSTATE {
    MPS_NOFILE(-1),        // 无音乐文件
    MPS_INVALID(0),    // 当前音乐文件无效
    MPS_PREPARE(1),        // 准备就绪
    MPS_PLAYING(2),        // 播放中
    MPS_PAUSE(3),      // 暂停
    READY(4);      // 就绪中
    int value;

    PLAYSTATE(int value) {
        this.value = value;
    }
    }
