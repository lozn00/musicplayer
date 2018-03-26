package cn.qssq666.musicplayer.music;

import android.content.Context;
import android.net.Uri;

import java.io.IOException;

/**
 * Created by qssq on 2018/3/26 qssq666@foxmail.com
 */

public interface IMediaPlayerProxy {
    public void setWakeMode(Context context, int mode);
    boolean isPlaying();

    void start();

    void pause();

    void prepareAsync();

    void setOnBufferingUpdateListener(OnBufferingUpdateListener listener);

    void setOnErrorListener(OnErrorListener listener);

    void setOnPreparedListener(OnPreparedListener listener);

    void setOnCompletionListener(OnCompletionListener listener);

    void setAudioStreamType(int streamMusic);

    void reset();

    void release();

    void seekTo(int seekto);

    void stop();

    void setLooping(boolean loop);

    void setOnSeekCompleteListener(OnSeekCompleteListener listener);

    void setDataSourceProxy(Context context, Uri parse) throws IOException;

    void setDataSourceProxy(String url) throws IOException;

    long getCurrentPositionProxy();

    long getDurationProxy();

    void setSpeed(float playSpeed);
    float getSpeed();



    public interface OnSeekCompleteListener
    {
        public void onSeekComplete(IMediaPlayerProxy mp);
    }

    public interface OnCompletionListener
    {
        void onCompletion(IMediaPlayerProxy mp);

    }

    public interface OnBufferingUpdateListener
    {
        void onBufferingUpdate(IMediaPlayerProxy mp, int percent);
    }

    public interface OnErrorListener
    {
        boolean onError(IMediaPlayerProxy mp, int what, int extra);
    }

    public interface OnPreparedListener
    {
        void onPrepared(IMediaPlayerProxy mp);
    }
}
