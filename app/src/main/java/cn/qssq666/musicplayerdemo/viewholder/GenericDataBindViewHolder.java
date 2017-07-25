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

package cn.qssq666.musicplayerdemo.viewholder;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;


/**
 * Created by 情随事迁(qssq666@foxmail.com) on 2017/2/9.
 */

public class GenericDataBindViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder   {
    private T binding;

    /**
     * `@Deprecated 如果用户直接从这里访问那么可能导致用户忘记什么databiing
     */
    public GenericDataBindViewHolder(View itemView) {
        super(itemView);
        this.binding = DataBindingUtil.findBinding(itemView);
    }

    public GenericDataBindViewHolder(T binding) {
        super(binding.getRoot());
        this.binding = binding;

    }

    public void setBinding(T binding) {
        this.binding = binding;
    }

    public T getBinding() {
        return this.binding;
    }

    public View getItemView() {
        return binding.getRoot();
    }
}
