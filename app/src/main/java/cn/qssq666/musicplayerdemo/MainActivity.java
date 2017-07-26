package cn.qssq666.musicplayerdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import cn.qssq666.musicplayer.music.MusicData;
import cn.qssq666.musicplayerdemo.msic.MusicServiceHelper;
import cn.qssq666.musicplayerdemo.utils.TestUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Intent musicService;
    private MusicServiceHelper instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_to_play_Page).setOnClickListener(this);
        findViewById(R.id.btn_to_play_list).setOnClickListener(this);
        musicService = TestUtils.getMusicService(this);
        instance = MusicServiceHelper.getInstance(this, new MusicServiceHelper.OnMusicHelperBaceListener() {
            @Override
            public void onPause(MusicData data) {

            }

            @Override
            public void onPlayErr(String str, MusicData data) {

            }

            @Override
            public void onPlay(MusicData data) {

            }

            @Override
            public void onBindService() {

            }
        });
        this.startService(musicService);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_to_play_list: {


                Intent intent = new Intent(this, MusicListActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.btn_to_play_Page:

            {
                if (instance.getPlaybinder() != null) {
                    List<? extends MusicData> musicList = instance.getPlaybinder().getMusicList();
                    if (musicList == null || musicList.isEmpty()) {
                        List defaultMusic = AppContext.getDefaultMusic();
                        instance.setMusicList(defaultMusic);
                        Toast.makeText(this, "第一次创建默认歌单,请点击右边菜单选择默认歌单进行播放", Toast.LENGTH_SHORT).show();
                    }
                }

                Intent intent = new Intent(this, MusicActivity.class);
                startActivity(intent);
            }

            break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance.destory();
        this.stopService(musicService);
    }
}
