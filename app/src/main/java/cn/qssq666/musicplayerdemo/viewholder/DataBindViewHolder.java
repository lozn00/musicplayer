package cn.qssq666.musicplayerdemo.viewholder;

import android.databinding.ViewDataBinding;
import android.view.View;

/**
 * Created by 情随事迁(qssq666@foxmail.com) on 2017/2/9.
 * 这样做会失去泛型
 */

public class DataBindViewHolder extends GenericDataBindViewHolder{

    public DataBindViewHolder(View itemView) {
        super(itemView);
    }

    public DataBindViewHolder(ViewDataBinding binding) {
        super(binding);
    }
}