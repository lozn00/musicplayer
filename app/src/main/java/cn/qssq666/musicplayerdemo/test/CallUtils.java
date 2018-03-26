package cn.qssq666.musicplayerdemo.test;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;

/**
 * http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/1007/3548.html
 * Created by qssq on 2017/11/30 qssq666@foxmail.com
 */

public class CallUtils {

    private static final String TAG = "CallUtils";

    /**
     * 设置扬声器开关
     *
     * @param context
     * @param enable
     */
    public static void setSpeakerphoneOn(Context context, boolean enable) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (enable) {
//            audioManager.setMode(AudioManager.MODE_NORMAL);
//            audioManager.setSpeakerphoneOn(true);
            changeToSpeaker(audioManager);
        } else {

//            audioManager.setSpeakerphoneOn(false);//关闭扬声器
            //把声音设定成Earpiece（听筒）出来，设定为正在通话中
//            audioManager.setMode(AudioManager.MODE_IN_CALL);
            changeToReceiver(audioManager);
        }

    }


    /**
     * 切换到听筒模式 实际上音量更大了..
     */
    public static void changeToEarpieceMode(AudioManager audioManager) {
        Log.w(TAG,"changeToEarpieceMode");
        audioManager.setSpeakerphoneOn(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                    audioManager.getStreamMaxVolume(AudioManager.MODE_IN_COMMUNICATION), AudioManager.FX_KEY_CLICK);
        } else {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                    audioManager.getStreamMaxVolume(AudioManager.MODE_IN_CALL), AudioManager.FX_KEY_CLICK);
        }
    }

    /**
     * 切换到耳机模式
     */
    public  static void changeToHeadsetMode(AudioManager audioManager) {
        audioManager.setSpeakerphoneOn(false);
    }

    /**
     * 切换到外放模式
     */
    public static void changeToSpeakerMode(AudioManager audioManager) {
        audioManager.setSpeakerphoneOn(true);
    }

    public static void resetPlayMode(AudioManager audioManager) {
        /**
         * 判断耳机是否已经插入
         */
        if (audioManager.isWiredHeadsetOn()) {
            changeToHeadsetMode(audioManager);
        } else {
            changeToSpeakerMode(audioManager);
        }
    }

    /**
     * 调大音量
     */
    public  static void raiseVolume(AudioManager audioManager) {
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (currentVolume < audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_RAISE, AudioManager.FX_FOCUS_NAVIGATION_UP);
        }
    }

    /**
     * 调小音量
     */
    public  static void lowerVolume(AudioManager audioManager) {
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (currentVolume > 0) {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_LOWER, AudioManager.FX_FOCUS_NAVIGATION_UP);
        }
    }


    /**
     * 切换到外放
     */
    public static void changeToSpeaker(AudioManager audioManager) {
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(true);
    }

    /**
     * 切换到耳机模式 荣耀8 没带耳机听不到，没给权限则和没修改一样 <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
     */
    public static void changeToHeadset(AudioManager audioManager) {
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setSpeakerphoneOn(false);//需要权限 否则无效 但是发现声音都没有了
    }

    /**
     * 切换到听筒 经验证在华为的某些机型中,设置MODE_IN_CALL根本不起作用.
     */
    public static void changeToReceiver(AudioManager audioManager) {
//        audioManager.setSpeakerphoneOn(false);
//        audioManager.setMode(AudioManager.MODE_CURRENT);
            audioManager.setSpeakerphoneOn(false);
        if (Build.VERSION.SDK_INT >= 11) {
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } else {
            audioManager.setMode(AudioManager.MODE_IN_CALL);
        }
    }



/*    private void setRi(Context context,boolean speakerphoneOn) {


        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock1 = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, this.getClass().getName());
        SensorManager senserManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        senserManager.registerListener(new SensorEventListener() {
                                           @Override
                                           public void onSensorChanged(SensorEvent event) {
                                               float[] dis = event.values;
                                               if (0.0f == dis[0]) { // 靠近身体
                                                   wakeLock.release(); // 熄灭屏幕
                                                   switchToEarpiece(); // 切换到听筒
                                               } else {
                                                   wakeLock.acquire(); // 点亮屏幕
                                                   switchToSpeaker(); // 切换到扬声器
                                               }
                                           }

                                           @Override
                                           public void onAccuracyChanged(Sensor sensor, int accuracy) {

                                           }
                                       },
                //http://blog.csdn.net/monkey_z_/article/details/50554038
                senserManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);

     *//*   if (!speakerphoneOn){
            mAudioManager.setSpeakerphoneOn(false);
            mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        }else {
            mAudioManager.setMode(AudioManager.MODE_NORMAL);
            mAudioManager.setSpeakerphoneOn(true);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }*//*

    }*/


}
