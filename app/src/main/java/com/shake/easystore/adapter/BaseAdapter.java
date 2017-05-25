package com.shake.easystore.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Iterator;
import java.util.List;

/**
 * Created by shake on 2017/5/6 0006.
 * 类型参数，代表是 "T" 数据类型的参数，但其实这种做法并不兼容HomeFragment中的RecyclerView。因为它每一项中都还有子View
 * 而这里是做法只能监听到每个列表项，即每一个ItemView，没法监听到里面的子View。。。
 */
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

    //数据的引用
    protected List<T> mListDatas;
    //布局ID
    protected int mLayoutResId;
    //布局解析器
    private LayoutInflater mInflater;

    private Context mContext;

    private BaseViewHolder mBaseViewHolder;

    /**
     * 回调接口设置，第二步，创建回调实例
     */
    OnItemClickListener mOnItemClickListener;


    public BaseAdapter(Context context, List<T> datas) {
        this(context, 0, datas);
    }

    public BaseAdapter(Context context, int layoutResId, List<T> datas) {
        this.mContext = context;
        this.mLayoutResId = layoutResId;
        this.mListDatas = datas;
        this.mInflater = LayoutInflater.from(mContext);
    }


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // 从布局文件中解析出View
        View view = mInflater.inflate(mLayoutResId, parent, false);
        // 创建出ViewHolder
        mBaseViewHolder = new BaseViewHolder(view, mOnItemClickListener);
        return mBaseViewHolder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        //获取对应列表项的数据
        T item = mListDatas.get(position);
        //绑定数据。留给外面去实现
        bindData(holder, item, position);
    }

    @Override
    public int getItemCount() {
        if (mListDatas == null || mListDatas.size() <= 0)
            return 0;
        return mListDatas.size();
    }


    /**
     * 可以这么理解，抽象方法，就是需要留给外面去实现的
     */
    public abstract void bindData(BaseViewHolder holder, T t, int position);


    /**
     * 回调接口设置，第一步，创建回调接口
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }


    /**
     * 回调接口设置，第三步，暴露接口
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }


    public T getItem(int position) {
        if (position >= mListDatas.size()) {
            return null;
        }
        return mListDatas.get(position);
    }


    /**
     * 清除数据
     */
    public void clearData() {
//        int itemCount = mListDatas.size();
//        mListDatas.clear();
//        this.notifyItemRangeRemoved(0, itemCount);

        if(mListDatas == null || mListDatas.size() <= 0){
            return;
        }
        Iterator it = mListDatas.iterator();

        while (it.hasNext()){
            T t = (T) it.next();
            int position = mListDatas.indexOf(t);
            it.remove();
            notifyItemRemoved(position);
        }

    }


    /**
     * 获取数据
     *
     * @return
     */
    public List<T> getDatas() {
        return mListDatas;
    }


    /**
     * 添加数据
     *
     * @param datas
     */
    public void addData(List<T> datas) {
        addData(0, datas);
    }

    /**
     * 添加数据
     *
     * @param position
     * @param datas
     */
    public void addData(int position, List<T> datas) {
        if (datas != null && datas.size() > 0) {
            this.mListDatas.addAll(datas);
            this.notifyItemRangeChanged(position, datas.size());
        }
    }


    /**
     * 刷新数据
     *
     * @param lists
     */
    public void refreshData(List<T> lists) {

        //先清除数据
        clearData();

        if (lists != null && lists.size() > 0) {

            int size = lists.size();
            //遍历添加数据
            for (int i = 0; i < size; i++) {
                mListDatas.add(i,lists.get(i));
                //更新对应的Item
                notifyItemInserted(i);
            }

        }
    }



    /**
     * 加载更多数据
     *
     * @param list
     */
    public void loadMoreData(List<T> list) {
        if (list != null && list.size() > 0) {
            int size = list.size();
            //从这个位置开始添加数据
            int begin = mListDatas.size();
            for (int i = 0; i < size; i++) {
                mListDatas.add(list.get(i));
                notifyItemInserted(i + begin);
            }
        }
    }


}
