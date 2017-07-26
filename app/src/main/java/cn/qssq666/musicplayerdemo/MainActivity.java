package cn.qssq666.musicplayerdemo;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.qssq666.musicplayer.music.MusicData;
import cn.qssq666.musicplayerdemo.bean.NetMusicModel;
import cn.qssq666.musicplayerdemo.bean.PhoneMedia;
import cn.qssq666.musicplayerdemo.interfaces.INotify;
import cn.qssq666.musicplayerdemo.msic.MusicServiceHelper;
import cn.qssq666.musicplayerdemo.msic.QssqTask;
import cn.qssq666.musicplayerdemo.utils.DialogUtils;
import cn.qssq666.musicplayerdemo.utils.TestUtils;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Intent musicService;
    private MusicServiceHelper instance;
    private boolean mCancel;
    private final static String TAG = "MainActivity";
    private TextView tvGithub;
    private EditText evTitle;
    private EditText evUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_to_play_Page).setOnClickListener(this);
        findViewById(R.id.btn_scan_local_music).setOnClickListener(this);
        evTitle = ((EditText) findViewById(R.id.ev_title));
        evUrl = ((EditText) findViewById(R.id.ev_url));
        findViewById(R.id.btn_add_music).setOnClickListener(this);
        tvGithub = (TextView) findViewById(R.id.btn_github);
        tvGithub.setOnClickListener(this);
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
//                scanLocalMusic();
            }

        });
        this.startService(musicService);
    }

    private void toMusicDetail() {
        Intent intent = new Intent(this, MusicActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_github:
                ActionEngine.toWebView(this, tvGithub.getText().toString());
                Toast.makeText(this, "记得star哦", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_to_play_list: {

                toList();

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
                toMusicDetail();

            }

            break;
            case R.id.btn_scan_local_music:
                scanLocalMusic();
                break;
            case R.id.btn_add_music:
                ArrayList list = null;
                if (instance.getPlaybinder().getMusicList() == null) {
                    list = new ArrayList();
                    instance.getPlaybinder().setMusicList(list);
                } else {
                    list = (ArrayList) instance.getPlaybinder().getMusicList();
                }
                NetMusicModel model = new NetMusicModel();
                model.setTitle(evTitle.getText().toString());
                if (TextUtils.isEmpty(evUrl.getText().toString())) {
                    Toast.makeText(this, "数据为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                model.setUrl(evUrl.getText().toString());
                synchronized (list) {
                    list.add(model);
                }

                break;
        }
    }

    private void toList() {
        Intent intent = new Intent(this, MusicListActivity.class);
        startActivity(intent);
    }

    private void scanLocalMusic() {

        final ProgressDialog dialog = DialogUtils.getProgressDialog(this);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mCancel = true;
            }
        });
        dialog.setTitle("正在查询");
        dialog.show();
        new QssqTask<ArrayList<PhoneMedia>>(new QssqTask.ICallBackImp<List>() {
            @Override
            public List onRunBackgroundThread() {
                ContentResolver contentResolver = AppContext.getInstance().getContentResolver();
                Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, (MediaStore.Audio.Media.DEFAULT_SORT_ORDER));
//                Cursor cursor = AppContext.getInstance().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
                int start = 1;
                if (cursor.getCount() > 0) {
                    ArrayList<PhoneMedia> arrayList = new ArrayList<PhoneMedia>();
                    while (!mCancel && cursor.moveToNext()) {
                        getQssqTask().publishProgressProxy(start++);
                        PhoneMedia media = new PhoneMedia();
//                        String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));//作者
                        int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                        media.setDuration(duration);
                        String tilte = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                        media.setName(tilte);
                        String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                        media.setPath(url);
                        try {
                            media.setCreatetime(new File(url).lastModified());

                        } catch (Exception e) {
                            Log.e(TAG, Log.getStackTraceString(e));
                        }
                        media.setSize(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)));

                        long thumbNailsId = cursor.getLong(cursor.getColumnIndex("_ID"));
                        media.setThumbNailsId(thumbNailsId);
                        String fileSizeStr = Formatter.formatFileSize(AppContext.getInstance(), media.getSize());
                        media.setSizeFormat(fileSizeStr);

//                        music.setName();
                        arrayList.add(media);
                    }
                    return arrayList;
                }
                return null;
            }

            @Override
            public void onProgressUpdate(Object o) {
                dialog.setMessage("正在处理第" + o + "条数据");
            }

            @Override
            public void onRunFinish(List o) {
                if (MainActivity.this.isFinishing()) {
                    return;
                }
                dialog.dismiss();
                if (o == null) {
                    Toast.makeText(MainActivity.this, "本地歌曲都没有,你竟然没有音乐爱好？", Toast.LENGTH_SHORT).show();
                } else {
                    DialogUtils.showConfirmDialog(MainActivity.this, "扫描歌曲完成,共" + o.size() + "首,是否跳转到播放器界面(右边菜单可以切换歌单)", new INotify<Void>() {
                        @Override
                        public void onNotify(Void param) {
                            toMusicDetail();
                        }
                    });
                    instance.setMusicList((List<? extends MusicData>) o);

                }
            }
        }).execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance.destory();
        this.stopService(musicService);
    }
}
