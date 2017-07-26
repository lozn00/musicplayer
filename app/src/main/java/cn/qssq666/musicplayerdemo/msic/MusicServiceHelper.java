package cn.qssq666.musicplayerdemo.msic;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

import cn.qssq666.musicplayer.music.IMediaControlCallBack;
import cn.qssq666.musicplayer.music.MusicData;
import cn.qssq666.musicplayer.music.PlayService;
import cn.qssq666.musicplayerdemo.interfaces.ShowModelI;
import cn.qssq666.musicplayerdemo.utils.TestUtils;

/**
 * Created by 情随事迁(qssq666@foxmail.com) on 2017/2/15.
 * <p>
 * <p>
 * 需要越来越变态对于这种变态不得不需要进行一定的处理让每一个fragment都能实现点播台显示。 点播台也许也应该弹出爱过额
 * 使用完毕后务必调用销毁方法。
 */


public class MusicServiceHelper {
    private static String TAG = "MusicStation";

    private String name;
    private ShowModelI showModelI;

    public boolean isCurrentControlList(List<? extends MusicData> data) {

        return mPlaybinder.isCurrentControlList(data);
    }

    public void setMusicList(List<? extends MusicData> musicList) {
        mPlaybinder.setMusicList(musicList);
    }


    public interface OnMusicHelperBaceListener {
        void onPause(MusicData data);

        void onPlayErr(String str, MusicData data);

        void onPlay(MusicData data);

        void onBindService();
    }

    private Context context;


    /**
     * 当需要下载控制的时候进行绑定。
     */
//    private ServiceConnection mDownloadconn;
    OnMusicHelperBaceListener onMusicHelperBaceListener;

    private MusicServiceHelper(Context context, OnMusicHelperBaceListener onMusicHelperBaceListener) {
        this(context, onMusicHelperBaceListener, "unknown");
    }

    private MusicServiceHelper(Context context, OnMusicHelperBaceListener onMusicHelperBaceListener, Object o) {
        this(context, onMusicHelperBaceListener, o.getClass().getSimpleName());
    }

    private MusicServiceHelper(Context context, OnMusicHelperBaceListener onMusicHelperBaceListener, String musicStationName) {
        this.onMusicHelperBaceListener = onMusicHelperBaceListener;
        name = musicStationName;
        this.context = context;
        context.bindService(TestUtils.getMusicService(context), mPlayconn, Service.BIND_AUTO_CREATE);


    }


    public void destory() {

        if (mPlayconn != null) {
            context.unbindService(mPlayconn);
        }
        if (mMediaControlCallBack != null && mPlaybinder != null) {
            mPlaybinder.removeMediaInfoCallBack(mMediaControlCallBack);
        }
    }


    public boolean isPlay() {
        return mPlaybinder != null && mPlaybinder.isPlaying() ? true : false;
    }


    public static MusicServiceHelper getInstance(Context context, OnMusicHelperBaceListener changeListener) {
        return new MusicServiceHelper(context, changeListener);
    }

    public static MusicServiceHelper getInstance(Context context, OnMusicHelperBaceListener changeListener, Object name) {
        return new MusicServiceHelper(context, changeListener, name);
    }


    public PlayService.MediaControlBinder getPlaybinder() {
        return mPlaybinder;
    }

    private PlayService.MediaControlBinder mPlaybinder;
    private ServiceConnection mPlayconn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof PlayService.MediaControlBinder) {
                mPlaybinder = ((PlayService.MediaControlBinder) service);
                mPlaybinder.addMediaInfoCallBack(mMediaControlCallBack);
                if (onMusicHelperBaceListener != null) {
                    onMusicHelperBaceListener.onBindService();
                }
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    IMediaControlCallBack mMediaControlCallBack = new IMediaControlCallBack() {
        @Override
        public void onPlay(MusicData data) {
            if (onMusicHelperBaceListener != null) {
                onMusicHelperBaceListener.onPlay(data);
            }
        }

        @Override
        public void onPause(MusicData data) {

            if (onMusicHelperBaceListener != null)

            {
                onMusicHelperBaceListener.onPause(data);
            }
        }


        @Override
        public void onPlayError(String str) {
            Log.d(TAG, "onPlayError");
            Log.d(TAG, "播放错误");

            if (onMusicHelperBaceListener != null) {
                onMusicHelperBaceListener.onPlayErr(str, mPlaybinder.getCurrentModel());
            }
        }

        @Override
        public void onPauseProgress() {
            Log.d(TAG, "onPauseProgress");
        }


        @Override
        public void onSkipToNext() {
            Log.d(TAG, "onSkipToNext");
        }

        @Override
        public void onSkipToPrevious() {
            Log.d(TAG, "onSkipToPrevious");
        }

        @Override
        public void onSeekTo(long pos) {
            Log.d(TAG, "onSeekTo：" + pos);
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {

        }

        @Override
        public void onProgressChnage(long position) {
            //mPopManager.updatePopProgressBarAndCurrentPlayTime(position);
            Log.d(TAG, "onProgressChnage得到了时间：" + MediaController.generateTime(position));
        }

        @Override
        public void onCacheProgressChnage(int position) {
            Log.d(TAG, "onCacheProgressChnage缓存进度：" + position);
            //mPopManager.updateCacheProgress(position);
        }

        @Override
        public void onPrepared(int position) {
        }

        @Override
        public void onMsgTip(String msg) {
        }
    };


    /**
     * 切换下一首
     */
    private void changeToNext() {
//        reSetBtn();
        if (mPlaybinder.playNext()) {//虽然会设置2边但是给人的体验是好的。
            MusicData currentModel = mPlaybinder.getCurrentModel();
            if (currentModel instanceof ShowModelI) {

                showModelI = (ShowModelI) currentModel;
            } else {
                if (currentModel != null) {
                }
            }
        }
    }


    public void onMusicPositionClick(int position, ShowModelI model) {
        if (position == -1) {
            return;
        }
        this.showModelI = model;
        mPlaybinder.play(position);
    }

    /**
     * 不会进入切换模式 也就是是暂停就是暂停了 而非暂停就进行播放.也就是判断列表是不是这个数据。
     *
     * @param position
     * @param model
     */
    public void onMusicPositionClickKeepPlay(int position, ShowModelI model) {
        if (position == -1) {
            return;
        }
        this.showModelI = model;
        if (mPlaybinder.getCurrentModel() == model) {
            return;
        }
        mPlaybinder.play(position);
    }


}

