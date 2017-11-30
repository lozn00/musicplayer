package cn.qssq666.musicplayerdemo.test;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;


/**
 * http://www.jianshu.com/p/7246e65cd225
 * Created by qssq on 2017/11/30 qssq666@foxmail.com
 */

public class CallSensorManager {
    private static final String TAG = "CallSensorManager";
    /**
     * 屏幕锁有一个机制，在设置引用计数的情况下(wakeLock.setReferenceCounted(true)其实系统默认的就是true),这时候wakeLock.acquire()和wakeLock.release()是需要成对出现的，也就是说两个方法的调用次数要相同，否则wakeLock就不能释放，影响正常的操作。如果wakeLock.setReferenceCounted(false)，则不启用引用计数，无论你调用了多少次wakeLock.acquire()，只需要一个wakeLock.release()就可以释放屏幕锁。
     * <p>
     * <p>
     * 作者：斯帕罗 http://www.jianshu.com/p/49e8aa8eb3e9
     * 链接：http://www.jianshu.com/p/7246e65cd225
     * 來源：简书
     * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
     */

    private SensorManager senserManager;
    private SensorEventListener sensorEventListener;
    //    private PowerManager.WakeLock wakeLock;
    private AudioManager audioManager;
    private Sensor sensor;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;


    public interface OnFaceStickListener {
        public void onStickFace();

        public void onLeaveFace();
    }

    public OnFaceStickListener getOnFaceStickListener() {
        return onFaceStickListener;
    }

    public void setOnFaceStickListener(OnFaceStickListener onFaceStickListener) {
        this.onFaceStickListener = onFaceStickListener;
    }

    OnFaceStickListener onFaceStickListener;

    /*
    PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
boolean screen = pm.isScreenOn();
if(!screen){//如果灭屏
  //相关操作
}
     */
//http://blog.csdn.net/pz0605/article/details/51477455
    public void onCreate(final Context context) {


        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } else {
            audioManager.setMode(AudioManager.MODE_IN_CALL);
        }
        audioManager.setSpeakerphoneOn(true);            //默认为扬声器播放

        powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getName());
//        wakeLock.setReferenceCounted(true); // 设置不启用引用计数
        senserManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float[] dis = event.values;
                Log.w(TAG, "dis" + dis[0]);
           /*     if (0.0f == dis[0]) { // 靠近身体

                    blackScreenLogic(context);
//                                                   switchToEarpiece(); // 切换到听筒


                } else {
                    brightScreenLogic(context);

                }
*/


                float value = event.values[0];
                if (value == sensor.getMaximumRange()) {
                    Log.w(TAG, "远离距离感应器,传感器的值:" + value);
                } else {
                    Log.w(TAG, "靠近距离感应器,传感器的值:" + value);
                }
//                if (playerManager.isPlaying()){
                if (value == sensor.getMaximumRange()) {
                    CallUtils.changeToSpeakerMode(audioManager);
                    setScreenOn();
                } else {
//                    CallUtils.changeToHeadset(audioManager);
//                    CallUtils.changeToReceiver(audioManager);
                    audioManager.setMode(AudioManager.MODE_IN_CALL);
                    audioManager.setSpeakerphoneOn(true);
//                    CallUtils.changeToEarpieceMode(audioManager);
                    setScreenOff();
                }
           /*     } else {
                    if(value == sensor.getMaximumRange()){
                        playerManager.changeToSpeakerMode();
                        setScreenOn();
                    }
                }*/

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensor = senserManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        senserManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);

//        setBlackscreen(true);//要先调用两瓶才能调用熄灭屏幕
//        brightScreenLogic(context);
    }

    private void setScreenOff() {
        Log.w(TAG, "setScreenOff");
        if (wakeLock == null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, TAG);
        }
        wakeLock.acquire();
    }


    private void setScreenOn() {
        Log.w(TAG, "setScreenOn");
        if (wakeLock != null) {
            wakeLock.setReferenceCounted(false);
            wakeLock.release();
            wakeLock = null;
        }
    }


    private void blackScreenLogic(Context context) {
        if (!blackscreen) {
            Log.w(TAG, "熄灭屏幕");
            blackscreen = true;

            wakeLock.release(); // 熄灭屏幕
            CallUtils.setSpeakerphoneOn(context, false);
            if (onFaceStickListener != null) {
                onFaceStickListener.onStickFace();
            }
        }
    }

    private void brightScreenLogic(Context context) {
        if (blackscreen) {
            Log.w(TAG, "点亮屏幕");
            blackscreen = false;
            if (onFaceStickListener != null) {
                onFaceStickListener.onLeaveFace();
            }
            wakeLock.acquire(); // 点亮屏幕
            CallUtils.setSpeakerphoneOn(context, true);
//                                                   switchToSpeaker(); // 切换到扬声器
        }
    }

    public void setBlackscreen(boolean blackscreen) {
        this.blackscreen = blackscreen;
    }

    boolean blackscreen = false;

    public void onDestory() {
        if (senserManager != null) {
       /*     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                senserManager.unregisterDynamicSensorCallback();
            }*/
            senserManager.unregisterListener(sensorEventListener);
        }
    }


    public void switchSpeakerphoneOn(boolean speakerphoneOn, AudioManager mAudioManager, MediaPlayer mMediaPlayer) {
        if (!speakerphoneOn) {
            mAudioManager.setSpeakerphoneOn(false);
            mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        } else {
            mAudioManager.setMode(AudioManager.MODE_NORMAL);
            mAudioManager.setSpeakerphoneOn(true);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }

    }

}
