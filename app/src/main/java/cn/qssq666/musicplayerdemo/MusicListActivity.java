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

        final ArrayList list = new ArrayList<>();
        //http://api.buyao.tv/topic/201704/20170418173230.mp3"音乐地址
        list.add(new NetMusicModel("test", "http://api.buyao.tv/topic/201704/20170418173230.mp3"));
        list.add(new NetMusicModel("老男孩", "http://sc.111ttt.com/up/mp3/239897/A6DD6C3334D173D79F76592AC0B8D02D.mp3"));
        list.add(new NetMusicModel("平凡之路", " http://sc1.111ttt.com/2014/1/09/24/2242313311.mp3"));
        list.add(new NetMusicModel("说书人", " http://sc.111ttt.com/up/mp3/150826/94C648CCAABB88287EF299C45AC8A636.mp3"));
        list.add(new NetMusicModel("雨花石", " http://sc.111ttt.com/up/mp3/312428/4CB74F7E4720D5C8FB3922591DAEC514.mp3"));


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
