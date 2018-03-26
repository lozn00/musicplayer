package cn.qssq666.musicplayer.music;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;

import java.io.IOException;

/**
 * Created by qssq on 2018/3/26 qssq666@foxmail.com
 */

public class SystemMediaPlayerProxyImpl extends MediaPlayer implements IMediaPlayerProxy {


    @Override
    public void setOnBufferingUpdateListener(final IMediaPlayerProxy.OnBufferingUpdateListener listener) {
        super.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                if (listener != null) {
                    listener.onBufferingUpdate(getInstance(), percent);
                }
            }
        });
    }

    @Override
    public void setOnErrorListener(final IMediaPlayerProxy.OnErrorListener listener) {
        super.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (listener == null) {
                    return false;
                } else {
                    return listener.onError(getInstance(), what, extra);

                }
            }
        });
    }

    @Override
    public void setOnPreparedListener(final IMediaPlayerProxy.OnPreparedListener listener) {
        super.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (listener != null) {
                    listener.onPrepared(getInstance());
                }

            }
        });
    }

    @Override
    public void setOnCompletionListener(final IMediaPlayerProxy.OnCompletionListener listener) {

        super.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                if (listener != null) {
                    listener.onCompletion(getInstance());
                }
            }
        });
    }

    @Override
    public void setOnSeekCompleteListener(final IMediaPlayerProxy.OnSeekCompleteListener listener) {
        super.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                if (listener != null) {
                    listener.onSeekComplete(getInstance());
                }

            }
        });
    }




    public IMediaPlayerProxy getInstance() {
        return this;
    }


    @Override
    public void setDataSourceProxy(Context context, Uri parse) {
        try {
            super.setDataSource(context, parse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setDataSourceProxy(String url) throws IOException {
        super.setDataSource(url);
    }

    @Override
    public long getCurrentPositionProxy() {
        return super.getCurrentPosition();
    }

    @Override
    public long getDurationProxy() {
        return super.getDuration();
    }

    @Override
    public void setSpeed(float playSpeed) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.setPlaybackParams(this.getPlaybackParams().setSpeed(playSpeed));
        }
    }

    @Override
    public float getSpeed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return this.getPlaybackParams().getSpeed();
        } else {
            return -1;
        }
    }
}
