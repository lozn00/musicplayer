package cn.qssq666.musicplayerdemo.interfaces;

import java.util.List;

/**
 * Created by 情随事迁(qssq666@foxmail.com) on 2017/3/10.
 */

public interface AdapterI<MODEL> {
    void notifyDataSetChanged();

    void appendModels(List<MODEL> models);

    void setData(List<MODEL> data);

    List<MODEL> getData();
}
