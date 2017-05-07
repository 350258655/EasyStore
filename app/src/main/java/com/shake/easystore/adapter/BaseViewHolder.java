package com.shake.easystore.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by shake on 2017/5/6 0006.
 * 封装的ViewHolder
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {

    //可以把它理解为一个map, 是用来存放列表项中的每一个元素
    private SparseArray<View> mViews;

    public BaseViewHolder(final View itemView, final BaseAdapter.OnItemClickListener onItemClickListener) {

        super(itemView);
        this.mViews = new SparseArray<>();

        /**
         * 当ItemView这整个列表项被点击的时候，在回调方法中有传入那个具体被点击到的View，我们的
         * 关键也就是监听到这个View被点击到了，然后回调给外面，至于外面要做什么事情，那就是外面的事了
         */
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    onItemClickListener.onItemClick(v,getLayoutPosition());
                }
            }
        });


    }



    /**
     * 从ItemView中寻找每一个View
     *
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View> T findView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }


}
