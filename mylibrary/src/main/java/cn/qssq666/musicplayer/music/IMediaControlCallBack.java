package cn.qssq666.musicplayer.music;

import android.os.Bundle;

/**
 * Created by Administrator on 2016/12/1.
 */

public interface IMediaControlCallBack {
    void onPlay(MusicData musicData);

    void onPlayError(String str);

    void onPauseProgress();

    void onPause(MusicData musicData);

    void onSkipToNext();

    void onSkipToPrevious();

    void onSeekTo(long pos);

    void onCustomAction(String action, Bundle extras);

    void onProgressChnage(long position);

    void onCacheProgressChnage(int percent);

    /**
     * 这里可以获取进度信息 也可以获取位置,当列表播放改变的时候这个东西很重要了.在正确完成回调是需要在这个方法里面改变ui的。
     */
    void onPrepared(int position);

    void onMsgTip(String msg);
}
