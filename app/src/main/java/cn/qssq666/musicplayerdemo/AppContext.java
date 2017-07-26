package cn.qssq666.musicplayerdemo;

import android.app.Application;
import android.widget.Toast;

import com.danikula.videocache.HttpProxyCacheServer;

import java.util.ArrayList;
import java.util.List;

import cn.qssq666.musicplayerdemo.bean.NetMusicModel;
import cn.qssq666.musicplayerdemo.interfaces.ShowModelI;
import cn.qssq666.musicplayerdemo.msic.MyFileNameGenerator;

/**
 * Created by qssq on 2017/7/25 qssq666@foxmail.com
 */

public class AppContext extends Application {

    private static HttpProxyCacheServer httpProxyCacheServer;
    private static AppContext application_instance;
    private static Toast mToast;
    private static List<ShowModelI> defaultMusic;

    public static List<ShowModelI> getDefaultMusic() {

        return defaultMusic;
    }

    {
        defaultMusic = new ArrayList<>();//。
        //下面的gtimg.cn是本人从抓包的,帮助到你的给我打赏个，呜呜
        defaultMusic.add(new NetMusicModel("一生所爱", "http://data.5sing.kgimg.com/G034/M0A/12/00/Yg0DAFX19AiAX4JoAEIMQZQOFPs323.mp3"));
        defaultMusic.add(new NetMusicModel("老男孩", "http://96.ierge.cn/11/166/333403.mp3"));
//        defaultMusic.add(new NetMusicModel("Beyond光辉岁月", "http://m2.music.126.net/vAVwJScrrrPBo5LDZs7KFg==/1045635558023798.mp3"));
        defaultMusic.add(new NetMusicModel("全军出击", "http://imgcache.gtimg.cn/club/moblie/special_sound/x61_v_7.mp3"));
        defaultMusic.add(new NetMusicModel("恭喜发财", "http://imgcache.gtimg.cn/club/moblie/special_sound/x71-1.lit_v_4.mp3"));
        defaultMusic.add(new NetMusicModel("抢枪抢h", "http://imgcache.gtimg.cn/club/moblie/special_sound/x71-1.lit_v_4.mp3"));
        defaultMusic.add(new NetMusicModel("抢红包啦", "http://imgcache.gtimg.cn/club/moblie/special_sound/x75_v_3.mp3"));
        defaultMusic.add(new NetMusicModel("游戏声音", "http://imgcache.gtimg.cn/club/moblie/special_sound/88-11_v_3.mp3"));
        defaultMusic.add(new NetMusicModel("德玛西亚", "http://imgcache.gtimg.cn/club/moblie/special_sound/x59_v_7.mp3"));
        defaultMusic.add(new NetMusicModel("俺老孙来也", "http://imgcache.gtimg.cn/club/moblie/special_sound/x57_v_7.mp3"));
        defaultMusic.add(new NetMusicModel("俺老孙来资源文件", "android.resource://" + BuildConfig.APPLICATION_ID + "/" + R.raw.x57_v_7));
        defaultMusic.add(new NetMusicModel("德玛西亚也资源文件", "android.resource://" + BuildConfig.APPLICATION_ID + "/" + R.raw.x59_v_7));

    }

    @Override
    public void onCreate() {
        super.onCreate();
        application_instance = this;
        mToast = Toast.makeText(application_instance, "", Toast.LENGTH_SHORT);
    }

    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy() {
        httpProxyCacheServer = newProxy();
        return httpProxyCacheServer == null ? (httpProxyCacheServer = newProxy()) : httpProxyCacheServer;
    }

    private static HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(getInstance())

//                .diskUsage(new MyCoolDiskUsageStrategy())
                .maxCacheSize(1024 * 1024 * 512)       // 1 Gb for cache  512M
                .maxCacheFilesCount(20)
                .fileNameGenerator(new MyFileNameGenerator())
                .build();
//        return new HttpProxyCacheServer(getInstance());
    }

    public static AppContext getInstance() {
        return application_instance;
    }


    public static void showToast(String str) {
        mToast.setText("" + str);
        mToast.show();
    }

    public static void showToast(String str, int duration) {
        mToast.setText("" + str);
        mToast.setDuration(duration);
        mToast.show();


    }
}
