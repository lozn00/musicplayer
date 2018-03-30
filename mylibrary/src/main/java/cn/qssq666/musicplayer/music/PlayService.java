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

    public PlayService.MediaControlBinder getMediaControlBinder() {
        return mediaControlBinder;
    }

    public void setMediaControlBinder(MediaControlBinder mediaControlBinder) {
        this.mediaControlBinder = mediaControlBinder;
    }

    private PlayService.MediaControlBinder mediaControlBinder;

    public IMediaPlayerProxy getMediaPlayer() {
        return mMediaPlayer;
    }


    private IMediaPlayerProxy mMediaPlayer;
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
                    switch (focusChange) {//https://blog.csdn.net/thl789/article/details/7422931
                        case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK)://暂时失去AudioFocus，但是可以继续播放，不过要在降低音量。
                            doTempPauseLogic();
                            break;
                        case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT)://暂时失去Audio Focus，并会很快再次获得。必须停止Audio的播放，但是因为可能会很快再次获得AudioFocus，这里可以不释放Media资源；
                            doTempPauseLogic();
                            break;
                        case (AudioManager.AUDIOFOCUS_LOSS)://并将会持续很长的时间。这里因为可能会停掉很长时间，所以不仅仅要停止Audio的播放，最好直接释放掉Media资源。而因为停止播放Audio的时间会很长，如果程序因为这个原因而失去AudioFocus，最好不要让它再次自动获得AudioFocus而继续播放，不然突然冒出来的声音会让用户感觉莫名其妙，感受很不好。这里直接放弃AudioFocu
                            doTempPauseLogic();
                            break;

                        case (AudioManager.AUDIOFOCUS_GAIN)://指示申请得到的Audio Focus不知道会持续多久，一般是长期占有；
                            doTempContinueLogic();
                            break;

                        default:
                            break;

                    }
                }
            };

    private void doTempContinueLogic() {
        if (enterTempPauseMode && mMediaPlayer != null && mMediaPlayer.isPlaying() == false) {//如果是暂停的 而且是临时暂停模式

            //enterTempPauseMode 进入了这个模式怎么退出呢？只能用户操作的时候退出了，
        }
            doStartInner();
            setPlayState(PLAYSTATE.MPS_PLAYING);
    }

    private void doStartInner() {
        requestAudioFocus();
        mMediaPlayer.start();

    }

    private void doTempPauseLogic() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            enterTempPauseMode = true;
            setPlayState(PLAYSTATE.MPS_PAUSE);
        } else {//如果你手动暂停了 且已经 进入了临时模式 我不管， 如果你在播放 ，那么照样走上面的逻辑。只要你暂停了 ，我就会取消临时模式 这样就保证不会再别的app暂停的情况下又收到的开始的。然后本来暂停的音乐又开始播放了。
            if (enterTempPauseMode) {
                enterTempPauseMode = false;
            }
        }
            pauseInner();
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


        private Object tag;
        private int page;

        /**
         * @param playSpeed
         * @return
         * @throws IllegalStateException 不支持的时候会 抛出!
         */
        public boolean setPlaySpeed(float playSpeed) throws IllegalStateException {
            return PlayService.this.setPlaySpeed(playSpeed);
        }

        public float getPlaySpeed() {
//            float speed = getMediaPlayer().getPlaybackParams().getSpeed();
            return PlayService.this.getPlaySpeed();
        }


        public IMediaPlayerProxy getMediaPlayer() {
            return mMediaPlayer;
        }

        public boolean addMediaInfoCallBack(IMediaControlCallBack mediaInfoCallBack) {
            return mMediaInfoPublicher.addMediaInfoCallBack(mediaInfoCallBack);

        }

        public boolean removeMediaInfoCallBack(IMediaControlCallBack mediaInfoCallBack) {
            return mMediaInfoPublicher.removeMediaInfoCallBack(mediaInfoCallBack);
        }

        public boolean setMusicList(List<? extends MusicData> musicFileList) {
            if (musicFileList == null) {
                return false;
            }
            if (isPlaying()) {
//                pause();

                destoryMediaPlayer();
            }
            mPlayPosition = -1;

            if (PlayService.this.mMusicList != null) {
                mMusicList.clear();
            }
            List temp = musicFileList;
            PlayService.this.mMusicList.addAll(temp);

            return false;
        }


        public List<? extends MusicData> getMusicList() {
            return mMusicList;
        }

        public int getCurrentDuration() {
            if (mediaIsVolid()) {
                return 0;
            }
            return (int) mMediaPlayer.getCurrentPositionProxy();
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

        /**
         * the duration in milliseconds, if no duration is available
         * (for example, if streaming live content), -1 is returned.
         *
         * @return
         */
        public long getDuration() {
            if (mediaIsVolid()) {
                return -1;
            }
            return mMediaPlayer.getDurationProxy();
        }

        public boolean pause() {
            clearProgressListener();
            setPlayState(PLAYSTATE.MPS_PAUSE);
            if (mediaIsVolid()) {
                return false;
            }
            pauseInner();
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
            doStartInner();
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

        public void setTag(Object tag) {
            this.tag = tag;
        }

        public Object getTag() {
            return tag;
        }

        public boolean apppendMusicList(List<? extends MusicData> musicFileList) {

            if (PlayService.this.mMusicList == null) {
                return false;
            } else {
                List temp = musicFileList;
                PlayService.this.mMusicList.addAll(temp);
                return true;
            }
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getPage() {
            return page;
        }

        public boolean playFirst() {
            if (mMusicList == null || mMusicList.isEmpty()) {
                return false;
            } else {
                MusicData musicData = mMusicList.get(0);
                play(musicData);
                return true;

            }

        }

        public boolean playLast() {
            if (mMusicList == null || mMusicList.isEmpty()) {
                return false;
            } else {
                MusicData musicData = mMusicList.get(mMusicList.size() - 1);
                play(musicData);
                return true;

            }

        }

        /**
         * 是否可以直接切换暂停或者播放，不可以的话就需要重新播放
         *
         * @return
         */
        public boolean isReady() {
            return mMediaPlayer != null && mMediaPlayer.getDurationProxy() > 0;
        }

        public void destory() {
            PlayService.this.destoryMediaPlayer();
            ;
        }


      /*  public boolean playPositionIsVolid() {
        }*/
    }

    private void pauseInner() {
        mMediaPlayer.pause();
        abandonAudioFocus();
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
        mediaControlBinder = new MediaControlBinder();
        Log.w(TAG, "onBind" + mediaControlBinder);
        return mediaControlBinder;
    }

    private Handler mProgressHandler = new Handler();
    private Runnable mProgressRunnable = new Runnable() {

        @Override
        public void run() {
            if (mMediaPlayer != null) {
                mMediaInfoPublicher.publishProgressChangeEvent(mMediaPlayer.getCurrentPositionProxy());
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
            IMediaPlayerProxy.OnCompletionListener, IMediaPlayerProxy.OnErrorListener,
            IMediaPlayerProxy.OnPreparedListener, IMediaPlayerProxy.OnSeekCompleteListener,
            IMediaPlayerProxy.OnBufferingUpdateListener {
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

                requestAudioFocus();
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
                mMediaPlayer.setDataSourceProxy(PlayService.this, Uri.parse(mUrl));

            } else {
                mMediaPlayer.setDataSourceProxy(mUrl);

            }
        }

        /**
         * 这里表示一曲播放完了，这时候，应该做的是
         *
         * @param mp
         */
        @Override
        public void onCompletion(IMediaPlayerProxy mp) {
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
        public boolean onError(IMediaPlayerProxy mp, int what, int extra) {
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
        public void onPrepared(IMediaPlayerProxy mp) {
            if (mPlaySpeed != 1.0f) {
                try {
                    setPlaySpeed(mPlaySpeed);

                } catch (IllegalStateException e) {
                    Log.d(TAG, "onPrepared-not set playSpeed");

                }
            }
            Log.d(TAG, "onPrepared");
            clearProgressListener();


            mErrorCount = 0;//清空原来的错误
            mMediaInfoPublicher.publishProgressChangeEvent(0);
            doStartInner();
            if (mRequestPlayPosition != INVALID_STATE && mRequestPlayPosition <= mMediaPlayer.getDurationProxy()) {
                mMediaPlayer.seekTo(mRequestPlayPosition);
                mRequestPlayPosition = INVALID_STATE;
            }

            mMediaPlayer.setLooping(mPlayMode == PLAYMODE.SIMPLE_LOOP);
            mProgressHandler.postDelayed(mProgressRunnable, 1000);//然后播放现在的 每过1秒发送一次进度
            mMediaInfoPublicher.pulishonPreparedEvent(mPlayPosition);
            setPlayState(PLAYSTATE.MPS_PLAYING);
        }


        @Override
        public void onSeekComplete(IMediaPlayerProxy mp) {
            Log.d(TAG, "onSeekComplete");
        }

        @Override
        public void onBufferingUpdate(IMediaPlayerProxy mp, int percent) {
            mMediaInfoPublicher.publishCacheChangeEvent(percent);
        }
    }

    protected IMediaPlayerProxy onCreateMediaPlayer() {
        return new SystemMediaPlayerProxyImpl();
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


    protected boolean doNextLogic(boolean isPersonOpera) {
        if (mMusicList.isEmpty()) {
            mMediaInfoPublicher.publishTipMsg("列表为空");
            return false;
        }
        if (isPersonOpera == false && mPlayMode == PLAYMODE.SIMPLE_LOOP) {
            Log.d(TAG, "自动操作的单曲循环模式 无需继续");
            return false;
        }

        int playPosition = -1;
        if (mPlayMode == PLAYMODE.LIST_LOOP || mPlayMode == PLAYMODE.SIMPLE_LOOP) {
            playPosition = 1 + mPlayPosition;
            if (playPosition >= mMusicList.size()) {//如果不成立也自动加1了 假如只有一首也没事
                playPosition = 0;
            }
        } else if (mPlayMode == PLAYMODE.RANDOM_PLAY) {
            playPosition = getRandomPosition();
            playMedia(getMusicUrlByPosition(mPlayPosition));
        } else if (mPlayMode == PLAYMODE.LIST_PLAY) {
            playPosition = mPlayPosition + 1;
            if (playPosition >= mMusicList.size()) {
                setErrPlayState("没有下一首了！");
            } else {
            }
        } else {
            playPosition = -2;
            mMediaInfoPublicher.publishTipMsg("当前模式不支持切换下一首哦!");
        }


        if (playPosition >= 0) {

            if (onPlayNextMusicModel(getCurrentModel(playPosition), playPosition)) {

                mPlayPosition = playPosition;//还是要付费!

            } else {
                return true;
            }
//            onPlayNextMusicModel(getMusicUrlByPosition(mPlayPosition));

        }

        return false;








      /*
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
        return false;*/

    }


    private float mPlaySpeed = 1.0f;

    /**
     * @param playSpeed
     * @return
     * @throws IllegalStateException 不支持的时候会 抛出!
     */
    public boolean setPlaySpeed(float playSpeed) throws IllegalStateException {
        this.mPlaySpeed = playSpeed;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {


            if (getMediaPlayer() == null) {
                return false;
            }
//            getMediaPlayer().setPlaybackParams(getMediaPlayer().getPlaybackParams().setSpeed(playSpeed));
            getMediaPlayer().setSpeed(playSpeed);
            this.mPlaySpeed = playSpeed;

            return true;


        } else {
            Log.e(TAG, "you are phone not support set play speed");

        }

        return false;
    }

    public float getPlaySpeed() {
//            float speed = getMediaPlayer().getPlaybackParams().getSpeed();
        return mPlaySpeed;
    }


    protected boolean onPlayNextMusicModel(MusicData musicUrlByPosition, int playPosition) {
//        onPlayNextMusicModel(getMusicUrlByPosition(mPlayPosition));
        playMedia(musicUrlByPosition.getPlayUrl());
        return true;
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
//        abandonAudioFocus();
//        this.unregisterReceiver(mLockScreenReceiver);
    }

}
