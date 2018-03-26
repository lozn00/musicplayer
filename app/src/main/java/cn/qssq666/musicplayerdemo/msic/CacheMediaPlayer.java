package cn.qssq666.musicplayerdemo.msic;

import android.media.MediaPlayer;

import com.danikula.videocache.CacheListener;

import java.io.File;
import java.io.IOException;

import cn.qssq666.musicplayer.music.SystemMediaPlayerProxyImpl;
import cn.qssq666.musicplayerdemo.AppContext;

/**
 * Created by qssq on 2017/7/18 qssq666@foxmail.com
 */

public class CacheMediaPlayer extends SystemMediaPlayerProxyImpl implements CacheListener {

    public CacheMediaPlayer() {
        super();
        super.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                if (onBufferingUpdateListener != null) {
                    onBufferingUpdateListener.onBufferingUpdate(mp, percent);
                }
            }
        });
    }

    @Override
    public void setOnBufferingUpdateListener(OnBufferingUpdateListener onBufferingUpdateListener) {
        this.onBufferingUpdateListener = onBufferingUpdateListener;
    }

    OnBufferingUpdateListener onBufferingUpdateListener;

    private String mPath;

    @Override
    public void start() throws IllegalStateException {
        super.start();
    }

    @Override
    public void pause() throws IllegalStateException {
        super.pause();
    }

    @Override
    public void stop() throws IllegalStateException {
        super.stop();

    }

    @Override
    public void reset() {
        //重置代码不能设置多次 包括设置数据源 等等因此换一首歌需要换一个 media
        AppContext.getProxy().unregisterCacheListener(this);
//        AppContext.getProxy(AppContext.getInstance()).unregisterCacheListener(this, mPath);
        super.reset();
    }

    @Override
    public void release() {

        super.release();
    }

    @Override
    public void prepare() throws IOException, IllegalStateException {
        super.prepare();
    }
/*
    @Override
    public void setDataSource(@NonNull AssetFileDescriptor afd) throws IOException, IllegalArgumentException, IllegalStateException {
        super.setDataSource(afd);
    }

    @Override
    public void setDataSource(@NonNull Context context, @NonNull Uri uri) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        super.setDataSource(context, uri);
    }

    @Override
    public void setDataSource(FileDescriptor fd) throws IOException, IllegalArgumentException, IllegalStateException {
        super.setDataSource(fd);
    }*/


    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        mPath = path;
        if (isHttp()) {

            AppContext.getProxy().registerCacheListener(this, mPath);
            super.setDataSource(AppContext.getProxy().getProxyUrl(mPath));

        } else {
            super.setDataSource(path);

        }

    }

    protected boolean isHttp() {
        if (mPath != null && mPath.contains("http")) {
            return true;
        }
        return false;

    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        super.prepareAsync();

    }

    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
        if (onBufferingUpdateListener != null) {
            onBufferingUpdateListener.onBufferingUpdate(null, percentsAvailable);
        }
    }

    @Override
    public void setOnPreparedListener(OnPreparedListener listener) {
        super.setOnPreparedListener(listener);

    }

    /*    mMediaPlayer.reset();
            mMediaPlayer.release();*/
}
