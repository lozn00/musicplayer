
 图片演示

![演示图片地址](https://github.com/qssq/musicplayer/blob/master/Pictures/1.gif)

##### 使用方法

gradle
```
 compile 'cn.qssq666:musicplayer:0.1'//回调duration由秒改成毫秒


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


demo中MusicServiceHelper代码
采用工厂模式轻松切换任意录制格式 RecordFactory类提供了5种录音姿势封装的演示


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

     
     
```

