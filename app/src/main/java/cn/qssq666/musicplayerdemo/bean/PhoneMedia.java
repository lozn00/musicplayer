package cn.qssq666.musicplayerdemo.bean;

import cn.qssq666.musicplayer.music.MusicData;
import cn.qssq666.musicplayerdemo.interfaces.ShowModelI;

/**
 * Created by 情随事迁(qssq666@foxmail.com) on 2017/2/23.
 */

public class PhoneMedia   implements MusicData,ShowModelI {
    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    long duration;
    String path;
    String name;
    long size;

    public long getThumbNailsId() {
        return thumbNailsId;
    }

    public void setThumbNailsId(long thumbNailsId) {
        this.thumbNailsId = thumbNailsId;
    }

    public String getSizeFormat() {
        return sizeFormat;
    }

    public void setSizeFormat(String sizeFormat) {
        this.sizeFormat = sizeFormat;
    }

    String sizeFormat;
    long thumbNailsId;

    public String getThumbNailsPath() {
        return thumbNailsPath;
    }

    public void setThumbNailsPath(String thumbNailsPath) {
        this.thumbNailsPath = thumbNailsPath;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    long createtime;
    String thumbNailsPath;

    public String getLocalurl() {
        return getPath();
    }

    @Override
    public String getPlayUrl() {
        return getPath();
    }


    @Override
    public String getMusicTitle() {
        return getName();
    }

    @Override
    public int getMusicId() {
        return 0;
    }
}
