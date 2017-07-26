/*
 *
 *                     .::::.
 *                   .::::::::.
 *                  :::::::::::  by qssq666@foxmail.com
 *              ..:::::::::::'
 *            '::::::::::::'
 *              .::::::::::
 *         '::::::::::::::..
 *              ..::::::::::::.
 *            ``::::::::::::::::
 *             ::::``:::::::::'        .:::.
 *            ::::'   ':::::'       .::::::::.
 *          .::::'      ::::     .:::::::'::::.
 *         .:::'       :::::  .:::::::::' ':::::.
 *        .::'        :::::.:::::::::'      ':::::.
 *       .::'         ::::::::::::::'         ``::::.
 *   ...:::           ::::::::::::'              ``::.
 *  ```` ':.          ':::::::::'                  ::::..
 *                     '.:::::'                    ':'````..
 *
 */

/*
 *
 *                     .::::.
 *                   .::::::::.
 *                  :::::::::::  by qssq666@foxmail.com
 *              ..:::::::::::'
 *            '::::::::::::'
 *              .::::::::::
 *         '::::::::::::::..
 *              ..::::::::::::.
 *            ``::::::::::::::::
 *             ::::``:::::::::'        .:::.
 *            ::::'   ':::::'       .::::::::.
 *          .::::'      ::::     .:::::::'::::.
 *         .:::'       :::::  .:::::::::' ':::::.
 *        .::'        :::::.:::::::::'      ':::::.
 *       .::'         ::::::::::::::'         ``::::.
 *   ...:::           ::::::::::::'              ``::.
 *  ```` ':.          ':::::::::'                  ::::..
 *                     '.:::::'                    ':'````..
 *
 */

package cn.qssq666.musicplayerdemo.msic;

import android.os.AsyncTask;

/**
 * Created by luozheng on 2016/11/14.  qssq.space
 */

public class QssqTask<T> extends AsyncTask<Object, Object, T> {
    public QssqTask(ICallBack iCallBack) {
        this.iCallBack = iCallBack;
    }

    public ICallBack<T> getCallBack() {
        return iCallBack;
    }

    ICallBack<T> iCallBack;

    @Override
    protected T doInBackground(Object... params) {
        return iCallBack.onRunBackgroundThread();
    }

    public static abstract class ICallBackImp<T> implements ICallBack<T> {
        public QssqTask<T> getQssqTask() {
            return task;
        }

        QssqTask<T> task = null;

        public void onProgressUpdate(Object o) {

        }

        public void onSetQssqTask(QssqTask taskFix) {
            task = taskFix;
        }
    }

    public void publishProgressProxy(Object o) {
        publishProgress(o);
//        progressBar.setProgress(values[0]); //设置进度条进度值
//        textView.append("当前进度值:" + values[0] + "\n");
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        if (iCallBack != null) {
            iCallBack.onProgressUpdate(values[0]);
        }
    }

    @Override
    protected void onPreExecute() {
        iCallBack.onSetQssqTask(this);
    }

    public interface ICallBack<T> {
        T onRunBackgroundThread();

        void onRunFinish(T t);

        void onProgressUpdate(Object o);

        void onSetQssqTask(QssqTask taskFix);
    }

    @Override
    protected void onPostExecute(T t) {
        super.onPostExecute(t);
        iCallBack.onRunFinish(t);
    }

    static public void executeTask(ICallBack iCallBackImp) {
        new QssqTask<>(iCallBackImp).execute();
    }
}
