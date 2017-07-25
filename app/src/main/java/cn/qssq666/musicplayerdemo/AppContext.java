package cn.qssq666.musicplayerdemo;

import android.app.Application;
import android.widget.Toast;

import com.danikula.videocache.HttpProxyCacheServer;

import cn.qssq666.musicplayerdemo.msic.MyFileNameGenerator;

/**
 * Created by qssq on 2017/7/25 qssq666@foxmail.com
 */

public class AppContext extends Application {

    private static HttpProxyCacheServer httpProxyCacheServer;
    private static AppContext application_instance;
    private static Toast mToast;

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
