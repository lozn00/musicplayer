package cn.qssq666.musicplayer.music;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Administrator on 2016/12/1.
 */

public class MediaInfoPublisher {


    /**
     * 在这里我可以用eventbus也可以直接回调。
     */
    public MediaInfoPublisher() {

    }

    ArrayList<IMediaControlCallBack> mMediaControlCallBacks = new ArrayList<>();

    /**
     * 内部维护就好 不需要让为毛在设置进来了
     *
     * @param mediaControlCallBacks
     */
    @Deprecated
    public MediaInfoPublisher(ArrayList<IMediaControlCallBack> mediaControlCallBacks) {
        this.mMediaControlCallBacks = mediaControlCallBacks;
    }

    public void publishProgressChangeEvent(long current) {
        for (IMediaControlCallBack callBack : mMediaControlCallBacks) {
            callBack.onProgressChnage(current);
        }
  /*      PlayInfoEvent event = new PlayInfoEvent();
        event.setTotalPosition(total);
        event.setTotalPosition(total);
        EventBus.getDefault().post(event);*/
    }

    public void publishPlayStateEvent(MusicData musicData, PLAYSTATE playstate) {
        for (int i = 0; i < mMediaControlCallBacks.size(); i++) {
            synchronized (mMediaControlCallBacks) {

                IMediaControlCallBack callBack = mMediaControlCallBacks.get(i);

                if (playstate == PLAYSTATE.MPS_PLAYING) {
                    callBack.onPlay(musicData);
                } else if (playstate == PLAYSTATE.MPS_PAUSE) {
                    callBack.onPause(musicData);
                } else if (playstate == PLAYSTATE.MPS_INVALID) {
                    callBack.onPlayError("");
                }
            }

        }
    }

 /*       MusicStateEvent event = new MusicStateEvent();
        event.setPlaystate(playstate);
        EventBus.getDefault().post(event);*/

    public void publishErrorPlayStateEvent(PLAYSTATE playstate, String error) {
        synchronized (mMediaControlCallBacks) {
            for (IMediaControlCallBack callBack : mMediaControlCallBacks) {
                callBack.onPlayError(error);
            }

        }
    }

    public void publishOnSeekToEvent(long seekTo) {
        synchronized (mMediaControlCallBacks) {
            for (IMediaControlCallBack callBack : mMediaControlCallBacks) {
                callBack.onSeekTo(seekTo);
            }

        }
    }


    public void publishOnPreEvent() {
        synchronized (mMediaControlCallBacks) {
            for (IMediaControlCallBack callBack : mMediaControlCallBacks) {
                callBack.onSkipToPrevious();
            }

        }
    }

    public void publishOnNextEvent() {
        synchronized (mMediaControlCallBacks) {

            for (IMediaControlCallBack callBack : mMediaControlCallBacks) {
                callBack.onSkipToNext();
            }
        }
    }

    public void publishTipMsg(String msg) {
        synchronized (mMediaControlCallBacks) {
            for (IMediaControlCallBack callBack : mMediaControlCallBacks) {
                callBack.onMsgTip(msg);
            }

        }
    }

    public void publishCacheChangeEvent(int percent) {
        synchronized (mMediaControlCallBacks) {
            for (IMediaControlCallBack callBack : mMediaControlCallBacks) {
                callBack.onCacheProgressChnage(percent);
            }

        }
    /*    CacheProgressEvent event = new CacheProgressEvent();
        event.setValue(percent);
        EventBus.getDefault().post(event);*/
    }

    public void pulishonPreparedEvent(int position) {
        synchronized (mMediaControlCallBacks) {
            for (IMediaControlCallBack callBack : mMediaControlCallBacks) {
                callBack.onPrepared(position);
            }
        }
      /*  CacheProgressEvent event = new CacheProgressEvent();
        event.setValue(percent);
        EventBus.getDefault().post(event);*/
    }

    public boolean removeMediaInfoCallBack(IMediaControlCallBack mediaInfoCallBack) {
        synchronized (mMediaControlCallBacks) {
            Iterator<IMediaControlCallBack> iterator = mMediaControlCallBacks.iterator();
            while (iterator.hasNext()) {  //执行过程中会执行数据锁定，性能稍差，若在循环过程中要去掉某个元素只能调用iter.remove()方法。
                IMediaControlCallBack currentControlCallBack = iterator.next();
                if (currentControlCallBack == mediaInfoCallBack) {
                    iterator.remove();
                    return true;
                }
            }
            return false;
        }
//            return mMediaControlCallBacks.remove(mediaInfoCallBack);
    }

    public boolean addMediaInfoCallBack(IMediaControlCallBack mediaInfoCallBack) {
        synchronized (mMediaControlCallBacks) {
            if (mMediaControlCallBacks.contains(mediaInfoCallBack)) {
                return false;
            }
            return mMediaControlCallBacks.add(mediaInfoCallBack);
        }
    }
}
