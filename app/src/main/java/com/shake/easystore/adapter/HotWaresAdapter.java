package com.shake.easystore.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shake.easystore.R;
import com.shake.easystore.bean.Wares;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by shake on 17-5-5.
 * 热门商品的Adapter
 */
public class HotWaresAdapter extends RecyclerView.Adapter<HotWaresAdapter.ViewHolder> {

    private List<Wares> mWares;

    private Context mContext;

    public HotWaresAdapter(List<Wares> wares, Context context) {
        this.mWares = wares;
        this.mContext = context;
    }

    LayoutInflater mInflater;


    /**
     * 第二步，根据布局，创建ViewHolder,并且return出去
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mInflater = LayoutInflater.from(parent.getContext());
        View view = mInflater.inflate(R.layout.template_hot_wares, parent, false);
        return new ViewHolder(view);
    }


    /**
     * 第三步，绑定ViewHolder，即将数据和ViewHolder上的控件绑定
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //获取数据实体
        Wares wares = mWares.get(position);

        holder.textTitle.setText(wares.getName());
        holder.textPrice.setText("￥" + wares.getPrice());

        //用Picasso加载图片
        Picasso.with(mContext).load(wares.getImgUrl()).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mWares.size();
    }


    /**
     * 第一步，封装一个ViewHolder。其实就是把一个列表项中的每一个元素封装起来
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView textTitle;
        TextView textPrice;

        public ViewHolder(View itemView) {
            super(itemView);

            mImageView = (ImageView) itemView.findViewById(R.id.drawee_view);
            textTitle = (TextView) itemView.findViewById(R.id.text_title);
            textPrice = (TextView) itemView.findViewById(R.id.text_price);
        }
    }


    /**
     * 清除数据
     */
    public void clearData() {
        mWares.clear();
        notifyItemRangeRemoved(0, mWares.size());
    }

    /**
     * 添加数据
     *
     * @param datas
     */
    public void addData(List<Wares> datas) {
        addData(0, datas);
    }

    /**
     * 在特定位置添加数据
     *
     * @param position
     * @param datas
     */
    public void addData(int position, List<Wares> datas) {
        if (datas != null && datas.size() > 0) {
            mWares.addAll(datas);
            notifyItemRangeChanged(position, mWares.size());
        }
    }


    public List<Wares> getDatas(){
        return mWares;
    }

}
