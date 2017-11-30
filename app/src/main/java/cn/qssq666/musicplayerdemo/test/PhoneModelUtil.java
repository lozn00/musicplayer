package cn.qssq666.musicplayerdemo.test;

import android.os.Build;

/**
 * 手机型号工具类
 * Created by Administrator on 2015/9/20 0020.
 */
public class PhoneModelUtil {
    public static String getPhoneModel(){
        return Build.BRAND + " " + Build.MODEL;
    }
    public static boolean isSamsungPhone() {
        if (getPhoneModel().contains("samsung")){
            return true;
        }
        return false;
    }
    public static boolean isHuaweiPhone() {
        if (getPhoneModel().contains("huawei") || getPhoneModel().contains("honour")){
            return true;
        }
        return false;
    }
}