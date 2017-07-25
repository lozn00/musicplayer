package cn.qssq666.musicplayerdemo.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.qssq666.musicplayerdemo.R;

/**
 * Created by qssq on 2017/6/27 qssq666@foxmail.com
 */

public class TestViewHolder extends RecyclerView.ViewHolder {

    public final TextView textView;
    public final ImageView iv;

    public TestViewHolder(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(android.R.id.text1);
        iv = ((ImageView) itemView.findViewById(R.id.btn_play));
    }
}
