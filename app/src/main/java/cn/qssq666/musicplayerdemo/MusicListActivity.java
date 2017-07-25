package cn.qssq666.musicplayerdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.qssq666.musicplayer.music.MusicData;
import cn.qssq666.musicplayerdemo.adapter.MyTestAadapter;
import cn.qssq666.musicplayerdemo.bean.NetMusicModel;
import cn.qssq666.musicplayerdemo.interfaces.OnItemClickListener;
import cn.qssq666.musicplayerdemo.interfaces.ShowModelI;
import cn.qssq666.musicplayerdemo.msic.MusicServiceHelper;

/**
 * Created by qssq on 2017/7/25 qssq666@foxmail.com
 */

public class MusicListActivity extends AppCompatActivity {
    private MusicServiceHelper musicServiceHelper;
    private RecyclerView recyclerView;
    private MyTestAadapter adapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        recyclerView = ((RecyclerView) findViewById(R.id.recyclerview));
        musicServiceHelper = MusicServiceHelper.getInstance(this, onMusicHelperBaceListener, this);

        final ArrayList list = new ArrayList<>();//铃声地址各位自己弄吧。
        list.add(new NetMusicModel("一生所爱", "http://web.kugou.com/?actWion=single&filename=%u9093%u7D2B%u68CB__-__%u9A7F%u52A8%u7684%u5FC3&hash=3b676fd9d340c0a7382f273caa64ed87&timelen=218000μblog=1&chl=kugou"));
        list.add(new NetMusicModel("老男孩", " http://m2.music.126.net/T1NwCCn2vb-ZR6x6obojkQ==/6645448279772928.mp3"));



        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new MyTestAadapter();
        adapter.setData(list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Object obj, View view, int position) {
                List<ShowModelI> data = adapter.getData();
                List dataNew = data;
                if (!musicServiceHelper.isCurrentControlList(dataNew)) {
                    musicServiceHelper.setMusicList(dataNew);
                }
                ShowModelI showModelI = adapter.getData().get(position);
                adapter.setCurrentPosition(position);
                adapter.notifyDataSetChanged();
                musicServiceHelper.onMusicPositionClick(position, showModelI);

            }
        });

//        musicStation.createPlayStationAndSHow();


    }

    MusicServiceHelper.OnMusicHelperBaceListener onMusicHelperBaceListener = new MusicServiceHelper.OnMusicHelperBaceListener() {
        @Override
        public void onPause(MusicData data) {
            adapter.setCurrentPosition(-1);
            adapter.notifyDataSetChanged();

        }

        @Override
        public void onPlayErr(String str, MusicData data) {
            adapter.setCurrentPosition(-1);
            adapter.notifyDataSetChanged();
            Toast.makeText(MusicListActivity.this, "" + str, Toast.LENGTH_SHORT).show();
        }

        @Override

        public void onPlay(MusicData data) {

        }

        @Override
        public void onBindService() {

        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        musicServiceHelper.destory();
    }
}
