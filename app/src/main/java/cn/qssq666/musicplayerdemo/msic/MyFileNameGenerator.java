package cn.qssq666.musicplayerdemo.msic;

import android.net.Uri;

import com.danikula.videocache.file.FileNameGenerator;

/**
 * Created by qssq on 2017/7/18 qssq666@foxmail.com
 */

public class MyFileNameGenerator implements FileNameGenerator {
    private static final String TAG = "MyFileNameGenerator";

    @Override
    public String generate(String url) {
        if (url != null) {
            Uri parse = Uri.parse(url);
            String lastPathSegment = parse.getLastPathSegment();
//            Log.w(TAG, "URL:" + url + "    " + lastPathSegment);
            return lastPathSegment;
        }
        return null;
    }
}
