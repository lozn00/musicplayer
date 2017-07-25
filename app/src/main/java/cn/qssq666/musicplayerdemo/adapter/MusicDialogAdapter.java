package cn.qssq666.musicplayerdemo.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import cn.qssq666.musicplayerdemo.AppContext;
import cn.qssq666.musicplayerdemo.BR;
import cn.qssq666.musicplayerdemo.R;
import cn.qssq666.musicplayerdemo.interfaces.ShowModelI;
import cn.qssq666.musicplayerdemo.viewholder.DataBindViewHolder;


/**
 * Created by luozheng on 16/1/8.
 */
public class MusicDialogAdapter extends BaseRecyclervdapter<ShowModelI, DataBindViewHolder> {
    public int getPlayposition() {
        return playposition;
    }

    public void setPlayposition(int playposition) {
        this.playposition = playposition;
    }

    private int playposition = -1;

    @Override
    public DataBindViewHolder onCreateViewHolderByExtend(ViewGroup parent, int viewType) {
        ViewDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(AppContext.getInstance()), viewType, parent, false);
        DataBindViewHolder dataBindViewHolder = new DataBindViewHolder(dataBinding.getRoot());
        dataBindViewHolder.setBinding(dataBinding);
        return dataBindViewHolder;


    }

    @Override
    public void onBindViewHolder(DataBindViewHolder holder, final int position) {
//                holder.iv
        final ShowModelI model = getData().get(position);
        holder.getBinding().setVariable(BR.musicModel, model);
        holder.getBinding().setVariable(BR.index, position);
        holder.getBinding().setVariable(BR.isplay, playposition == position);
        holder.getBinding().executePendingBindings();
/*        ((ViewItemOnlineMusicDataBinding) holder.getBinding()).ivMusicPlayPause.setOnClickListener(new View.OnClickListener() {
                                                                                                       @Override
                                                                                                       public void onClick(View v) {
                                                                                                           if (onMusicItemClickListener != null) {
                                                                                                               onMusicItemClickListener.onItemClick(null, v, position, model.getId());
                                                                                                           }
                                                                                                       }
                                                                                                   }
        );*/
    }

/*
    public void setOnMusicItemClickListener(AdapterView.OnItemClickListener onMusicItemClickListener) {
        this.onMusicItemClickListener = onMusicItemClickListener;
    }
*/


//    AdapterView.OnItemClickListener onMusicItemClickListener = null;

    @Override
    public int getItemViewType(int position) {
        return playposition == position ? R.layout.view_item_music_menu_current : R.layout.view_item_music_menu;
    }
}
