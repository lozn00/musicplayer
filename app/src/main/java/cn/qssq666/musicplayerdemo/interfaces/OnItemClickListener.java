package cn.qssq666.musicplayerdemo.interfaces;

import android.view.View;

/**
 * Created by luozheng on 2016/4/5.  qssq.space
 */
public interface OnItemClickListener<VH> {

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param obj
     * @param view The view within the AdapterView that was clicked (this
     *            will be MyInputConnectionWrapper view provided by the adapter)
     * @param position The position of the view in the adapter.
     */
    void onItemClick(VH obj, View view, int position);
}