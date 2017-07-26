package cn.qssq666.musicplayerdemo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.qssq666.musicplayerdemo.R;
import cn.qssq666.musicplayerdemo.interfaces.ShowModelI;
import cn.qssq666.musicplayerdemo.viewholder.TestViewHolder;


/**
 * Created by qssq on 2017/6/27 qssq666@foxmail.com
 */

public class MyTestAadapter extends BaseRecyclervdapter<ShowModelI, TestViewHolder> {


    public MyTestAadapter() {
    }


    @Override
    public TestViewHolder onCreateViewHolderByExtend(ViewGroup parent, int viewType) {
        return new TestViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_text, parent, false));
    }

    @Override
    public void onBindViewHolder(TestViewHolder holder, final int position) {
        holder.textView.setText("" + getData().get(position).getMusicTitle());

        holder.iv.setVisibility(currentPosition == position ? View.VISIBLE : View.GONE);


    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    private int currentPosition=-1;
}
