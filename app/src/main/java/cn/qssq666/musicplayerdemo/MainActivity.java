package cn.qssq666.musicplayerdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cn.qssq666.musicplayerdemo.utils.AutoUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Intent musicService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_to_play_Page).setOnClickListener(this);
        findViewById(R.id.btn_to_play_list).setOnClickListener(this);
        musicService = AutoUtils.getMusicService(this);
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
                Intent intent = new Intent(this, MusicActivity.class);
                startActivity(intent);
            }

            break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.stopService(musicService);
    }
}
