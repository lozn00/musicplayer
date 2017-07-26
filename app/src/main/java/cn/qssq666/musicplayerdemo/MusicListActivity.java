package cn.qssq666.musicplayerdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import cn.qssq666.musicplayer.music.MusicData;
import cn.qssq666.musicplayerdemo.adapter.MyTestAadapter;
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



        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new MyTestAadapter();
        adapter.setData(AppContext.getDefaultMusic());
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
            int position = adapter.getData().indexOf(data);
            if (position >= 0) {
                adapter.setCurrentPosition(position);
                adapter.notifyDataSetChanged();

            }

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
