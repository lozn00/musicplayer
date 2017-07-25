package cn.qssq666.musicplayerdemo;

import android.app.Activity;
import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import cn.qssq666.musicplayerdemo.adapter.BaseRecyclervdapter;
import cn.qssq666.musicplayerdemo.databinding.DialogMusicListBinding;
import cn.qssq666.musicplayerdemo.ui.ListDividerItemDecoration;

/**
 * Created by qssq on 2017/7/25 qssq666@foxmail.com
 */

class ActionEngine {
    public static Pair<DialogMusicListBinding, Dialog> generateMusicMenuDialog(final Activity activity, String mode, BaseRecyclervdapter adapter) {
        final Dialog dialog = new Dialog(activity, R.style.dialog_bottom_show);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        DialogMusicListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.dialog_music_list, null, false);
        View rootView = binding.getRoot();
        dialog.setContentView(rootView);//这样可以解决布局没有填充问题
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = LinearLayout.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
        binding.recyclerview.addItemDecoration(new ListDividerItemDecoration(AppContext.getInstance(), R.drawable.shape_divider));//HORIZONTAL_LIST
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AppContext.getInstance());
        binding.recyclerview.setLayoutManager(linearLayoutManager);
        binding.recyclerview.setAdapter(adapter);

        binding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return Pair.create(binding, dialog);


    }
}
