package cn.qssq666.musicplayerdemo.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import cn.qssq666.musicplayerdemo.adapter.MyTestAadapter;
import cn.qssq666.musicplayerdemo.msic.CachePlayService;


/**
 * Created by qssq on 2017/6/27 qssq666@foxmail.com
 */

public class TestUtils {
    public static void initTestVerticalData(RecyclerView recyclerView) {
        initAdapter(recyclerView, LinearLayout.VERTICAL);

    }

    public static void initTestHorzontalData(RecyclerView recyclerView) {
        initAdapter(recyclerView, LinearLayout.HORIZONTAL);

    }

    public static void initAdapter(RecyclerView recyclerView, int orientation) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), orientation, false));
        recyclerView.setAdapter(new MyTestAadapter());

    }

    public static Intent getMusicService(Context context) {
        Intent intent = new Intent(context, CachePlayService.class);
//        Intent intent = new Intent(context, PlayService.class);
        return intent;
    }
}
