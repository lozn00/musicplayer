
 图片演示

![演示图片地址1](https://github.com/qssq/musicplayer/blob/master/Pictures/1.png)
![演示图片地址2](https://github.com/qssq/musicplayer/blob/master/Pictures/2.png)
![演示图片地址3](https://github.com/qssq/musicplayer/blob/master/Pictures/3.png)
![演示图片地址4](https://github.com/qssq/musicplayer/blob/master/Pictures/4.png)
##### 模块功能介绍


模块无任何ui代码，只是一个播放器后台框架而已，是一个基于 service binder 作为控制台 +观察者模式提供回调的的音乐播放器


demo项目则演示了多个列表 多个界面的控制播放暂停 能力 ui列表展示的能力，
另外为了方便使用提供了一个工具类 帮助快速的绑定和接触binder绑定

#### 是否可靠
我的公司项目app正常使用中,复杂程度和类似正常的音乐app,目前功能比百度音乐人还复杂,多了各种语音 视频 动态 列表，各种fragment 嵌套多个，多个fragment中的列表播放控制和解绑。点击这边的fragment又要控制另外的 fragment列表的状态变成暂停，
所以说，这app绝对不是因为demo而出现，而是先有项目后有demo

#### 扩展性
兼容第三方MediaPlayer ，系统的播放器MediaPlayer已经适配好了```SystemMediaPlayerProxyImpl``
七牛PLMediaPlayer的代码我会在本文档最后么给出.各位根据模仿着改就ok的哈!
##### 使用方法

gradle
```
 compile 'cn.qssq666:musicplayer:0.2'


```

maven

```
<dependency>
  <groupId>cn.qssq666</groupId>
  <artifactId>musicplayer</artifactId>
  <version>0.1</version>
  <type>pom</type>
</dependency>
```

##### 视频地址

http://v.youku.com/v_show/id_XMjkyMTQ2MDAwOA==.html?spm=a2h3j.8428770.3416059.1

音乐列表后台播放器，支持各种播放模式,(顺序播放,循环播放,随机播放,单曲循环)使用观察者模式实现通知回调，使用bind的方法提供操作接口控制台
关于扩展性可靠性都是绝对可信的,我们公司开发的app我就为了隐私就不说了,但是我们公司的项目可以说比百度音乐人的还麻烦,各种音乐列表,各种音乐布局,还包括了列表进度监听等等,所以绝对不坑害大家,尽管使用哈!
另外还支持自动缓存 ，demo中已经写好了 如何使用缓存的方法,实际上就是CachePlayServiced的逻辑,只需要绑定此类就行了

另外本人没用任何的第三方库什么高级的Retrofit什么的,就用的观察者模式,实现多个控制台的通知,通熟易懂方便学习又能轻松应用用公司项目开发.
 


```
class MediaControlBinder extends Binder { 
   public boolean addMediaInfoCallBack(IMediaControlCallBack mediaInfoCallBack) {

        }

        public boolean removeMediaInfoCallBack(IMediaControlCallBack mediaInfoCallBack) {
        }
        /**
            设置控制台播放列表集合
        **/
        public void setMusicList(List<? extends MusicData> musicFileList) {
        }

        public List<? extends MusicData> getMusicList() {
        }

        public int getCurrentDuration() {
        }

        public boolean isCurrentControlList(MusicData musicData) {

        }

        public boolean isCurrentControlList(List<? extends MusicData> list) {

        }

        public boolean isPlaying() {
        }

        public int getPlayListPosition() {

        }

        public MusicData getCurrentModel(int position) {
        }

        public MusicData getCurrentModel() {
        }
 /**
         * 如果没有播放没有初始化 用来获取这个判断类型也是可以的
         *
         * @return
         */
        public MusicData getFirstModel() {
        }

        public int getDuration() {
        }

        public boolean pause() {
        }


        public boolean play(MusicData data) {
        }

        public boolean play(int position) {
        }

        public void replay(MusicData data) {
        }

        public void replay(MusicData data, int duration) {
        }

        public boolean playOrPause() {
        }

        public boolean playNext() {
        }

        public boolean playPre() {
        }

        public boolean continuePlay() {

        }

        public boolean seekTo(int rate) {
        }

        public boolean stop() {
        }

        public PLAYSTATE getPlayState() {
        }

        public void setPlayMode(PLAYMODE mode) {
        }

        public PLAYMODE getPlayMode() {
        }

        public boolean playPositionIsVolid() {
        }

        public boolean playPositionIsVolid(int position) {
}
}
```


demo封装了MusicServiceHelper代码 可以轻松在任意activity中使用
# MusicServiceHelper主要代码

```

    private MusicServiceHelper(Context context, OnMusicChangeListener onMusicChangeListener, String musicStationName) {
        string = musicStationName;
        this.context = context;
        Intent intent = null;
        intent = new Intent(context, PlayService.class);
        context.bindService(intent, mPlayconn, Service.BIND_AUTO_CREATE);
        this.onMusicChangeListener = onMusicChangeListener;


    }


    public void destory() {

        if (mPlayconn != null) {
            context.unbindService(mPlayconn);
        }
        if (mMediaControlCallBack != null && mPlaybinder != null) {
            mPlaybinder.removeMediaInfoCallBack(mMediaControlCallBack);
        }
    }


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
     
     
```
MusicServiceHelper是对绑定服务和添加监听的封装，各位不懂服务的绑定用法完全可以看MusicServiceHelper怎么写的哈,或者直接拿来用简单的需求，MainActivity中只需要判断是否有歌单则我通常就直接用这个帮助类了
完全没必要写麻烦的添加监听方法了.

# MusicServiceHelper 在activity或者fragment里面的用法


```
//onCreate中
  instance = MusicServiceHelper.getInstance(this, new MusicServiceHelper.OnMusicHelperBaceListener() {
            @Override
            public void onPause(MusicData data) {

            }

            @Override
            public void onPlayErr(String str, MusicData data) {

            }

            @Override
            public void onPlay(MusicData data) {

            }

            @Override
            public void onBindService() {

            }
        });
 //onDestory中
        instance.destory();


```



# 其他吐槽

demo已实现了离线缓存功能,轻松为服务器减压! 推荐做烧钱的大流量公司这么干，可以大大节省成本.

# PLMediaPlayer实现方案
```java


public class PLMediaPlayerImpl extends PLMediaPlayer implements IMediaPlayerProxy {

    private static final String TAG = "PLMediaPlayerImpl";

    public PLMediaPlayerImpl(Context context) {
        super(context);
    }

    public PLMediaPlayerImpl(Context context, AVOptions avOptions) {
        super(context, avOptions);
    }


    public PLMediaPlayerImpl getInstance() {
        return this;
    }




    @Override
    public void setOnBufferingUpdateListener(final IMediaPlayerProxy.OnBufferingUpdateListener listener) {
        super.setOnBufferingUpdateListener(new PLMediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(PLMediaPlayer plMediaPlayer, int percent) {
                if (listener != null) {
                    listener.onBufferingUpdate(getInstance(), percent);
                }
            }
        });
    }

    @Override
    public void setOnErrorListener(final IMediaPlayerProxy.OnErrorListener listener) {
        super.setOnErrorListener(new PLMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(PLMediaPlayer plMediaPlayer, int i) {
                if (listener == null) {
                    return false;
                } else {
                    return listener.onError(getInstance(), i, 0);

                }
            }
        });
    }

    @Override
    public void setOnPreparedListener(final IMediaPlayerProxy.OnPreparedListener listener) {
        super.setOnPreparedListener(new PLMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(PLMediaPlayer plMediaPlayer, int i) {
                if (listener != null) {
                    listener.onPrepared(getInstance());
                }
            }
        });
    }

    @Override
    public void setOnCompletionListener(final IMediaPlayerProxy.OnCompletionListener listener) {

        super.setOnCompletionListener(new PLMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(PLMediaPlayer mp) {

                if (listener != null) {
                    listener.onCompletion(getInstance());
                }
            }
        });
    }

    @Override
    public void setOnSeekCompleteListener(final IMediaPlayerProxy.OnSeekCompleteListener listener) {
        super.setOnSeekCompleteListener(new PLMediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(PLMediaPlayer plMediaPlayer) {
                if (listener != null) {
                    listener.onSeekComplete(getInstance());
                }
            }
        });
    }


    @Override
    public void setAudioStreamType(int streamMusic) {

    }

    @Override
    public void seekTo(int seekto) {

        Prt.w(TAG,"seekTo:"+seekto);
        super.seekTo(seekto);
    }


    @Override
    public void setDataSourceProxy(Context context, Uri parse) throws IOException {
        super.setDataSource(context, parse);
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

    }

    @Override
    public float getSpeed() {
        return 0;
    }
}


```
七牛服务
```java



public class LiveMusicService extends PlayService {

    @Override
    protected IMediaPlayerProxy onCreateMediaPlayer() {
        AVOptions options = new AVOptions();
        int isLiveStreaming = 0;
        // the unit of timeout is ms
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);
        // Some optimization with buffering mechanism when be set to 1
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, isLiveStreaming);
        if (isLiveStreaming == 1) {
            options.setInteger(AVOptions.KEY_DELAY_OPTIMIZATION, 1);
        }
        // 1 -> hw codec enable, 0 -> disable [recommended]
//        int codec = getIntent().getIntExtra("mediaCodec", 0);
        options.setInteger(AVOptions.KEY_MEDIACODEC, 1);
        // whether start play automatically after prepared, default value is 1
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 0);

        return new PLMediaPlayerImpl(getApplicationContext(), options);
    }
}

```
如果是直播就把isLiveStreaming改成1