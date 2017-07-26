package cn.qssq666.musicplayer.music;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * http://mp.weixin.qq.com/s?__biz=MzA3NTYzODYzMg==&mid=2653577446&idx=2&sn=940cfe45f8da91277d1046d90368d440&scene=4#wechat_redirect
 */

public class PlayService extends Service {
    private static String TAG = "PlayService";
    private static final int INVALID_STATE = -1;
    PLAYMODE mPlayMode = PLAYMODE.LIST_LOOP;
    PLAYSTATE mPlayState = PLAYSTATE.MPS_NOFILE;

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    private MediaPlayer mMediaPlayer;
    private int mPlayPosition = -1;
    private int mErrorCount = 0;
    private MediaInfoPublisher mMediaInfoPublicher;
    private int mRequestPlayPosition = INVALID_STATE;


    public List<? extends MusicData> getMusicList() {
        return mMusicList;
    }

    List<? extends MusicData> mMusicList = new ArrayList<>();
    private PlayingMnager mMediaControlMnager;
    private boolean enterTempPauseMode;
    private double mMaxErrorCount = 3;
/*    BroadcastReceiver mLockScreenReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "屏幕锁定");
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                if (pm.isInteractive()) {
                    Log.d(TAG, "又解锁了1");
                    return;
                }
            } else {
                if (pm.isScreenOn()) {
                    Log.d(TAG, "又解锁了");
                    return;
                }
            }
            if (mMediaPlayer == null || !mMediaPlayer.isPlaying()) {//没有播放过就不弹出

                return;
            }
            Intent lock_intent = new Intent();
            lock_intent.setClass(PlayService.this,
                    LockScreenActivity.class);
            lock_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
            );
            *//*
            标志位FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS，是为了避免在最近使用程序列表出现Service所启动的Activity,但这个标志位不是必须的，其使用依情况而定。
             *//*
            PlayService.this.startActivity(lock_intent);
        }
    };*/

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
//        EventBus.getDefault().register(this);
        mMediaControlMnager = new PlayingMnager();
        mMediaInfoPublicher = new MediaInfoPublisher();
        requestAudioFocus();
    /*    IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mLockScreenReceiver, intentFilter);*/

    }

    public MusicData getCurrentModel(int position) {
        if (playPositionIsVolid(position)) {
            return null;
        }
        return mMusicList.get(position);
    }

    public boolean playPositionIsVolid(int position) {
        if (position < 0 || mMusicList.isEmpty() || position >= mMusicList.size()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 好像只能处理一次。
     * 结果发现 如果另外一个播放器播放获取了焦点了，那么一直就是对方的，除非对方释放了，除非你再次强求也许才会回调 focusChangeListenre，所以
     * 测试歌曲播放的时候打开qq音乐，然后开始播放 会被qq音乐获取焦点了，然后再在本软件播放然后再用qq音乐打开 无效了，因此 看来 要反复的操作，经不起折腾了，所以视频的我还是直接检测是否在播放播放就关闭了。
     *
     * @return
     */
    private boolean requestAudioFocus() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioManager.requestAudioFocus(focusChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);
    }

    private void abandonAudioFocus() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private AudioManager.OnAudioFocusChangeListener focusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {

                public void onAudioFocusChange(int focusChange) {
                    switch (focusChange) {
                        case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK):
                            doTempPauseLogic();
                            break;
                        case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT):
                            doTempPauseLogic();
                            break;
                        case (AudioManager.AUDIOFOCUS_LOSS):
                            doTempPauseLogic();
                            break;

                        case (AudioManager.AUDIOFOCUS_GAIN):
                            doTempContinueLogic();
                            break;

                        default:
                            break;

                    }
                }
            };

    private void doTempContinueLogic() {
        if (enterTempPauseMode && mMediaPlayer != null && mMediaPlayer.isPlaying() == false) {//如果是暂停的 而且是临时暂停模式

            mMediaPlayer.start();
            setPlayState(PLAYSTATE.MPS_PLAYING);
            //enterTempPauseMode 进入了这个模式怎么退出呢？只能用户操作的时候退出了，
        }
    }

    private void doTempPauseLogic() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            enterTempPauseMode = true;
            mMediaPlayer.pause();
            setPlayState(PLAYSTATE.MPS_PAUSE);
        } else {//如果你手动暂停了 且已经 进入了临时模式 我不管， 如果你在播放 ，那么照样走上面的逻辑。只要你暂停了 ，我就会取消临时模式 这样就保证不会再别的app暂停的情况下又收到的开始的。然后本来暂停的音乐又开始播放了。
            if (enterTempPauseMode) {
                enterTempPauseMode = false;
            }
        }
    }


    public int getPlayListPosition() {
        return mPlayPosition;
    }


    public enum PLAYMODE {
        RANDOM_PLAY(0), LIST_LOOP(1), LIST_PLAY(2), SIMPLE_LOOP(3), SIMPLE_PLAY(4);

        PLAYMODE(int value) {
            setValue(value);
        }

        public int getValue() {
            return value;

        }

        public void setValue(int value) {
            this.value = value;
            switch (value) {
                case 0:
                    this.name = "随机播放";
                    break;
                case 1:
                    this.name = "列表循环";
                    break;
                case 2:
                    this.name = "顺序播放";
                    break;
                case 3:
                    this.name = "单曲循环";
                    break;
            }
        }

        int value;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        String name;
    }

    //    新增随机播放、单曲循环、顺序播放、播放时间进度条、定时关闭、下载、评论
    public class MediaControlBinder extends Binder {
        public boolean addMediaInfoCallBack(IMediaControlCallBack mediaInfoCallBack) {
            return mMediaInfoPublicher.addMediaInfoCallBack(mediaInfoCallBack);

        }

        public boolean removeMediaInfoCallBack(IMediaControlCallBack mediaInfoCallBack) {
            return mMediaInfoPublicher.removeMediaInfoCallBack(mediaInfoCallBack);
        }

        public void setMusicList(List<? extends MusicData> musicFileList) {
            if (musicFileList == null) {
                musicFileList = new ArrayList<>();
            }
            if (isPlaying()) {
//                pause();

                destoryMediaPlayer();
            }
            mPlayPosition = -1;

            PlayService.this.mMusicList = musicFileList;
        }

        public List<? extends MusicData> getMusicList() {
            return mMusicList;
        }

        public int getCurrentDuration() {
            if (mediaIsVolid()) {
                return 0;
            }
            return mMediaPlayer.getCurrentPosition();
        }

        public boolean isCurrentControlList(MusicData musicData) {
            return mMusicList.contains(musicData);

        }

        public boolean isCurrentControlList(List<? extends MusicData> list) {
            boolean notEmpty = list != null && mMusicList != null;

            if (notEmpty && list == mMusicList) {
                return notEmpty;
            } else {

                if (list == null) {//数据都没有判断个鸡巴
                    return false;
                }


                if (notEmpty) {
                    if (list.size() != mMusicList.size()) {
                        return false;
                    }

                    if (list.containsAll(mMusicList) && mMusicList.containsAll(list)) {

                        Log.d(TAG, "播放列表完全和服务中相等" + mMusicList + "," + list);
                        return true;
                    } else {
                        Log.d(TAG, "长度相等数据不相同" + mMusicList + "," + list);
                    }
                }
                return false;
            }


            /*
             boolean result= list != null &
             */

        }

        public boolean isPlaying() {
            return mMediaPlayer != null && mMediaPlayer.isPlaying();
        }

        /*      public int getBufferPercentage(){
                  mMediaPlayer.GET
              }*/
        public int getPlayListPosition() {
            return PlayService.this.getPlayListPosition();

        }

        /**
         * 不会检查是否无效 不行哈
         *
         * @param position
         * @return
         */
        public MusicData getCurrentModel(int position) {
            return PlayService.this.getCurrentModel(position);
        }

        public MusicData getCurrentModel() {
            return PlayService.this.getCurrentModel();
        }

        /**
         * 如果没有播放没有初始化 用来获取这个判断类型也是可以的
         *
         * @return
         */
        public MusicData getFirstModel() {
            if (mMusicList != null && !mMusicList.isEmpty()) {

                return mMusicList.get(0);
            }

            return null;
        }

        public int getDuration() {
            if (mediaIsVolid()) {
                return 0;
            }
            return mMediaPlayer.getDuration();
        }

        public boolean pause() {
            clearProgressListener();
            setPlayState(PLAYSTATE.MPS_PAUSE);
            if (mediaIsVolid()) {
                return false;
            }
            mMediaPlayer.pause();

            return true;
        }


        public boolean play(MusicData data) {
            int position;
            if ((position = mMusicList.indexOf(data)) != -1) {
                return play(position);
            }
            return false;

        }

        public boolean play(int position) {
            if (mPlayPosition != position) {
                if (position >= mMusicList.size()) {
                    setErrPlayState("播放位置出现错误 ,position:" + position);
                    return false;
                }
                playMedia(getMusicUrlByPosition(mPlayPosition = position));
            } else {
                playOrPause();
            }
            return true;
        }

        /**
         * 不管是不是相同的都要重新播放 但是传递的必须是属于item的否则无法切换下一首
         *
         * @param data
         * @return
         */
        public void replay(MusicData data) {
            playMedia(data.getPlayUrl());
        }

        public void replay(MusicData data, int duration) {
            playMedia(data.getPlayUrl());
            mRequestPlayPosition = duration;
        }

        public boolean playOrPause() {
            if (mediaIsVolid()) {
                if (!playPositionIsVolid()) {
                    playMedia(getMusicUrlByPosition(mPlayPosition));
                } else {
                    setErrPlayState("切换播放失败,列表播放位置无效");
                }
                return false;
            } else {
                if (mMediaPlayer.isPlaying()) {
                    pause();
                } else {

                    continuePlay();
                }
            }
            return true;
        }

        public boolean playNext() {
            return doNextLogic(true);
          /*  if (mPlayPosition + 1 >= mMusicList.size()) {
                if (mPlayMode == PLAYMODE.LIST_LOOP) {
                    playMedia(getMusicUrlByPosition(mPlayPosition = 0));
                } else {
                    setErrPlayState("没有下一首啦！ ,position:" + (mPlayPosition + 1));

                }
                return false;
            } else {
                playMedia(getMusicUrlByPosition(++mPlayPosition));
                return true;
            }*/
            // TODO Auto-generated method stub
        }

        public boolean playPre() {
            if (mMusicList.isEmpty()) {
                setErrPlayState("播放列表为空");

                return false;
            } else {
                if (mPlayMode == PLAYMODE.SIMPLE_LOOP) {
                    setErrPlayState("单曲循环不支持切换");
                    return false;
//                    mPlayPosition = getRandomPosition();
                    //上一首还是不支持随机好了。
                } else if (mPlayPosition == 0 && mPlayMode == PLAYMODE.LIST_PLAY) {
                    setErrPlayState("没有上一首了哦");
                    return false;
//                    mPlayPosition = getRandomPosition();
                    //上一首还是不支持随机好了。
                } else {
                    //假如歌曲只有一首 我是根据列表总数-1所以没事
                    mPlayPosition = mPlayPosition == 0 ? mMusicList.size() - 1 : mPlayPosition - 1;
                }
                playMedia(getMusicUrlByPosition(mPlayPosition));
                return true;
            }
        }

        public boolean continuePlay() {
            if (mediaIsVolid()) {
                return false;
            }
            mMediaPlayer.start();
            setPlayState(PLAYSTATE.MPS_PLAYING);
            clearProgressListener();
            mProgressHandler.postDelayed(mProgressRunnable, 1000);//然后播放现在的 每过1秒发送一次进度
            return true;

        }

        public boolean seekTo(int seekto) {
            if (mediaIsVolid()) {
                return false;
            }
            Log.w(TAG, "seekTo:" + seekto);
            mMediaPlayer.seekTo(seekto);

            return true;
        }

        public boolean stop() {
            if (mediaIsVolid()) {
                return false;
            }
            mMediaPlayer.stop();
            clearProgressListener();
            setPlayState(PLAYSTATE.MPS_PAUSE);
            return true;
        }

        public PLAYSTATE getPlayState() {
            return mPlayState;
        }

        public void setPlayMode(PLAYMODE mode) {
            mPlayMode = mode;
            if (!mediaIsVolid()) {

                mMediaPlayer.setLooping(mPlayMode == PLAYMODE.SIMPLE_LOOP);//单曲播放就开启单曲循环播放
            }
        }

        public PLAYMODE getPlayMode() {
            return mPlayMode;
        }

        public boolean playPositionIsVolid() {
            return PlayService.this.playPositionIsVolid();
        }

        public boolean playPositionIsVolid(int position) {
            return PlayService.this.playPositionIsVolid(position);
        }

      /*  public boolean playPositionIsVolid() {
        }*/
    }

    private MusicData getCurrentModel() {
        if (PlayService.this.playPositionIsVolid()) {
            return null;
        }
        return mMusicList.get(getPlayListPosition());
    }

    public boolean playPositionIsVolid() {
        return playPositionIsVolid(getPlayListPosition());
    }

    public String getMusicUrlByPosition(int postion) {
        Log.d(TAG, "getMusicUrlByPosition " + postion);
        return mMusicList.get(postion).getPlayUrl();
    }

    public void setErrPlayState(String str) {

        mPlayState = PLAYSTATE.MPS_INVALID;
        mMediaInfoPublicher.publishErrorPlayStateEvent(PLAYSTATE.MPS_INVALID, str == null ? "" : str);
        clearProgressListener();
    }

    public void setPlayState(PLAYSTATE state) {
        mPlayState = state;

        mMediaInfoPublicher.publishPlayStateEvent(getMusicDataBy(mPlayPosition), mPlayState);
    }

    public MusicData getMusicDataBy(int mPlayPosition) {
        if (mPlayPosition == -1 || mPlayPosition >= mMusicList.size()) {
            return null;
        } else {
            return mMusicList.get(mPlayPosition);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MediaControlBinder();
    }

    private Handler mProgressHandler = new Handler();
    private Runnable mProgressRunnable = new Runnable() {

        @Override
        public void run() {
            if (mMediaPlayer != null) {
                mMediaInfoPublicher.publishProgressChangeEvent(mMediaPlayer.getCurrentPosition());
//                mProgressHandler.removeCallbacks(this);
                mProgressHandler.postDelayed(this, 1000);
            }
        }
    };

    private boolean mediaIsVolid() {
        return mMediaPlayer == null || mPlayState == PLAYSTATE.MPS_INVALID;
    }

    private void clearProgressListener() {
        mProgressHandler.removeCallbacks(mProgressRunnable);
    }

    public void playMedia(String playUrl) {
        clearProgressListener();
        mMediaControlMnager.seUrl(playUrl).playPrepareAsync();
    }

    private class PlayingMnager implements
            MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
            MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener,
            MediaPlayer.OnBufferingUpdateListener {
        public PlayingMnager() {
            initMedia();
        }

        private String mUrl;

        public PlayingMnager seUrl(String mUrl) {
            this.mUrl = mUrl;
            return this;
        }

        public void initMedia() {
            // 初始化MediaPlayer


        }


        public void playPrepareAsync() {
            try {


                // 开始播放
//                    Log.w(TAG, "播放URL:" + mUrl);
                if (mUrl == null) {
                    return;
                }
//                mMediaPlayer.release()
                destoryMediaPlayer();
                mMediaPlayer = onCreateMediaPlayer();
//                mMediaPlayer = MediaPlayer.create() new MediaPlayer();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setOnCompletionListener(this);
                mMediaPlayer.setOnPreparedListener(this);
                mMediaPlayer.setOnSeekCompleteListener(this);
                mMediaPlayer.setOnErrorListener(this);
//                mMediaPlayer.setOnTimedMetaDataAvailableListener();
                mMediaPlayer.setOnBufferingUpdateListener(this);
//                mMediaPlayer.setAudioSessionId(PlayService.this instanceof OnLinePlayService ? 1 : (PlayService.this instanceof SearchPlayService ? 2 : 3));
                onSetDataResource();
                mMediaPlayer.prepareAsync();
                MusicData currentModel = getCurrentModel();
                setPlayState(PLAYSTATE.MPS_PREPARE);

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                Log.e(TAG, "IllegalArgumentException无法播放" + mUrl);
                setErrPlayState("" + "播放失败,非法参数 ");
                Log.e(TAG, "无法播放" + mUrl);
            } catch (IOException e) {
                Log.e(TAG, "IOException无法播放" + mUrl);
                setErrPlayState("" + "播放失败," + e.toString());
                e.printStackTrace();
            } catch (Exception e) {
                Log.e(TAG, "Exception无法播放" + mUrl);
                setErrPlayState("" + "播放失败 " + e.toString());
            }
        }

        protected void onSetDataResource() throws IOException {
            if (mUrl.startsWith("android.resource://")) {
                mMediaPlayer.setDataSource(PlayService.this, Uri.parse(mUrl));

            } else {
                mMediaPlayer.setDataSource(mUrl);

            }
        }

        /**
         * 这里表示一曲播放完了，这时候，应该做的是
         *
         * @param mp
         */
        @Override
        public void onCompletion(MediaPlayer mp) {
          /*  if (mPlayState == PLAYSTATE.READY) {
                Log.d(TAG, "应该是在还未就绪的情况下 调用了start导致 发生-38 错误,比较操蛋的是错误为毛也要调用onCompletion");
                return;
            }*/
            Log.e(TAG, "onCompletion->Mode:" + mPlayMode);
            //发布一曲终了的广播
//            mMediaInfoPublicher.publishPlayStateEvent(PLAYSTATE.MPS_PAUSE);
            if (mPlayState == PLAYSTATE.MPS_INVALID && getMusicList().size() < 3) {
                Log.e(TAG, "发生错误,无法播放!");
            }
            if (mPlayState == PLAYSTATE.MPS_INVALID && mErrorCount >= mMaxErrorCount) {
                Log.e(TAG, "播放歌曲失败总数过多,已停止循环播放");
                return;
            }
            if (mPlayMode == PLAYMODE.SIMPLE_LOOP) {
                return;
            } else if (mPlayMode == PLAYMODE.SIMPLE_PLAY) {
                setPlayState(PLAYSTATE.MPS_PAUSE);
                return;
            } else {
                doNextLogic(false);
            }

        }

    /*
      /**
         * Called to indicate an error.
         *
         * @param mp      the MediaPlayer the error pertains to
         * @param what    the type of error that has occurred:
         * <ul>
         * <li>{@link #MEDIA_ERROR_UNKNOWN}
         * <li>{@link #MEDIA_ERROR_SERVER_DIED}
         * </ul>
         * @param extra an extra code, specific to the error. Typically
         * implementation dependent.
         * <ul>
         * <li>{@link #MEDIA_ERROR_IO}
         * <li>{@link #MEDIA_ERROR_MALFORMED}
         * <li>{@link #MEDIA_ERROR_UNSUPPORTED}
         * <li>{@link #MEDIA_ERROR_TIMED_OUT}
         * <li><code>MEDIA_ERROR_SYSTEM (-2147483648)</code> - low-level system error.
         * </ul>
         * @return True if the method handled the error, false if it didn't.
         * Returning false, or not having an OnErrorListener at all, will
         * cause the OnCompletionListener to be called.
         */

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            clearProgressListener();
            String wahtStr = "";
            String extraStr = "";
            switch (extra) {
                case MediaPlayer.MEDIA_ERROR_IO:
                    extraStr = "文件流错误";

                    break;
                case MediaPlayer.MEDIA_ERROR_MALFORMED:
                    extraStr = "格式不正确";
                    break;
                case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                    extraStr = " 此文件不支持";
                    break;
                case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                    extraStr = "请求超时";
                    break;
                default:
                    extraStr = " extra=(" + extra + ")";
                    break;
            }
            switch (what) {
                case -38:
                    wahtStr = "尚未就绪,请稍等片刻";
                    mPlayState = PLAYSTATE.READY;
                    break;
                case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                    wahtStr = "未知(waht=" + what + ")";
                    break;
                case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                    wahtStr = "服务器已关闭";
                    break;
                default:
                    wahtStr = "(waht:" + what + ")";
            }
            if (what == -38) {
//                setErrPlayState("正在就绪..请稍候");//TODO 获取消息不能太快...获取播放位置 否则会出错.
                return true;
            }
            mErrorCount++;
            Log.e(TAG, "onError播放出现错误,waht:" + what + ",extra:" + extra + "," + wahtStr);
            setErrPlayState(wahtStr + " " + extraStr);

            return false;//看来问题解决了 onComplete
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            Log.d(TAG, "onPrepared");
            clearProgressListener();
            mErrorCount = 0;//清空原来的错误
            mMediaInfoPublicher.publishProgressChangeEvent(0);
            mMediaPlayer.start();
            if (mRequestPlayPosition != INVALID_STATE && mRequestPlayPosition <= mMediaPlayer.getDuration()) {
                mMediaPlayer.seekTo(mRequestPlayPosition);
                mRequestPlayPosition = INVALID_STATE;
            }

            mMediaPlayer.setLooping(mPlayMode == PLAYMODE.SIMPLE_LOOP);
            mProgressHandler.postDelayed(mProgressRunnable, 1000);//然后播放现在的 每过1秒发送一次进度
            mMediaInfoPublicher.pulishonPreparedEvent(mPlayPosition);
            setPlayState(PLAYSTATE.MPS_PLAYING);
        }


        @Override
        public void onSeekComplete(MediaPlayer mp) {
            Log.d(TAG, "onSeekComplete");
        }

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            mMediaInfoPublicher.publishCacheChangeEvent(percent);
        }
    }

    protected MediaPlayer onCreateMediaPlayer() {
        return new MediaPlayer();
    }

    private void destoryMediaPlayer() {
        try {

            if (mMediaPlayer != null) {
                mMediaPlayer.setOnCompletionListener(null);
                mMediaPlayer.setOnPreparedListener(null);
                mMediaPlayer.reset();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        } catch (Exception e) {

        }
    }

    /**
     * 一首歌曲完成了或者是人为手动切换下一首都会触发
     *
     * @param isPersonOpera
     * @return
     */
    private boolean doNextLogic(boolean isPersonOpera) {
        if (mMusicList.isEmpty()) {
            mMediaInfoPublicher.publishTipMsg("列表为空");
            return false;
        }
        if (isPersonOpera == false && mPlayMode == PLAYMODE.SIMPLE_LOOP) {
            Log.d(TAG, "自动操作的单曲循环模式 无需继续");
            return false;
        }
        if (mPlayMode == PLAYMODE.LIST_LOOP || mPlayMode == PLAYMODE.SIMPLE_LOOP) {
            if (++mPlayPosition >= mMusicList.size()) {//如果不成立也自动加1了 假如只有一首也没事
                mPlayPosition = 0;
            }
            playMedia(getMusicUrlByPosition(mPlayPosition));
            return true;
        } else if (mPlayMode == PLAYMODE.RANDOM_PLAY) {
            mPlayPosition = getRandomPosition();
            playMedia(getMusicUrlByPosition(mPlayPosition));
            return true;
        } else if (mPlayMode == PLAYMODE.LIST_PLAY) {
            if (mPlayPosition + 1 >= mMusicList.size()) {
                setErrPlayState("没有下一首了！");
            } else {
                playMedia(getMusicUrlByPosition(++mPlayPosition));
            }
            return true;
        } else {
            mMediaInfoPublicher.publishTipMsg("当前模式不支持切换下一首哦!");
        }
        return false;
    }

    /*   @Subscribe(threadMode = ThreadMode.MAIN)
       public void onReceiveStopMsg(ClockStopEvent event) {
           if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
               mMediaPlayer.pause();
           }
       }
   */
    private int getRandomPosition() {
        return (int) (Math.random() * mMusicList.size());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
//        EventBus.getDefault().unregister(this);
        if (null != mMediaPlayer) {
            mProgressHandler.removeCallbacks(mProgressRunnable);
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        abandonAudioFocus();
//        this.unregisterReceiver(mLockScreenReceiver);
    }
}
