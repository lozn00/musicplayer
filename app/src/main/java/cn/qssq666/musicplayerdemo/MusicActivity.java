package cn.qssq666.musicplayerdemo;

import android.app.Dialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import cn.qssq666.musicplayer.music.IMediaControlCallBack;
import cn.qssq666.musicplayer.music.MusicData;
import cn.qssq666.musicplayer.music.PlayService;
import cn.qssq666.musicplayerdemo.adapter.MusicDialogAdapter;
import cn.qssq666.musicplayerdemo.bean.LocalMusicModel;
import cn.qssq666.musicplayerdemo.bean.PhoneMedia;
import cn.qssq666.musicplayerdemo.databinding.ActivityMusicDetailBinding;
import cn.qssq666.musicplayerdemo.databinding.DialogMusicListBinding;
import cn.qssq666.musicplayerdemo.interfaces.OnItemClickListener;
import cn.qssq666.musicplayerdemo.interfaces.OnItemLongClickListener;
import cn.qssq666.musicplayerdemo.interfaces.ShowModelI;
import cn.qssq666.musicplayerdemo.msic.MediaController;
import cn.qssq666.musicplayerdemo.msic.PlayActionCallBack;
import cn.qssq666.musicplayerdemo.utils.TestUtils;


/**
 * Created by luozheng on 2017/2/10.  qssq.space
 * TODO
 */

public class MusicActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MusicActivity";
    private MusicDialogAdapter adapterDialog;
    private Pair<DialogMusicListBinding, Dialog> dialogMenuPair;
    private Intent musicService;

    /**
     * 在绑定之后和onPre都可能被同时加载
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = DataBindingUtil.setContentView(this, R.layout.activity_music_detail);

        musicService = TestUtils.getMusicService(this);
        MusicActivity.this.bindService(musicService, mPlayconn, Service.BIND_AUTO_CREATE);

        mBind.ivSwitchMode.setOnClickListener(this);
//                    mSnackBarContainer=contentView.findViewById(android.R.id.content);

        //初始值
        mBind.mediacontrollerProgress.setMax(1000);
        mBind.mediacontrollerProgress.setThumbOffset(1);
        mBind.mediacontrollerProgress.setOnSeekBarChangeListener(mPopSeekListener);
        mBind.ivSwitchMode.setOnClickListener(this);
        mBind.pause.setOnClickListener(this);
        mBind.icPre.setOnClickListener(this);
        mBind.icNext.setOnClickListener(this);
        mBind.ivMusicList.setOnClickListener(this);
//        EventBus.getDefault().register(this);


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaControlCallBack != null && mPlaybinder != null) {
            mPlaybinder.removeMediaInfoCallBack(mMediaControlCallBack);
        }
        this.unbindService(mPlayconn);
    }


    private ActivityMusicDetailBinding mBind;


    private boolean mPopSeekBarTouching;
    private SeekBar.OnSeekBarChangeListener mPopSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (mPopSeekBarTouching) {//额 我发现多余了有 一个fromUser
                final long newposition = (mPlayActionCallBack.getTotalTime() * progress) / 1000;

                mPlayActionCallBack.seekTo((int) newposition);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mPopSeekBarTouching = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mPopSeekBarTouching = false;
        }
    };


    public void updateOrInitPlayMode(boolean isPersonClick) {
        int resources = R.mipmap.ic_launcher;
        PlayService.PLAYMODE playMode = mPlayActionCallBack.getPlayMode();
        String result = "";
        switch (playMode) {
            case SIMPLE_LOOP:
                resources = R.drawable.pageplaying_btn_radio_single_loop;
                result = "单曲循环";
                break;
            case RANDOM_PLAY:
                resources = R.drawable.pageplaying_btn_radio_random;
                result = "随机播放";
                break;
            case LIST_PLAY:
                resources = R.drawable.pageplaying_btn_radio_loop;
                result = "列表播放";
                break;
            case LIST_LOOP:
                resources = R.drawable.pageplaying_btn_radio_loop;
                result = "列表循环";
                break;
        }
        result = "已切换到" + result;
        if (isPersonClick) {
            AppContext.showToast(result);
//            Snackbar snackbar = Snackbar.make(mSnackBarContainer, result, Snackbar.LENGTH_LONG).setActionTextColor(getResources().getColor(R.color.colorWhite));
            //center_vertical|left|start
       /*     ((TextView) snackbar.getView().findViewById(R.id.snackbar_text)).setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            snackbar.getView().setBackgroundColor(AppContext.getInstance().getResources().getColor(R.color.color_Black_half));

            snackbar.createPlayStationAndSHow();*/
        }
        mBind.ivSwitchMode.setImageResource(resources);
    }

    public void updateCacheProgress(int percent) {
        if (percent > 0) {
            percent = percent * 10;
        }
        mBind.mediacontrollerProgress.setSecondaryProgress(percent);//最大值是1000 所以需要*10
    }

    /**
     * 更新当前时间
     *
     * @param currentPosition
     */
    public void updatePopProgressBarAndCurrentPlayTime(long currentPosition) {
        /**
         * 在 刚弹出来的时候可能时间没获取到但是不初始化的话对于正在播放的又没法初始化
         */
        if (currentPosition > 0 && mPlayActionCallBack.getTotalTime() > 0) {
            mBind.mediacontrollerProgress.setProgress((int) (1000L * currentPosition / mPlayActionCallBack.getTotalTime()));
            mBind.timeCurrent.setText("" + MediaController.generateTime(currentPosition));

        }
//        mBind.mediacontrollerProgress.setProgress((int) (1000L * mPlaybinder.getCurrentDuration() / mPlaybinder.getDuration()));
    }

    public void updateTotalTime(int totalTime) {
        mBind.totaltime.setText("" + MediaController.generateTime(totalTime));
    }

    public void updateTitle(String title) {
        mBind.tvTitle.setText("" + title);

    }


    public void setIsPause() {
        mBind.pause.setImageResource(R.drawable.pageplaying_btn_radio_stoping);
//        mBind.ivPopBigImg.clearAnimation();
    }

    public void setIsPlaying() {

        mBind.pause.setImageResource(R.drawable.pageplaying_btn_radio_playing);
//        mBind.ivPopBigImg.startAnimation(loopRolateAnim);
    }


    @Override
    public void onClick(View v) {


        switch (v.getId()) {

            case R.id.iv_music_list:
                if (mPlaybinder.getMusicList() == null || mPlaybinder.getMusicList().isEmpty()) {
                    Toast.makeText(this, "播放列表为空!", Toast.LENGTH_SHORT).show();
                }
                //初始化对话框适配器
                if (adapterDialog == null) {
                    if (mPlaybinder.getFirstModel() instanceof ShowModelI) {


                        MusicDialogAdapter adapter = new MusicDialogAdapter();
                        adapter.setPlayposition(mPlaybinder.getPlayListPosition());
                        adapterDialog = adapter;
                        adapterDialog.setData((List) mPlaybinder.getMusicList());
                    } else {
                        Toast.makeText(this, "error type 请务必实现ShowModelI", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                //初始化对话框dialog
                if (dialogMenuPair == null) {

                    dialogMenuPair = ActionEngine.generateMusicMenuDialog(this, mPlaybinder.getPlayMode().getName(), adapterDialog);

                    adapterDialog.setOnItemClickListener(new OnItemClickListener() {
                                                             @Override
                                                             public void onItemClick(Object Object, View view, int position) {
                                                                 if (!mPlaybinder.playPositionIsVolid(position)) {
                                                                     mPlaybinder.play(position);
                                                                 } else {
                                                                     Toast.makeText(MusicActivity.this, "data_position_err", Toast.LENGTH_SHORT).show();
                                                                 }
                                                             }
                                                         }

                    );
                    adapterDialog.setOnItemLongClickListener(new OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(ViewGroup parent, View view, int position) {
                            ShowModelI showModelI = adapterDialog.getData().get(position);
                            if(showModelI instanceof PhoneMedia){
                                File file = new File(((PhoneMedia) showModelI).getPath());
                                if (!file.exists()) {
                                    return false;
                                }
                                MusicActivity.this.startActivity(ActionEngine.getShareFileIntent(file));
                                return true;
                            }
                            return false;
                        }
                    });
                }

                dialogMenuPair.second.show();
                DialogMusicListBinding binding = dialogMenuPair.first;
                if (binding != null && binding.tvPlayMode != null) {
                    binding.tvPlayMode.setText(String.format("%s (%d)", mPlaybinder.getPlayMode().getName(), adapterDialog.getItemCount()));
                }


                break;
            case R.id.iv_switch_mode:
                if (interceptClick()) {
                    return;
                }
                PlayService.PLAYMODE playMode = mPlayActionCallBack.getPlayMode();
                switch (playMode) {
                    case LIST_PLAY:
                        mPlayActionCallBack.setPlayMode(PlayService.PLAYMODE.LIST_LOOP);
                        break;
                    case LIST_LOOP:
                        mPlayActionCallBack.setPlayMode(PlayService.PLAYMODE.RANDOM_PLAY);
                        break;
                    case RANDOM_PLAY:
                        mPlayActionCallBack.setPlayMode(PlayService.PLAYMODE.SIMPLE_LOOP);
                        break;
                    case SIMPLE_LOOP:
                        mPlayActionCallBack.setPlayMode(PlayService.PLAYMODE.LIST_LOOP);//删除列表播放模式 boss不需要 所以直接跳转到 列表循环。
//                        mPlayActionCallBack.setPlayMode(PlayService.PLAYMODE.LIST_PLAY);
                        break;
                }
                this.updateOrInitPlayMode(true);
                break;
            case R.id.ic_pre:
                if (interceptClick()) {
                    return;
                }
                resetPopBtn();

                if (mPlayActionCallBack.playPre()) {
                    MusicData currentMusicData = mPlayActionCallBack.getCurrentMusicData();
                    if (currentMusicData instanceof ShowModelI) {
                        ShowModelI musicModelI = (ShowModelI) currentMusicData;
                        mBind.tvTitle.setText(musicModelI.getMusicTitle() + "");
                    }
                }

                break;
            case R.id.pause:
                if (interceptClick()) {
                    return;
                }
                mPlayActionCallBack.pauseOrPlay();
                break;
            case R.id.ic_next:
                if (interceptClick()) {
                    return;
                }
                resetPopBtn();
                if (mPlayActionCallBack.playNext()) {//虽然会设置2边但是给人的体验是好的。
                    MusicData currentMusicData = mPlayActionCallBack.getCurrentMusicData();
                    if (currentMusicData instanceof LocalMusicModel) {
                        ShowModelI musicModelI = (ShowModelI) currentMusicData;
                        mBind.tvTitle.setText(musicModelI.getMusicTitle() + "");
                    }
                }
                break;
        }
    }

    private boolean interceptClick() {
        if (mPlaybinder.getCurrentModel() == null) {
            Toast.makeText(this, "not music data!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }


    private void resetPopBtn() {
//        mBind.ivPopBigImg.clearAnimation();
        mBind.timeCurrent.setText("00:00");
        mBind.mediacontrollerProgress.setProgress(0);
        mBind.pause.setImageResource(R.drawable.pageplaying_btn_radio_playing);
    }


    private PlayService.MediaControlBinder mPlaybinder;
    private ServiceConnection mPlayconn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof PlayService.MediaControlBinder) {
                mPlaybinder = ((PlayService.MediaControlBinder) service);
                mPlaybinder.addMediaInfoCallBack(mMediaControlCallBack);
                updateOrInitPlayMode(false);
                updateMusicViewState(mPlaybinder.getPlayListPosition());
                if (mPlaybinder.isPlaying()) {
                    setIsPlaying();
                } else {
                    setIsPause();
                }

                MusicData currentModel = mPlaybinder.getCurrentModel();


            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    IMediaControlCallBack mMediaControlCallBack = new IMediaControlCallBack() {

        @Override
        public void onPlay(MusicData musicData) {
            setIsPlaying();
            updateMusicDialogList(mPlaybinder.getPlayListPosition());

        }

        @Override
        public void onPlayError(String str) {
            updateMusicDialogList(-1);
            Log.d(TAG, "onPlayError");
//            mIvPlayPause.stopAnim();
            setIsPause();
            Log.e(TAG, "播放错误" + str);
            AppContext.showToast( "播放错误" + str);
//            onLoadFail("播放失败");
          /*  if (mPlaybinder.isCurrentControlList(adapter.getData())) {
                adapter.setPlayposition(-1);
                adapter.notifyDataSetChanged();
            }*/
        }

        @Override
        public void onPauseProgress() {
            Log.d(TAG, "onPauseProgress");
        }

        @Override
        public void onPause(MusicData musicData) {
            setIsPause();
            updateMusicDialogList(-1);
        }


        @Override
        public void onSkipToNext() {
            Log.d(TAG, "onSkipToNext");
        }

        @Override
        public void onSkipToPrevious() {
            Log.d(TAG, "onSkipToPrevious");
        }

        @Override
        public void onSeekTo(long pos) {
            Log.d(TAG, "onSeekTo：" + pos);
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {

        }

        @Override
        public void onProgressChnage(long position) {
            updatePopProgressBarAndCurrentPlayTime(position);
        }

        @Override
        public void onCacheProgressChnage(int position) {
            updateCacheProgress(position);
        }

        @Override
        public void onPrepared(int position) {
            updateMusicViewState(position);
        }

        @Override
        public void onMsgTip(String msg) {
            Toast.makeText(MusicActivity.this, "" + msg, Toast.LENGTH_SHORT).show();
        }
    };

    private void updateMusicDialogList(int position) {
        if (dialogMenuPair == null) {
            return;
        }

        adapterDialog.setPlayposition(position);
        adapterDialog.notifyDataSetChanged();
    }

    public void queryMusicInfo(final int id) {
    }


    /**
     * 更新 标题 总时间 头像 ， 通常在切换下一首播放或者 页面刚打开的时候进行
     *
     * @param position
     */

    private void updateMusicViewState(int position) {

        MusicData currentModel = mPlaybinder.getCurrentModel(position);
        if (currentModel == null) {
            return;
        }
        if (currentModel instanceof ShowModelI) {
            ShowModelI model = (ShowModelI) currentModel;
            queryMusicInfo(model.getMusicId());
        }
        updateMusicDialogList(position);
        if (currentModel instanceof ShowModelI) {
            ShowModelI model = (ShowModelI) currentModel;
            updateTitle(model.getMusicTitle());

        } else {
        }
        updateTotalTime(mPlaybinder.getDuration());
        updatePopProgressBarAndCurrentPlayTime(mPlaybinder.getCurrentDuration());


    }


    private Dialog alertDialog;
    PlayActionCallBack mPlayActionCallBack = new PlayActionCallBack() {
        @Override
        public int getTotalTime() {
            return mPlaybinder.getDuration();
        }

        @Override
        public MusicData getCurrentMusicData() {
            return mPlaybinder.getCurrentModel();
        }

        @Override
        public void setPlayMode(PlayService.PLAYMODE mode) {
            mPlaybinder.setPlayMode(mode);
        }

        @Override
        public void seekTo(int duration) {
            mPlaybinder.seekTo(duration);
        }

        @Override
        public boolean playNext() {
            return mPlaybinder.playNext();
        }

        @Override
        public boolean playPre() {
            return mPlaybinder.playPre();
        }

        @Override
        public void pauseOrPlay() {
            mPlaybinder.playOrPause();
        }

        @Override
        public PlayService.PLAYMODE getPlayMode() {
            return mPlaybinder.getPlayMode();
        }


        @Override
        public boolean isPlay() {
            return mPlaybinder.isPlaying();
        }
    };


}

