package cn.qssq666.musicplayer.music;

import android.media.session.MediaController;

/**
 * Created by Administrator on 2015/9/7.
 */
public interface IMediaControllerConsumer {

    /**
     * 当服务绑定成功，mediaController对象生成的时候
     * @param mediaController
     */
    public void onObtainMediaController(MediaController mediaController);


    /**
     * 当服务解绑的时候...
     * @param mediaController
     */
    public void onReleasedMediaController(MediaController mediaController);
}
