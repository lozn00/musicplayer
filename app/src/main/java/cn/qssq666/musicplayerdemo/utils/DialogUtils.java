package cn.qssq666.musicplayerdemo.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.Field;

import cn.qssq666.musicplayerdemo.AppContext;
import cn.qssq666.musicplayerdemo.R;
import cn.qssq666.musicplayerdemo.interfaces.INotify;

/**
 * Created by luozheng on 15/12/8.
 */
public class DialogUtils {
    public static void delaydismissDialog(final Dialog dialog, String message) {
        delaydismissDialog(dialog, message, null);
    }

    public static void delaydismissDialog(final Dialog dialog, String message, final INotify notify) {
        delaydismissDialog(dialog, message, 1500, notify);
    }

    /**
     * 延迟关闭对话框 也就是说延时小时一个标题 如果是未知对话框 那么 message信息不进行操作
     *
     * @param dialog
     * @param message  支持 的对话框 设置 有  @see {@link ProgressDialog,AlertDialog}
     * @param duration
     */
    public static void delaydismissDialog(final Dialog dialog, String message, long duration) {
        delaydismissDialog(dialog, message, duration, null);
    }

    public static void delaydismissDialog(final Dialog dialog, String message, long duration, final INotify notify) {
        //SingleChoiceDialog
        if (dialog instanceof ProgressDialog) {
            ((ProgressDialog) dialog).setMessage("" + message);
        } else if (dialog instanceof AlertDialog) {
            ((AlertDialog) dialog).setMessage("" + message);
        }
       /* else if (dialog instanceof SingleChoiceDialog) {
            ((SingleChoiceDialog) dialog).setMessage("" + message);
        }*/
   /*     AppContext.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                if (notify != null) {
                    notify.onNotify(null);
                }
            }
        }, duration);*/

    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)  xxx的分辨率是比1920*1080还大 ？xxxhdpi density 1dp=? xxhdpi =480密度 =1920*1080 =1dp=3px xxxdpi=2048*1536 2560*1536 2560*1600
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;//density=dpi/160
        return (int) (dpValue * scale + 0.5f);

            /*


             */
    }
    public static AlertDialog showEditDialog(Context context, String content, final INotify<String> onClickListener) {
        return showEditDialog(context, content, onClickListener, null);
    }

    public static AlertDialog showEditDialog(Context context, String content, final INotify<String> onClickListener, final INotify cancelNotify) {
        final EditText editText = new EditText(context);
        editText.setTextColor(AppContext.getInstance().getResources().getColor(R.color.colorThemeBlack));
        editText.setBackgroundColor(Color.parseColor("#ffffff"));
        int padding = dip2px(context, 8);
        editText.setPadding(padding, padding, padding, padding);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(editText);
//        builder.setTitle(TextUtils.isEmpty(title) ? "温馨提示" : title + "");
        builder.setMessage(content);
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(editText.getText().toString())) {
                    return;
                }
                modifyDialogAttr(dialog, true);
                dialog.dismiss();
                onClickListener.onNotify(editText.getText().toString());
            }
        });
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                modifyDialogAttr(dialog, true);
                if (cancelNotify != null) {
                    cancelNotify.onNotify(null);
                }
                dialog.dismiss();
            }
        });
        AlertDialog show = builder.show();
        modifyDialogAttr(show, false);
        return show;
    }


    public static ProgressDialog getProgressDialog(Activity activity, String title, String content, boolean canncelable, DialogInterface.OnCancelListener onCancelListener) {
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setIcon(R.mipmap.ic_launcher);
        progressDialog.setCanceledOnTouchOutside(canncelable);
        progressDialog.setCancelable(canncelable);
        progressDialog.setOnCancelListener(onCancelListener);
        if (title != null) {
            progressDialog.setTitle(title == null ? "温馨提示" : title);

        }
        progressDialog.setMessage((content == null ? "加载中" : content));
        return progressDialog;
    }

    public static ProgressDialog getProgressDialog(Activity activity, String content, boolean canncelable, DialogInterface.OnCancelListener onCancelListener) {
        return getProgressDialog(activity, null, content, canncelable, onCancelListener);
    }

    /**
     * 默认不能取消
     *
     * @param activity
     * @param content
     * @param onCancelListener
     * @return
     */
    public static ProgressDialog getProgressDialog(Activity activity, String content, DialogInterface.OnCancelListener onCancelListener) {
        return getProgressDialog(activity, null, content, false, onCancelListener);
    }

    public static ProgressDialog getProgressDialog(Activity activity, String content) {
        return getProgressDialog(activity, null, content, false, null);
    }

    /**
     * 默认不能取消
     *
     * @param activity
     * @return
     */
    public static ProgressDialog getProgressDialog(Activity activity) {
        return getProgressDialog(activity, null, null, false, null);
    }

    /**
     * @param dialog
     * @param close  能dis不? true表示不饿能够 也是
     */
    public static void modifyDialogAttr(DialogInterface dialog, boolean close) {
        try {
            //不关闭
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, close);//false 则是不关闭
//            if(close){
//                dialog.cancel();
//            }


        } catch (Exception e) {
        }


    }


    public static void showToast(Context context, String str) {
        showToast(context, str, Toast.LENGTH_SHORT);
    }

    public static void showToast(String str) {
        showToast(AppContext.getInstance(), str, Toast.LENGTH_SHORT);
    }

    public static void showLongToast(String str) {
        showToast(AppContext.getInstance(), str, Toast.LENGTH_LONG);
    }

    public static void showToast(Context context, String str, int value) {
        Toast.makeText(context, str, value).show();
    }

    public static AlertDialog showDialog(Context context, String content) {
        return showDialog(context, content, null);
    }

    public static AlertDialog showDialog(Context context, String content, String title) {
        return showDialog(context, content, title, null);
    }

    public static void showSystemDialog(Context context, String content, String title, DialogInterface.OnClickListener onClickListener) {
//        final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null) {
            builder.setTitle(TextUtils.isEmpty(title) ? "温馨提示" : title + "");

        }
        builder.setIcon(R.mipmap.ic_launcher);
        if (content != null) {
            builder.setMessage(content);

        }
        builder.setNegativeButton("确定", onClickListener);
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
//        WindowManager.LayoutParams para = new WindowManager.LayoutParams();
//        para.height = -1;
//        para.width = -1;
//        para.format = 1;
//        para.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
//        para.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        AlertDialog alertDialog = builder.create();
//        alertDialog.getWindow().getDecorView().setLayoutParams(para);
//        builder.updateTitle(TextUtils.isEmpty(title) ? "温馨提示" : title + "");
//        builder.setIcon(R.drawable.ic_launcher);
//        builder.setMessage(content);
//        builder.setNegativeButton("确定", onClickListener);
//        builder.createPlayStationAndSHow();
    }

    public static AlertDialog showDialog(Context context, String content, String title, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = getDialog(context, content, title, onClickListener);
        return builder.show();
    }

    public static AlertDialog.Builder getDialog(Context context, String content, String title, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null) {
            builder.setTitle(TextUtils.isEmpty(title) ? "温馨提示" : title + "");

        }
        builder.setIcon(R.mipmap.ic_launcher);
        if (content != null) {
            builder.setMessage(content);

        }
        builder.setNegativeButton("确定", onClickListener);
        return builder;
    }


    /**
     * 只有确定回调的对话框
     *
     * @param context
     * @param content
     * @param title
     * @param iNotify
     */

    /**
     * @param context
     * @param content
     * @param title      标题为null则为温馨提示 为 “"为信息
     * @param yesiNotify 确定回调
     * @param iNotify    取消回调
     */


    /**
     * @param context
     * @param content
     * @param yesiNotify 确定按钮的回调
     */
    public static AlertDialog showConfirmDialog(Context context, String content, final INotify<Void> yesiNotify) {
        return showConfirmDialog(context, content, yesiNotify, null);
    }

    /**
     * @param context
     * @param content
     */
    public static AlertDialog showConfirmDialog(Context context, String content, final INotify<Void> yesiNotify, final INotify<Void> noiNotify) {
        return showConfirmDialog(context, content, null, yesiNotify, noiNotify);
    }

    /**
     * @param context
     * @param content
     * @param title      标题为null则为温馨提示 为 “"为信息
     * @param yesiNotify 确定回调
     * @param iNotify    取消回调
     */
    public static AlertDialog showConfirmDialog(Context context, String content, String title, final INotify<Void> yesiNotify, final INotify<Void> iNotify) {
        AlertDialog yesOrNoDialog = getYesOrNoDialog(context, content, title, yesiNotify, iNotify);

        yesOrNoDialog.show();
        return yesOrNoDialog;
    }

    public static AlertDialog showConfirmDialog(Context context, String content, String yesBtn, String noBtn, final INotify<Void> yesiNotify) {
        AlertDialog yesOrNoDialog = getYesOrNoDialog(context, content, null, yesBtn, noBtn, yesiNotify, null);
        yesOrNoDialog.show();
        return yesOrNoDialog;
    }

    public static AlertDialog showConfirmDialog(Context context, String content, String title, String yesBtn, String noBtn, final INotify<Void> yesiNotify, final INotify<Void> noiNotify) {
        AlertDialog yesOrNoDialog = getYesOrNoDialog(context, content, title, yesBtn, noBtn, yesiNotify, noiNotify);
        yesOrNoDialog.show();
        return yesOrNoDialog;
    }

    public static AlertDialog showConfirmDialog(Context context, String content, String yesBtn, String noBtn, final INotify<Void> yesiNotify, final INotify<Void> notify) {
        AlertDialog yesOrNoDialog = getYesOrNoDialog(context, content, null, yesBtn, noBtn, yesiNotify, notify);
        yesOrNoDialog.show();
        return yesOrNoDialog;
    }

    public static void showYesOrSystemNoDialog(Context context, String content, final INotify<Void> yesiNotify, final INotify<Void> noiNotify) {
        AlertDialog yesOrNoDialog = getYesOrNoDialog(context, content, null, yesiNotify, noiNotify);
        yesOrNoDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        yesOrNoDialog.show();
    }


    public static AlertDialog getYesOrNoDialog(Context context, String content, String title, String yesBtn, String noBtn, final INotify<Void> yesiNotify, final INotify<Void> noiNotify) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        if (title != null) {
            builder.setTitle(TextUtils.isEmpty(title) ? "温馨提示" : title + "");

        }
//        builder.setTitle(TextUtils.isEmpty(title) || "".equals(title) ? AppContext.getInstance().getString(R.string.Prompt)  : title + "");
        builder.setIcon(R.mipmap.ic_launcher);
        if (content != null) {
            builder.setMessage(content);

        }
        builder.setPositiveButton(noBtn == null ? AppContext.getInstance().getResources().getString(android.R.string.cancel) : noBtn, noiNotify == null ? null : new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                noiNotify.onNotify(null);
            }
        });
        builder.setNegativeButton(yesBtn == null ? AppContext.getInstance().getResources().getString(android.R.string.ok) : yesBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                yesiNotify.onNotify(null);
            }
        });
        AlertDialog alertDialog = builder.create();
        return alertDialog;

    }


    public static AlertDialog getYesOrNoDialog(Context context, String content, String title, final INotify<Void> yesiNotify, final INotify<Void> noiNotify) {
        return getYesOrNoDialog(context, content, title, null, null, yesiNotify, noiNotify);

    }


  /*  *//**
     * 随机产生订单号
     *
     * @return
     *//*
    public static String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
                Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }*/

}
